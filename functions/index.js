/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const myFunctions = require('./functions');
const admin = require('firebase-admin');
admin.initializeApp();
const bucket = admin.storage().bucket();

// Export each function
exports.getLatestImage = myFunctions.getLatestImage;

