package com.example.guest.chatdawgz.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guest.chatdawgz.Constants;
import com.example.guest.chatdawgz.R;
import com.example.guest.chatdawgz.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.enterUserName) EditText mEnterUsername;
    @BindView(R.id.enterEmail) EditText mEnterEmail;
    @BindView(R.id.enterPassword) EditText mEnterPassword;
    @BindView(R.id.confirmPassword) EditText mConfirmPassword;
    @BindView(R.id.createAccountButton) Button mCreateAccountButton;
    @BindView(R.id.loginTextView) TextView mLogin;
    @BindView(R.id.createAccountHeader) TextView mCreateAccountHeader;

    private FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        mCreateAccountButton.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

        Typeface headerFont = Typeface.createFromAsset(getAssets(), "fonts/font.TTF");
        mCreateAccountHeader.setTypeface(headerFont);

        createAuthProgressDialog();
    }


    @Override
    public void onClick(View v) {
        if (v == mLogin) {
            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        if (v == mCreateAccountButton) {
            createNewUser();
        }

    }

    public void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Almost there, canine!");
        mAuthProgressDialog.setMessage("Keep it cool, pooch!");
        mAuthProgressDialog.setCancelable(false);
    }

    private void createNewUser() {
        mUserName = mEnterUsername.getText().toString().trim();
        String email = mEnterEmail.getText().toString().trim();
        String password = mEnterPassword.getText().toString().trim();
        String confirmPassword = mConfirmPassword.getText().toString().trim();

        boolean validEmail = isValidEmail(email);
        boolean validName = isValidName(mUserName);
        boolean validPassword = isValidPassword(password, confirmPassword);
        if (!validEmail || !validName || !validPassword) return;
        mAuthProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mAuthProgressDialog.dismiss();
                if (task.isSuccessful()) {
                    createFirebaseUserProfile(task.getResult().getUser());
                } else {
                    Toast.makeText(CreateAccountActivity.this, "WOOF Authentication failed. Try again. WOOF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createFirebaseUserProfile(final FirebaseUser user) {
        UserProfileChangeRequest addProfileName = new UserProfileChangeRequest.Builder()
                .setDisplayName(mUserName)
                .setPhotoUri(Uri.parse(Constants.defaultImgUrl))
                .build();
        user.updateProfile(addProfileName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    User newUser = new User(user.getDisplayName(), user.getPhotoUrl().toString());
                    newUser.setId(user.getUid());

                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                            .getReference(Constants.FIREBASE_USER_REF)
                            .child(user.getUid());

                    userRef.setValue(newUser);

                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    private boolean isValidEmail(String email) {
        boolean isGoodEmail = (email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) mEnterEmail.setError("Please enter a valid email address, dawg!");
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        boolean isGoodName = !name.equals("");
        if (!isGoodName) mEnterUsername.setError("Please enter your name, mutt");
        return isGoodName;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            mEnterPassword.setError("Please create a password containing at least 6 characters, hound");
            return false;
        } else if (!password.equals(confirmPassword)) {
            mEnterPassword.setError("Passwords do not match, flea bag");
            return false;
        }
        return true;
    }

}
