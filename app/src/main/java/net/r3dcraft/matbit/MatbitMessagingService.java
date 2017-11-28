package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 15.11.2017.
 *
 * This is used to receive notifications sent by me from Google Firebase.
 *
 * I created this by following this tutorial:
 * https://www.codementor.io/flame3/send-push-notifications-to-android-with-firebase-du10860kb
 */

public class MatbitMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MatbitMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }
}