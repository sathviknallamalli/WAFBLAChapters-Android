const functions = require("firebase-functions");

exports.sendNotification = functions.database.ref('/Chapters/{chapID}/Users/{userID}/Notifications')
    .onWrite(async (change, context) => {
      const chapID = context.params.chapID;
      const userID = context.params.userID;
    

      // Get the list of device notification tokens.
      const getDeviceTokensPromise = admin.database()
          .ref(`/Chapters/${chapID}/Users/${userID}/device_token`).once('value');

      

      const results = await Promise.all([getDeviceTokensPromise]);
      const tokensSnapshot = results[0];

      // Notification details.
      const payload = {
        notification: {
          title: 'You have a new follower!',
          body: `ghirhgrihgrhgi`,
        }
      };

    
      const response = await admin.messaging().sendToDevice(tokensSnapshot, payload);
  
    });
