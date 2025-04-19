package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.LoginActivity;
import com.example.login.R;
import com.example.login.RegisterActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        // References to the buttons
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        // Navigate to LoginActivity when login button is clicked
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the LoginActivity
                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent); // Start the LoginActivity
            }
            // This comment explains that the button leads the user to the login screen when clicked
        });

        // Navigate to RegisterActivity when register button is clicked
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the RegisterActivity
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent); // Start the RegisterActivity
            }
            // This comment explains that the button leads the user to the registration screen when clicked
        });
    }
}
