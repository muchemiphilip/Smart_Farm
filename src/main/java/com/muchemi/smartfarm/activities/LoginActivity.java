package com.muchemi.smartfarm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.NestedScrollView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private final AppCompatActivity activity = LoginActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    private TextInputEditText mEmail;
    private TextInputEditText mPassword;

    private AppCompatButton mBtnLogin;
    private AppCompatTextView mRegisterLink;
    private AppCompatTextView mForgottenPassword;
    private AppCompatTextView mResendVerificationEmail;
    private ProgressBar mProgressBar;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started.");

        initViews();
        setupFirebaseAuth();

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: logging in the user.");

                //check if the fields are filled out
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())) {
                    Log.d(TAG, "onClick: attempting to authenticate.");

                    showDialog();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                            mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Authenticated", Toast.LENGTH_SHORT).show();
                                        hideDialog();
                                    }
                                    if (!task.isSuccessful()) {
                                        try {

                                            throw task.getException();

                                            //if user enters wrong Email
                                        } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                            Log.d(TAG, "onComplete: invalid Email");
                                            Toast.makeText(LoginActivity.this, "The email you entered doesn't conform to a standard type", Toast.LENGTH_SHORT).show();

                                        } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                            Log.d(TAG, "onComplete: malformedEmail");
                                            Toast.makeText(LoginActivity.this, "Wrong Password!!! ", Toast.LENGTH_SHORT).show();


                                        } catch (Exception e) {
                                            Log.d(TAG,"onComplete: " + e.getMessage());
                                        }
                                    }
                                }
                            });
                }
            }
        });

        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Navigating to Register page.");

                Intent intentRegister = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intentRegister);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                emptyInputEditText();
                finish();
            }
        });
        mForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mResendVerificationEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResendVerificationDialog dialog = new ResendVerificationDialog();
                dialog.show(getSupportFragmentManager(), "dialog_resend_email_verification");
            }
        });
        hideSoftKeyboard();
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

    /**
     * This method is to initialize views
     */
    private void initViews() {
        nestedScrollView = findViewById(R.id.nestedScrollView);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        mEmail = findViewById(R.id.textInputEditTextEmail);
        mPassword = findViewById(R.id.textInputEditTextPassword);
        mBtnLogin = findViewById(R.id.appCompatButtonLogin);
        mRegisterLink = findViewById(R.id.appCompatTextViewRegisterLink);
        mForgottenPassword = findViewById(R.id.forgot_password);
        mResendVerificationEmail = findViewById(R.id.resend_verification_email);
        mProgressBar = findViewById(R.id.progressBar);
    }

    private void emptyInputEditText() {
        mEmail.setText(null);
        mPassword.setText(null);
    }

    private boolean isEmpty(String string){
        return string.equals("");
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

    /*
        ----------------------------- Firebase setup ---------------------------------
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    if (user.isEmailVerified()) {
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new  Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Check your Email for a Verification Link" ,
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            }
        };

        }
    }

