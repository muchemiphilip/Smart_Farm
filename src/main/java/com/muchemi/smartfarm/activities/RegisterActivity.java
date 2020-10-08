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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.muchemi.smartfarm.models.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private final AppCompatActivity activity = RegisterActivity.this;

    private NestedScrollView nestedScrollView;

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private TextInputEditText mName;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private TextInputEditText mConfirmPassword;

    private AppCompatButton mRegister;
    private AppCompatTextView mTextViewLoginLink;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    public static final int PASSWORD_LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to register.");
                //check for null valued EditText fields
                if (!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())) {

                    //check if passwords match
                    if (doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {

                        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());

                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        hideSoftKeyboard();

        mTextViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                emptyInputEditText();
                finish();
            }
        });
    }
    /**
     * This method is to initialize views
     */
    private void initViews() {

        nestedScrollView = findViewById(R.id.nestedScrollView);

        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);

        mName = findViewById(R.id.textInputEditTextName);
        mEmail = findViewById(R.id.textInputEditTextEmail);
        mPassword = findViewById(R.id.textInputEditTextPassword);
        mConfirmPassword = findViewById(R.id.textInputEditTextConfirmPassword);

        mRegister = findViewById(R.id.appCompatButtonRegister);
        mTextViewLoginLink = findViewById(R.id.appCompatTextViewLoginLink);
        mProgressBar = findViewById(R.id.progressBar);
    }

    public void registerNewEmail(final String email, String password){

        showDialog();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance().getCurrentUser().getUid());

                            sendVerificationEmail();

                            // Create a User object
                            User user = new User();
                          user.setName(email.substring(0, email.indexOf("@")));
                            user.setPhone("+254");
                            user.setSecurity_level("1");
                            user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            Log.d(TAG,"user: " + user.toString());

                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbnode_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseAuth.getInstance().signOut();
                                    redirectLoginScreen();
                                    Toast.makeText(RegisterActivity.this, "Please confirm your Verification in your Email", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (!task.isSuccessful()) {
                            try{

                                throw task.getException();

                                //if user enters weak Password
                            } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Log.d(TAG,"onComplete: Weak Password");
                                Toast.makeText(RegisterActivity.this, "Weak Password, Kindly try a Stronger Password", Toast.LENGTH_SHORT).show();

                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Log.d(TAG,"onComplete: malformedEmail");
                                Toast.makeText(RegisterActivity.this, "Malformed Email,The email you entered don't conform to a standard type ", Toast.LENGTH_SHORT).show();

                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                Log.d(TAG,"onComplete: Email already Exists");
                                Toast.makeText(RegisterActivity.this, "The email entered already exists ", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Log.d(TAG,"onComplete: " + e.getMessage());
                            }
                        }
                        hideDialog();
                        // ...
                    }
                });
    }

    private void sendVerificationEmail(){
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Sent Verification Email! Kindly Check your Email ",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Couldn't Send Verification Email",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void emptyInputEditText() {
        mName.setText(null);
        mEmail.setText(null);
        mPassword.setText(null);
        mConfirmPassword.setText(null);
    }

    private void redirectLoginScreen(){
        Log.d(TAG, "redirectLoginScreen: redirecting to login screen.");

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        emptyInputEditText();
        finish();
    }

    private boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
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

    public static boolean is_Valid_Password(String password) {

        if (password.length() < PASSWORD_LENGTH) return false;

        int charCount = 0;
        int numCount = 0;
        for (int i = 0; i < password.length(); i++) {

            char ch = password.charAt(i);

            if (is_Numeric(ch)) numCount++;
            else if (is_Letter(ch)) charCount++;
            else return false;
        }
        return (charCount >= 2 && numCount >= 2);
    }

    public static boolean is_Letter(char ch) {
        ch = Character.toUpperCase(ch);
        return (ch >= 'A' && ch <= 'Z');
    }
    public static boolean is_Numeric(char ch) {

        return (ch >= '0' && ch <= '9');
    }
}
