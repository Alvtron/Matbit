/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Firebase Authentication using a Google ID Token.
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private Context context;
    private static final int RC_SIGN_IN = 9001;

    private TextView mStatusTextView;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signin);

        context = this;

        // Views
        mStatusTextView = findViewById(R.id.activity_signin_txt_status);
        progressBar = findViewById(R.id.activity_signin_progressbar);

        // Button listeners
        findViewById(R.id.activity_signin_btn_sign_in).setOnClickListener(this);
        findViewById(R.id.activity_signin_btn_sign_out).setOnClickListener(this);
        findViewById(R.id.activity_signin_btn_continue).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // initialize auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, R.string.string_google_sign_in_failed_try_again_later, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        progressBar.setVisibility(View.INVISIBLE);
        if (user != null) {
            mStatusTextView.setText(Html.fromHtml(getString(R.string.html_welcome_back_message) + MatbitDatabase.getCurrentUserDisplayName() + "</b>"));
            findViewById(R.id.activity_signin_btn_sign_out).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_signin_btn_continue).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_signin_btn_sign_in).setVisibility(View.GONE);
        } else {
            mStatusTextView.setText(R.string.string_welcome_back);
            findViewById(R.id.activity_signin_btn_sign_in).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_signin_btn_sign_out).setVisibility(View.GONE);
            findViewById(R.id.activity_signin_btn_continue).setVisibility(View.VISIBLE);
        }
    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        findViewById(R.id.activity_signin_btn_sign_out).setVisibility(View.GONE);
        findViewById(R.id.activity_signin_btn_continue).setVisibility(View.GONE);
        findViewById(R.id.activity_signin_btn_sign_in).setVisibility(View.GONE);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finishAffinity();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.activity_signin_btn_sign_in) {
            signIn();
        } else if (i == R.id.activity_signin_btn_sign_out) {
            signOut();
        } else if (i == R.id.activity_signin_btn_continue) {
            if (MatbitDatabase.getCurrentUser() != null) {
                MatbitDatabase.handleNewUserIfNew(context);
            }
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}