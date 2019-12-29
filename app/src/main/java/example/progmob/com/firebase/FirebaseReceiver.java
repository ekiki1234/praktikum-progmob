package example.progmob.com.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import androidx.core.app.NotificationCompat;

import example.progmob.com.LoginActivity;
import example.progmob.com.R;
import example.progmob.com.SplashScreen;

public class FirebaseReceiver  extends FirebaseMessagingService{

    SharedPreferences sharedpreferences;
    public static final String TAG_ID = "id";
    String id,haha1;
    String bram="bramcool";
    String cool="bramsss";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //handle when receive notification via data event
        sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
//        haha1=;
        id = sharedpreferences.getString(TAG_ID, null);
        Log.d("haha",id);
        Log.d("ini",remoteMessage.getNotification().getTitle());
        if(remoteMessage.getData().size()>0){
            if(remoteMessage.getData().get("title").equals(id)){
//                showNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));
                showNotification(bram,cool);
            }
        }

        //handle when receive notification
        if(remoteMessage.getNotification()!=null){
            if(remoteMessage.getNotification().getTitle().equals(id)){
                showNotification(bram,cool);
            }

        }

    }

    private RemoteViews getCustomDesign(String title,String message){
        RemoteViews remoteViews=new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.message,message);
        remoteViews.setImageViewResource(R.id.icon,R.drawable.logo);
        return remoteViews;
    }

    public void showNotification(String title,String message){
        Intent intent=new Intent(this, SplashScreen.class);
        String channel_id="web_app_channel";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),channel_id)
                .setSmallIcon(R.drawable.logo)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            builder=builder.setContent(getCustomDesign(title,message));
        }
//        else{
//            builder=builder.setContentTitle(title)
//                    .setContentText(message)
//                    .setSmallIcon(R.drawable.web_hi_res_512);
//        }

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(channel_id,"web_app",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri,null);
            notificationChannel.setDescription(message);
            notificationChannel.setName(title);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        notificationManager.notify(0,builder.build());
    }

    //app part ready now let see how to send differnet users
    //like send to specific device
    //like specifi topic

}
