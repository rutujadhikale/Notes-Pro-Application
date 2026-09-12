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

public class LoginActivity extends AppCompatActivity {
    EditText email_edittext,password_edittext;
    Button login_button;
    TextView create_account_textview;
    ProgressBar login_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_edittext = findViewById(R.id.login_email_edt);
        password_edittext = findViewById(R.id.login_password_edt);
        login_button = findViewById(R.id.login_btn);
        create_account_textview = findViewById(R.id.create_account_textview);
        login_progressbar = findViewById(R.id.login_progressbar);

        login_button.setOnClickListener(view -> Login_User());
        create_account_textview.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class)));
    }

    void Login_User() {
        String email = email_edittext.getText().toString();
        String password = password_edittext.getText().toString();

        Boolean isvalidated = Validate_Data(email,password);
        if (!isvalidated){
            return;
        }
        login_Account_In_Firebase(email,password);
    }

    void login_Account_In_Firebase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Change_In_Progress(true);

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Change_In_Progress(false);
                if (task.isSuccessful()){
                    // if successful
                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                            //Go to main Activity
                             startActivity(new Intent(LoginActivity.this,MainActivity.class));
                             finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Email is Not Verified.. Check Your Email", Toast.LENGTH_SHORT).show();
                        }
                }else {
                    // if failure
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    Boolean Validate_Data(String email, String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edittext.setError("Invalid Email");
            return false;
        }
        if (password.length()<6) {
            password_edittext.setError("Password Length smaller than 6");
            return false;
        }
        return true;
    }

    void Change_In_Progress(boolean inprogress){
        if (inprogress){
            login_progressbar.setVisibility(View.VISIBLE);
            login_button.setVisibility(View.GONE);
        }else{
            login_progressbar.setVisibility(View.GONE);
            login_button.setVisibility(View.VISIBLE);
        }
    }
}