package com.example.soundfriends.auth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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

import com.example.soundfriends.R;
import com.example.soundfriends.utils.ToggleShowHideUI;
import com.example.soundfriends.utils.validator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    //Màn hình đăng nhập
    //khai báo các biến để cbi liên kết với các view
    EditText edtEmail, edtPassword;
    Button btnRegister, btnLogIn, btnForgotPassword;
    ImageView btnLoginWithGoogle;
    FirebaseAuth firebaseAuth;
    ProgressBar pbLoadLogin;

    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // kiểm tra user đã từng đăng nhập chưa, nếu đã đăng nhập thì di chuyển đến màn hình home
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            SharedAuthMethods.goHomeActivity(Login.this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //liên kết biến đã khởi tạo bên trên với view
        edtEmail = (EditText) findViewById(R.id.edtLoginEmail);
        edtPassword = (EditText) findViewById(R.id.edtLoginPassword);
        btnRegister = (Button) findViewById(R.id.btnRegisterInLogin);
        pbLoadLogin = (ProgressBar) findViewById(R.id.pbLoadLogin);
        btnLogIn = (Button) findViewById(R.id.btnLogin);
        btnLoginWithGoogle = (ImageView) findViewById(R.id.logInWithGoogle);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        //getFirebaseAuth Instance
        firebaseAuth = FirebaseAuth.getInstance();

        //getGoogleSignInOptions
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        //getGoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(Login.this, googleSignInOptions);

        // sự kiện nhấn nút đăng nhập
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

                //set loading state - gọi view loading lên
                ToggleShowHideUI.toggleShowUI(true, pbLoadLogin);
                // sử dụng đăng nhập firebase với email và pass
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    // If sign in success, display a message to the user.
                                    Toast.makeText(Login.this, "Đăng nhập thành công",
                                            Toast.LENGTH_SHORT).show();
                                    //goHome Intent- sử dụng intent để di chuyển tới màn hình home
                                    SharedAuthMethods.goHomeActivity(Login.this);
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    FirebaseException exception = (FirebaseException) task.getException();
                                    String errorMessage = validator.validatorMessage(exception.getMessage());
                                    Log.d("Error", errorMessage);
                                    Toast.makeText(Login.this, errorMessage,
                                            Toast.LENGTH_LONG).show();
                                }
                                //hide loading state
                                ToggleShowHideUI.toggleShowUI(false, pbLoadLogin);
                            }
                        });
            }
        });

        // sự kiện nhấn nút đăng kí
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterActivityIntent = new Intent(Login.this, Register.class);
                startActivity(RegisterActivityIntent);
                finish();
            }
        });

        //2ND WAY: AUTHENTICATE WITH GOOGLE
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                SharedAuthMethods sharedAuthMethods = new SharedAuthMethods();
                sharedAuthMethods.GoogleIntentLauncher(result, Login.this, pbLoadLogin, firebaseAuth, googleSignInClient);
            }
        });

        //sự kiện nhất nút đăng nhập bằng google
        btnLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GoogleSignInIntent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(GoogleSignInIntent);
            }
        });

        //sự kiện nhấn nút quên mật khẩu
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtEmail.getText().toString().isEmpty()){
                    Toast.makeText(Login.this, "Vui lòng nhập Email tài khoản", Toast.LENGTH_SHORT).show();
                } else handleResetPassword();
            }
        });
    }

    private void handleResetPassword() {
        ToggleShowHideUI.toggleShowUI(true, pbLoadLogin);
        firebaseAuth.sendPasswordResetEmail(edtEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Login.this, "Đã gửi Email khôi phục mật khẩu. Hãy mở ứng dụng Email", Toast.LENGTH_LONG).show();
                    ToggleShowHideUI.toggleShowUI(false, pbLoadLogin);
                } else Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}