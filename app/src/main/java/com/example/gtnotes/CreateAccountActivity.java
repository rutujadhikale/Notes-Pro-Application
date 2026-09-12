package com.example.gtnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {
    EditText email_edittext,password_edittext,confirm_password_edittext;
    Button create_account_button;
    TextView login_textview;
    ProgressBar create_account_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        // Linking

        email_edittext = findViewById(R.id.create_account_email_edt);
        password_edittext = findViewById(R.id.create_account_password_edt);
        confirm_password_edittext = findViewById(R.id.create_account_confirm_password_edt);
        create_account_button = findViewById(R.id.create_account_btn);
        login_textview = findViewById(R.id.create_account_login_textview);
        create_account_progressbar = findViewById(R.id.create_account_progressbar);

        create_account_button.setOnClickListener(view -> Create_Account());
        login_textview.setOnClickListener(view -> startActivity(new Intent(CreateAccountActivity.this,LoginActivity.class)));

    }

    void Create_Account() {
        String email = email_edittext.getText().toString();
        String password = password_edittext.getText().toString();
        String confirm_password = confirm_password_edittext.getText().toString();

        Boolean isvalidated =  Validate_Data(email,password,confirm_password);
        if (!isvalidated){
            return;
        }
        Create_Firebase_Account(email,password);
    }

    void Create_Firebase_Account(String email, String password) {
        Change_In_Progress(true);
        //Connecting to Firebase Authentication to create account

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Change_In_Progress(false);
                        if (task.isSuccessful()){
                            // if successfull
                            Toast.makeText(CreateAccountActivity.this, "Successfully Email Created.. Check Emial To verify", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else {
                            // if failure
                            Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                );


    }
    void Change_In_Progress(boolean inprogress){
        if (inprogress){
            create_account_progressbar.setVisibility(View.VISIBLE);
            create_account_button.setVisibility(View.GONE);
        }else{
            create_account_progressbar.setVisibility(View.GONE);
            create_account_button.setVisibility(View.VISIBLE);
        }
    }

    boolean Validate_Data(String email, String password, String confirm_password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edittext.setError("Invalid Email");
            return false;
        }
        if (password.length()<6) {
            password_edittext.setError("Password Length smaller than 6");
            return false;
        }
        if (!password.equals(confirm_password)){
            confirm_password_edittext.setError("Invalid Confirm Password");
            return false;
        }
        return true;
    }
}