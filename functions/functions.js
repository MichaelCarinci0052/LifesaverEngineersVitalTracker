const functions = require("firebase-functions");
const admin = require("firebase-admin");
const logger = require("firebase-functions/logger");
const moment = require("moment-timezone");

function toESTISOString(date) {
	// Convert the date to the 'America/New_York' time zone and format as ISO string
	return moment(date).tz("America/New_York").format();
}

exports.getLatestImage = functions.https.onCall(async (data, context) => {
	// Ensure the user is authenticated
	if (!context.auth) {
		throw new functions.https.HttpsError(
			"unauthenticated",
			"The function must be called while authenticated."
		);
	}

	const userId = context.auth.uid;
	const bucket = admin.storage().bucket();
	const userFolder = `profileImages/${userId}/`;

	try {
		const [files] = await bucket.getFiles({ prefix: userFolder });
		if (files.length === 0) {
			logger.log("No files found for user:", userId);
			return { url: null };
		}

		// Sort files by upload date
		files.sort((a, b) =>
			b.metadata.timeCreated.localeCompare(a.metadata.timeCreated)
		);
		const latestFile = files[0];

		// Get the signed URL for the most recent file
		const [url] = await latestFile.getSignedUrl({
			action: "read",
			expires: "03-09-2491", // far future date or a reasonable expiration time
		});

		logger.log("Returning file URL:", url);
		return { url };
	} catch (error) {
		logger.error("Error getting files:", error);
		throw new functions.https.HttpsError("internal", "Unable to get files.");
	}
});

exports.submitFeedback = functions.https.onCall(async (data, context) => {
	// Ensure the user is authenticated
	if (!context.auth) {
		throw new functions.https.HttpsError(
			"unauthenticated",
			"The function must be called while authenticated."
		);
	}
	const userId = context.auth.uid;
	const db = admin.firestore();
	const feedbackRef = db
		.collection("userId")
		.doc(userId)
		.collection("feedback");

	// Check for last feedback timestamp
	const lastFeedback = await feedbackRef
		.orderBy("submittedAt", "desc")
		.limit(1)
		.get();
	if (!lastFeedback.empty) {
		const lastTimestamp = lastFeedback.docs[0].data().submittedAt;
		const timeDifference = Date.now() - lastTimestamp.toMillis();

		// Check if 24 hours have passed
		if (timeDifference < 24 * 60 * 60 * 1000) {
			throw new functions.https.HttpsError(
				"failed-precondition",
				"You can only submit feedback once every 24 hours."
			);
		}
	}

	// Add the new feedback
	const newFeedback = {
		first_name: data.first,
		last_name: data.last,
		email: data.email,
		phone_number: data.phone,
		rating: data.rating,
		comment: data.comment,
		phone_Model: data.deviceModel,
		submittedAt: admin.firestore.FieldValue.serverTimestamp(), // Record the submission time
	};

	await feedbackRef.add(newFeedback);

	return { success: true };
});

exports.checkLastSubmission = functions.https.onCall((data, context) => {
	if (!context.auth) {
		throw new functions.https.HttpsError(
			"unauthenticated",
			"The function must be called while authenticated."
		);
	}

	if (!context.auth) {
		throw new functions.https.HttpsError(
			"unauthenticated",
			"The function must be called while authenticated."
		);
	}

	// Get the authenticated user's ID
	const userId = context.auth.uid;

	// Reference to the user's document and feedback sub-collection
	const feedbackRef = admin
		.firestore()
		.collection("userId")
		.doc(userId)
		.collection("feedback");

	return feedbackRef
		.orderBy("submittedAt", "desc")
		.limit(1)
		.get()
		.then((snapshot) => {
			if (snapshot.empty) {
				return { canSubmit: true };
			}

			// Get the last feedback submission time
			const lastFeedback = snapshot.docs[0].data();
			const lastSubmissionTime = lastFeedback.submittedAt.toDate();
			const currentTime = new Date();
			const timeDiff = currentTime - lastSubmissionTime;
			const hoursSinceLastSubmission = timeDiff / (1000 * 60 * 60);

			// Check if 24 hours have passed since the last submission
			return { canSubmit: hoursSinceLastSubmission >= 24 };
		})
		.catch((error) => {
			// Handle errors appropriately
			throw new functions.https.HttpsError(
				"unknown",
				"An error occurred while checking the last submission time.",
				error
			);
		});
});

exports.syncGpstoFirestore = functions.database
	.ref("/users/{userId}/data/{dataId}/GPS")
	.onWrite(async (change, context) => {
		const { userId, dataId } = context.params;

		// Check if the data exists
		if (!change.after.exists()) {
			console.log("GPS data has been deleted, no action necessary.");
			return null;
		}

		const gpsData = change.after.val();

		// Extract the latitude and longitude
		const { Lat, Long } = gpsData;

		// Define the Firestore path where the GPS data will be stored
		const firestorePath = `userId/${userId}/location_data/current_location`;

		// Create a reference to the Firestore document
		const firestoreRef = admin.firestore().doc(firestorePath);

		try {
			// Update the Firestore document with the GPS data
			const firestoreUpdate = { latitude: Lat, longitude: Long };
			await firestoreRef.set(firestoreUpdate);
			console.log(
				`Firestore document at ${firestorePath} updated successfully with the GPS data.`
			);
		} catch (error) {
			console.error("Error updating Firestore document with GPS data:", error);
			// You might want to handle the error differently or rethrow it to retry
			throw error;
		}
	});

exports.syncTemptoFirestore = functions.database
	.ref("/users/{userId}/data/{dataId}")
	.onWrite(async (change, context) => {
		const { userId, dataId } = context.params;
		const date = dataId.substring(0, 8);
		const vitalsSnapshot = change.after.exists() ? change.after.val() : null;
		const estTime = moment().tz("America/New_York").format();
		// Check if the data exists
		if (!change.after.exists()) {
			console.log("TEMP data has been deleted, no action necessary.");
			return null;
		}
		console.log(vitalsSnapshot);
		const newVitals = {
			bodyTemp: vitalsSnapshot.TEMP,
			heartRate: vitalsSnapshot.Heartbeat,
			timestamp: estTime, // Assuming you store the timestamp as an ISO string
		};

		// Define the Firestore path where the vitals data will be stored
		const firestorePath = `userId/${userId}/vitals/${date}`;

		// Create a reference to the Firestore document
		const firestoreRef = admin.firestore().doc(firestorePath);

		try {
			// Use a Firestore transaction to append the new vitals object to the array
			await admin.firestore().runTransaction(async (transaction) => {
				console.log(newVitals);
				const doc = await transaction.get(firestoreRef);
				const currentVitals =
					doc.exists && doc.data().vitalsData ? doc.data().vitalsData : [];

				// Append the new vitals object to the current vitals array
				const updatedVitals = [...currentVitals, newVitals];

				// Set the updated vitals array back to Firestore
				transaction.set(firestoreRef, { vitalsData: updatedVitals });
			});
			console.log(
				`Firestore document at ${firestorePath} updated successfully with the new vitals.`
			);
		} catch (error) {
			console.error(
				"Error updating Firestore document with vitals data:",
				error
			);
			throw error;
		}
	});
