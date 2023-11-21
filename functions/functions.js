const functions = require('firebase-functions');
const admin = require('firebase-admin');

exports.getLatestImage = functions.https.onCall((data, context) => {
    if (!context.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new functions.https.HttpsError('unauthenticated', 'The function must be called ' +
        'while authenticated.');
  }
    const userId = context.auth.uid;
   const bucket = admin.storage().bucket();
   const userFolder = `profileImages/${userId}/`;

   return bucket.getFiles({ prefix: userFolder })
     .then(results => {
       const files = results[0];
       if (files.length === 0) {
         return { ref: null };
       }

       // Sort files by upload date
       files.sort((a, b) => b.metadata.timeCreated.localeCompare(a.metadata.timeCreated));

       // Get the most recent file
       const latestFile = files[0];
       const reference = `gs://${bucket.name}/${latestFile.name}`;

       // Return a reference to the most recent file
       return { ref: reference };
     })
     .catch(error => {
       console.error('Error getting files:', error);
       throw new functions.https.HttpsError('internal', 'Unable to get files.');
     });
});