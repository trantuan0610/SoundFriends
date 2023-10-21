package com.example.soundfriends.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.soundfriends.MainActivity;
import com.example.soundfriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    Button btnRegister, btnLogIn;
    FirebaseAuth mAuth;
    ProgressBar pbLoadLogin;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            HomeActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = (EditText) findViewById(R.id.edtLoginEmail);
        edtPassword = (EditText) findViewById(R.id.edtLoginPassword);
        btnRegister = (Button) findViewById(R.id.btnRegisterInLogin);
        pbLoadLogin = (ProgressBar) findViewById(R.id.pbLoadLogin);
        btnLogIn = (Button) findViewById(R.id.btnLogin);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, password2;
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                //set loading state
                pbLoadLogin.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // If sign in success, display a message to the user.
                                    Toast.makeText(Login.this, "Đăng nhập thành công",
                                            Toast.LENGTH_SHORT).show();
                                    HomeActivity();
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Exception exception =  task.getException();
                                    Toast.makeText(Login.this, exception.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                //hide loading state
                                pbLoadLogin.setVisibility(View.GONE);
                            }
                        });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterActivityIntent = new Intent(Login.this, Register.class);
                startActivity(RegisterActivityIntent);
                finish();
            }
        });
    }
    private void HomeActivity() {
        Intent MainActivityIntent = new Intent(Login.this, MainActivity.class);
        startActivity(MainActivityIntent);
        finish();
    }
}