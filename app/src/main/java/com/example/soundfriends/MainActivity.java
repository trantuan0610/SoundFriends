package com.example.soundfriends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.soundfriends.auth.Login;
import com.example.soundfriends.auth.Register;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView textView;
    Button btnLogout;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        textView = findViewById(R.id.textView);
        btnLogout = findViewById(R.id.logout);

        if(user == null) {
            Intent unauthIntent = new Intent(this, Login.class);
            startActivity(unauthIntent);
            finish();
        }else {
            textView.setText(user.getEmail());
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent unauthIntent = new Intent(MainActivity.this, Login.class);
                startActivity(unauthIntent);
                finish();
            }
        });
    }
}