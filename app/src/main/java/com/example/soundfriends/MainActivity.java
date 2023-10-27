package com.example.soundfriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soundfriends.auth.Login;
import com.example.soundfriends.auth.Register;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView textView;
    Button btnLogout;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        btnLogout = findViewById(R.id.logout);

        //get Firebase user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user == null) {
            goAuthActivity();
        }else {
            String info = user.getEmail() != null ? user.getEmail() : user.getDisplayName();
            textView.setText("Tài khoản: " + info);
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                goAuthActivity();
            }
        });
    }

    private void goAuthActivity(){
        Intent unauthIntent = new Intent(MainActivity.this, Login.class);
        startActivity(unauthIntent);
        finish();
    }
}