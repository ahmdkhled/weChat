'use-strict'

const functions = require('firebase-functions');
const admin=require('firebase-admin');
admin.initializeApp();


exports.sendNotification=functions.database.ref('/notifications/{userId}/{notificationId}')
.onCreate((snapshot, context) => {

 	const senderUid = snapshot.val().userUid;
 	const type = snapshot.val().type;
 	const postUid = snapshot.val().target.postUid;
    const userId = context.params.userId;
    const notificationId = context.params.notificationId;

    var bodyContent="";

    return admin.database().ref('/users/' + userId).once('value')
    .then((snapshot)=> {
        const token = (snapshot.val() && snapshot.val().notification_token) || 'Anonymous';

        return admin.database().ref('/users/' + senderUid).once('value')
            .then((snapshot)=> {
                const name = (snapshot.val() && snapshot.val().name) || 'Anonymous';

                if (type==="post comment") {
                    bodyContent=name+" has commented on your post "
                }

                const payload = {
                    notification: {
                        title: "comment notification",
                        body: bodyContent
                    },data:{
                        postUid: postUid
                    }
                };
                console.log('bodyContent : '+bodyContent);

                return admin.messaging().sendToDevice(token,payload)
                    .then((response) => {
                        //console.log('Successfully sent message:', response);
                        return response;
                    }).catch((error)=> {
                        console.log('Error sending message:', error);
                    });
            });


  
 		
   }).catch((error)=> {
    console.log('Error reading token ', error);
  });




 
});
