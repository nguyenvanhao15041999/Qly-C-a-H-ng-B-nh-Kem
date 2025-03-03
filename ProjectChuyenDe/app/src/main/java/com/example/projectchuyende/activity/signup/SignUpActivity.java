package com.example.projectchuyende.activity.signup;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.projectchuyende.R;
import com.example.projectchuyende.validators.AddressValidator;
import com.example.projectchuyende.validators.EmailValidator;
import com.example.projectchuyende.validators.PasswordValidator;
import com.example.projectchuyende.validators.PhoneNumberValidator;
import com.example.projectchuyende.validators.UsernameValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;


public class SignUpActivity extends Activity {
    ImageView imgBack;
    EditText edtAccount, edtPassword, edtEmail, edtPhoneNumber, edtAddress;
    Button btnSignup;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        imgBack = findViewById(R.id.imgBack);
        edtAccount = findViewById(R.id.edtAccount);
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAddress = findViewById(R.id.edtAddress);
        btnSignup = findViewById(R.id.btnSignup);

        setControl();
        Toolbar toolbar = findViewById(R.id.toolbar_forgotpassword);
    }

    private void setControl() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // When sign up button is clicked
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtAccount.getText().toString();
                String password = edtPassword.getText().toString();
                String email = edtEmail.getText().toString();
                String address = edtAddress.getText().toString();
                String phoneNumber = edtPhoneNumber.getText().toString();

                String permisstion = "user";

                if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
                    && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(address)
                    && !TextUtils.isEmpty(phoneNumber)){
                    // run register function
                    register(username, password, email, address, phoneNumber, permisstion);
                } else if(TextUtils.isEmpty(username)){ // username is null
                    edtAccount.setError("Account is invalid!");
                } else if(TextUtils.isEmpty(password)) { // password name is null
                    edtPassword.setError("Password is invalid!");
                } else if(TextUtils.isEmpty(email)) { // email name is null
                    edtPassword.setError("Email is invalid!");
                } else if(TextUtils.isEmpty(address)) { // address name is null
                    edtPassword.setError("Address is invalid!");
                } else if(TextUtils.isEmpty(phoneNumber)) { // phoneNumber name is null
                    edtPassword.setError("Phone Number is invalid!");
                }

            }
        });
    }

    private void register(final String username, final String password, final String email, final String address, final String phoneNumber, final String permisstion) {
        // create new user account
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // creating success
                if (task.isSuccessful()) {
                    // Get user id of account created
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();
                    // Save user info in Users table
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userId", userId);
                    hashMap.put("username", username);
                    hashMap.put("email", email);
                    hashMap.put("address", address);
                    hashMap.put("phoneNumber", phoneNumber);
                    hashMap.put("permission", permisstion);

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Sign up success!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign up failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
