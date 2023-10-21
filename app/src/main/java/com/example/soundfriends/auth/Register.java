package com.example.soundfriends.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.soundfriends.MainActivity;
import com.example.soundfriends.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    EditText edtEmail, edtPassword, edtPassword2;
    Button btnRegister, btnLogIn;
    ImageView btnRegisterWithGoogle;
    FirebaseAuth mAuth;
    ProgressBar pbLoadRegister;

//    GoogleSignInOptions googleSignInOptions;
//    GoogleSignInClient googleSignInClient;

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
        setContentView(R.layout.activity_register);

        //getFirebaseAuth Instance
        mAuth = FirebaseAuth.getInstance();
//        //getGoogleSignInOptions
//        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//        //getGoogleSignInClient
//        googleSignInClient = GoogleSignIn.getClient(Register.this, googleSignInOptions);

        edtEmail = (EditText) findViewById(R.id.edtRegisterEmail);
        edtPassword = (EditText) findViewById(R.id.edtRegisterPassword);
        edtPassword2 = (EditText) findViewById(R.id.edtRegisterPassword2);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        pbLoadRegister = (ProgressBar) findViewById(R.id.pbLoadRegister);
        btnLogIn = (Button) findViewById(R.id.btnLoginInRegister);
        btnRegisterWithGoogle = (ImageView) findViewById(R.id.btnRegisterWithGoogle);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, password2;
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                password2 = edtPassword2.getText().toString();

                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(password2)){
                    Toast.makeText(Register.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!password.equals(password2)){
                    Toast.makeText(Register.this, "Mật khẩu nhập lại không trùng khớp với Mật khẩu đã nhập", Toast.LENGTH_SHORT).show();
                    return;
                }

                //set loading state
                pbLoadRegister.setVisibility(View.VISIBLE);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // If sign in success, display a message to the user.
                                    Toast.makeText(Register.this, "Đăng ký thành công",
                                            Toast.LENGTH_SHORT).show();
                                    HomeActivity();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Exception exception =  task.getException();
                                    Toast.makeText(Register.this, exception.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                                //hide loading state
                                pbLoadRegister.setVisibility(View.GONE);
                            }
                        });
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginActivityIntent = new Intent(Register.this, Login.class);
                startActivity(LoginActivityIntent);
                finish();
            }
        });

        btnRegisterWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent GoogleSignInIntent = googleSignInClient.getSignInIntent();
//                startActivityForResult(GoogleSignInIntent, 100);
            };
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 100) {
//            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                googleSignInAccountTask.getResult(ApiException.class);
//            } catch (ApiException e) {
//                Toast.makeText(Register.this, "Lỗi đăng nhập qua Google", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void HomeActivity() {
        Intent MainActivityIntent = new Intent(Register.this, MainActivity.class);
        startActivity(MainActivityIntent);
        finish();
    }
}