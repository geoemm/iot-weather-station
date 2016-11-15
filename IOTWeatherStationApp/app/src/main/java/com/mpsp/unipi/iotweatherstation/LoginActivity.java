package com.mpsp.unipi.iotweatherstation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;//Google API pop-up screen about register or login via Google account
    private static final int RC_SIGN_IN=1;
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    /*
    Define few strings about saving account data
      */
    private String name;
    private String email;
    private String userId;
    /*
    Define buttons
     */
    private Button logoutBtn,nextBtn;
    private SignInButton mGoogleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*
        Update UI set visible only the sign-in button
         */
        updateUI(false);

        /*
        Integrate UI buttons to java code
         */

        mGoogleBtn = (SignInButton)findViewById(R.id.googleBtn);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        nextBtn = (Button) findViewById(R.id.nextBtn);

        /*
        Set OnClick Listener on logout button for sign-off the account from App. Then someone could participate with other google account
         */
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        /*
        Set OnClick Listener on Go To App button. User is redirected to Main Activity. Account data pushed to the Main Activity via Intent
         */
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                intent.putExtra("userId", userId);
                intent.putExtra("name",name);
                intent.putExtra("email",email);

                startActivity(intent);
            }
        });



        mAuth=FirebaseAuth.getInstance();

        /*
        Set Authenticate Listener that if user exists and is authenticated then no login needed and proceed with login in App auto
         */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Name, email address, and profile photo Url
                         name = user.getDisplayName();
                         email = user.getEmail();
                         userId = user.getUid();
                         Log.d("Email Data:",email);
                         Log.d("Name Data:",name);
                         Log.d("User Id Data:",userId);
                    }

                    updateUI(true); //Update UI set Google Sign-IN Button unvisible


                }
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(LoginActivity.this,"Open Wireless Internet OR Data Mobile Provider",Toast.LENGTH_LONG);
                updateUI(false);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed. Please Open Wireless Network OR Provider Data",
                                    Toast.LENGTH_LONG).show();
                        }
                        // ...
                    }
                });

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    private void signOut() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]

                    }
                });
    }



    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.googleBtn).setVisibility(View.GONE);
            findViewById(R.id.logoutBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.nextBtn).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.googleBtn).setVisibility(View.VISIBLE);
            findViewById(R.id.logoutBtn).setVisibility(View.GONE);
            findViewById(R.id.nextBtn).setVisibility(View.GONE);
        }
    }


}
