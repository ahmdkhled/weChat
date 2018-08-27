'use-strict'

const functions = require('firebase-functions');
const admin=require('firebase-admin');
admin.initializeApp();


exports.sendNotification=functions.database.ref('/notifications/{userId}/{notificationId}')
.onCreate((snapshot, context) => {

 	const senderUid = snapshot.val().userUid;
 	const type = snapshot.val().type;
    const userId = context.params.userId;

    if(type==="post comment"){
        var postUid = snapshot.val().target.postUid;
        var commentUid = snapshot.val().target.commentUid;
    }
    const notificationId = context.params.notificationId;



    return admin.database().ref('/users/' + userId).once('value')
    .then((snapshot)=> {
        const token = (snapshot.val() && snapshot.val().notification_token) || 'Anonymous';

        return admin.database().ref('/users/' + senderUid).once('value')
            .then((snapshot)=> {
                const name = (snapshot.val() && snapshot.val().name) || 'Anonymous';
                var payload={};
                var bodyContent="";

                if (type==="post comment") {
                     const bodyContent=name+" has commented on your post "
                     payload = {
                        notification: {
                            title: "comment notification",
                            body: bodyContent,
                            sound: "default",
                            click_action: "com.ahmdkhled.wechat.activities.MainActivity"
                        },data:{
                             postUid: postUid,
                             notificationType:type
                        }
                    };
                }
                else if (type ==="sent request"){
                    const bodyContent=name+" sent you friend request "
                    payload = {
                        notification: {
                            title: "friend request",
                            body: bodyContent,
                            sound: "default",
                            click_action: "com.ahmdkhled.wechat.activities.MainActivity"
                        },
                        data:{
                            notificationType:type
                        }
                    };
                }


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
