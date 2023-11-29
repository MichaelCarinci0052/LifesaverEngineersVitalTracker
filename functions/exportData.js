const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
const createCsvWriter = require('csv-writer').createObjectCsvWriter;

const zohoUsername = process.env.ZOHO_USERNAME;
const zohoPassword = process.env.ZOHO_PASSWORD;

exports.sendVitalsDataEmail = functions.https.onCall(async (data, context) => {
    // Ensure the user is authenticated
    if (!context.auth) {
        throw new functions.https.HttpsError('unauthenticated', 'The function must be called while authenticated.');
    }



    const uid = context.auth.uid;
    const selectedDate = data.selectedDate; // Date in yyyyMMdd format
    const email = data.email; // User's email address


    // Log the received data
    console.log(`UID: ${uid}, Date: ${selectedDate}, Email: ${email}`);

    // Reference to the user's vitals for the selected date
    const vitalsDataPath = `userId/${uid}/vitals/${selectedDate}`;


    console.log(`Path: ${vitalsDataPath}`);
    try {
        const vitalsDocRef = admin.firestore().doc(vitalsDataPath);
        const doc = await vitalsDocRef.get();
        
        if (!doc.exists) {
            console.log(`No document found at path: ${vitalsDataPath}`);
            throw new functions.https.HttpsError('not-found', 'Document not found at the specified path.');
        }

        const vitalsDataArray = doc.data().vitalsData;

        // Prepare CSV data
        const csvData = vitalsDataArray.map((entry, index) => ({
            time: index, // If you have a timestamp, replace 'index' with the actual timestamp field
            bodyTemp: entry.bodyTemp,
            heartRate: entry.heartRate,
            oxygenLevel: entry.oxygenLevel
        }));

        // CSV Writer setup
        const csvWriter = createCsvWriter({
            path: '/tmp/vitals-data.csv',
            header: [
                {id: 'time', title: 'TIME'},
                {id: 'bodyTemp', title: 'BODY TEMP'},
                {id: 'heartRate', title: 'HEART RATE'},
                {id: 'oxygenLevel', title: 'OXYGEN LEVEL'}
            ]
        });

        await csvWriter.writeRecords(csvData);

        // Configure Nodemailer with your email service details
        const transporter = nodemailer.createTransport({
            host: 'smtp.zohocloud.ca',
            port: 465,
            secure: true, // use SSL
            auth: {
                user: zohoUsername,
                pass: zohoPassword
            }
        });

        const mailOptions = {
            from: zohoUsername,
            to: email,
            subject: 'Your Vitals Data',
            text: 'Attached is your requested vitals data.',
            attachments: [
                {
                    filename: 'vitals-data.csv',
                    path: '/tmp/vitals-data.csv'
                }
            ]
        };

        await transporter.sendMail(mailOptions);

        return { success: true };

    } catch (error) {
        console.error('Error:', error);
        throw new functions.https.HttpsError('unknown', 'An error occurred while processing your request.');
    }
});