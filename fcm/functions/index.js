'use-strict'

const functions = require('firebase-functions');
const admin=require('firebase-admin');
admin.initializeApp();


exports.sendNotification=functions.database.ref('/notifications/{userId}/{notificationId}')
.onWrite((change, context) => {
 
    const userId = context.params.userId;
    const notificationId = context.params.notificationId;



    admin.database().ref('/users/' + userId).once('value')
    .then((snapshot)=> {
  	var token = (snapshot.val() && snapshot.val().notification_token) || 'Anonymous';


  	console.log('userId : '+userId+ ' token : ', token);

  	const payload = {

                notification: {
                    title: "title",
                    body: "bodyy"
                }
};

    return admin.messaging().sendToDevice(token,payload)
  .then((response) => {
    console.log('Successfully sent message:', response);
    return response;
  }).catch((error)=> {
    console.log('Error sending message:', error);
  });
  
 		
   }).catch((error)=> {
    console.log('Error reading token ', error);
  });




 
});
