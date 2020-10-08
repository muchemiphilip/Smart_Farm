package com.muchemi.smartfarm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muchemi.smartfarm.models.User;


public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    //firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private EditText mEmail, mCurrentPassword, mName, mPhone;
    private ImageView mProfileImage;
    private Button mSave;
    private ProgressBar mProgressBar;
    private TextView mResetPasswordLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Log.d(TAG, "onCreate: started.");
        mEmail = (EditText) findViewById(R.id.input_email);
        mName = findViewById(R.id.input_name);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mPhone = findViewById(R.id.input_phone);
        mCurrentPassword = (EditText) findViewById(R.id.input_password);
        mSave= (Button) findViewById(R.id.btn_save);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mResetPasswordLink = (TextView) findViewById(R.id.change_password);

        setupFirebaseAuth();

        setCurrentEmail();

        getUserAccountsData();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save settings.");

                //make sure email and current password fields are filled
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mCurrentPassword.getText().toString())){

                    if(!FirebaseAuth.getInstance().getCurrentUser().getEmail()
                            .equals(mEmail.getText().toString())){
                        editUserEmail();
                    }else{
                        Toast.makeText(SettingsActivity.this, "no changes were made", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(SettingsActivity.this, "Email and Current Password Fields Must be Filled to Save", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mResetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: sending password reset link");

                sendResetPasswordLink();
            }
        });
        hideSoftKeyboard();
    }

    private void getUserAccountsData(){
        Log.d(TAG,"getUserAccountsData: getting the user accounts information");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        // Creating our query with method 1
        Query query1 = reference.child(getString(R.string.dbnode_users))
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()){
                    User user = singleSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: (Query Method 1) found user: " + user.toString() );

                    mName.setText(user.getName());
                    mPhone.setText(user.getPhone());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Creating our query with method 2 which is the same as Query method 1
        Query query2 = reference.child(getString(R.string.dbnode_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()){
                    User user = singleSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: (Query Method 2) found user: " + user.toString() );

                    mName.setText(user.getName());
                    mPhone.setText(user.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    private void sendResetPasswordLink(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Password Reset Email sent.");
                            Toast.makeText(SettingsActivity.this, "Sent Password Reset Link to Email",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG, "onComplete: No user associated with that email.");

                            Toast.makeText(SettingsActivity.this, "No User Associated with that Email.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    private void editUserEmail(){
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.

        showDialog();

        AuthCredential credential = EmailAuthProvider
                .getCredential(FirebaseAuth.getInstance().getCurrentUser().getEmail(), mCurrentPassword.getText().toString());
        Log.d(TAG, "editUserEmail: reauthenticating with:  \n email " + FirebaseAuth.getInstance().getCurrentUser().getEmail()
                + " \n passowrd: " + mCurrentPassword.getText().toString());


        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: reauthenticate success.");

                            ///////////////////now check to see if the email is not already present in the database
                            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(

                                    new OnCompleteListener<SignInMethodQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                                    /////////////////////add new email
                                                    FirebaseAuth.getInstance().getCurrentUser().updateEmail(mEmail.getText().toString())
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.d(TAG, "onComplete: User email address updated.");
                                                                        Toast.makeText(SettingsActivity.this, "Updated email", Toast.LENGTH_SHORT).show();
                                                                        sendVerificationEmail();
                                                                        FirebaseAuth.getInstance().signOut();
                                                                    } else {
                                                                        Log.d(TAG, "onComplete: Could not update email.");
                                                                        Toast.makeText(SettingsActivity.this, "unable to update email", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    hideDialog();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    hideDialog();
                                                                    Toast.makeText(SettingsActivity.this, "unable to update email", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hideDialog();
                                            Toast.makeText(SettingsActivity.this, "unable to update email", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    {
                        Log.d(TAG, "onComplete: Incorrect Password");
                        Toast.makeText(SettingsActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        hideDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideDialog();
                        Toast.makeText(SettingsActivity.this, "“unable to update email”", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * sends an email verification link to the user
     */
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SettingsActivity.this, "Couldn't Verification Send Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void setCurrentEmail(){
        Log.d(TAG, "setCurrentEmail: setting current email to EditText field");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(TAG, "setCurrentEmail: user is NOT null.");
            String email = user.getEmail();
            Log.d(TAG, "setCurrentEmail: got the email: " + email);
            mEmail.setText(email);
        }
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private boolean isEmpty(String string){
        return string.equals("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState(){
        Log.d(TAG, "checkAuthenticationState: checking authentication state.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.");

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            Log.d(TAG, "checkAuthenticationState: user is authenticated.");
        }
    }


    /*
            ----------------------------- Firebase setup ---------------------------------
    */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    //toastMessage("Successfully signed in with: " + user.getEmail());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(SettingsActivity.this, "Signed out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}















