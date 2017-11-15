package net.r3dcraft.matbit;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 22.10.2017.
 */

class UploadUserPhoto extends AsyncTask<Uri, Void, Void> {

    private final static String TAG = "UploadUserPhoto";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // do something
    }

    @Override
    protected Void doInBackground(Uri... uris) {
        StorageReference reference = MatbitDatabase.getUserPhoto(MatbitDatabase.getCurrentUserUID());

        for (int i  = 0; i < uris.length; i++) {
            try {
                URL url = new URL(uris[i].toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                UploadTask uploadTask = reference.putStream(input);
                ;
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.v(TAG, "Failed to upload img_thumbnail");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG, "Failed to upload img_thumbnail");
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void v) {
        // do something
    }
}
