const functions = require('firebase-functions');
const admin = require('firebase-admin');
const logger = require('firebase-functions/logger')

exports.getLatestImage = functions.https.onCall(async (data, context) => {
  // Ensure the user is authenticated
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'The function must be called while authenticated.');
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
    files.sort((a, b) => b.metadata.timeCreated.localeCompare(a.metadata.timeCreated));
    const latestFile = files[0];

    // Get the signed URL for the most recent file
    const [url] = await latestFile.getSignedUrl({
      action: 'read',
      expires: '03-09-2491' // far future date or a reasonable expiration time
    });

    logger.log("Returning file URL:", url);
    return { url };
  } catch (error) {
    logger.error('Error getting files:', error);
    throw new functions.https.HttpsError('internal', 'Unable to get files.');
  }
});