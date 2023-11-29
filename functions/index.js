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
const sendVitalsDataEmail = require('./exportData')
const admin = require('firebase-admin');
require('dotenv').config();
admin.initializeApp();

// Export each function
exports.getLatestImage = myFunctions.getLatestImage;
exports.submitFeedback = myFunctions.submitFeedback;
exports.checkLastSubmission = myFunctions.checkLastSubmission;
exports.sendVitalsDataEmail = sendVitalsDataEmail.sendVitalsDataEmail;