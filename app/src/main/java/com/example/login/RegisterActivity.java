package com.example.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    // UI components
    private EditText editTextNewUsername, editTextNewPassword, editTextDni;
    private Button buttonRegister;
    private TextView lblLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Link UI elements to variables
        editTextDni = findViewById(R.id.editTextDni);
        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        lblLogin = findViewById(R.id.lblLogin);

        // Set click listener for the register button
        buttonRegister.setOnClickListener(v -> registerUser());

        // Set click listener to go back to login screen
        lblLogin.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }

    /**
     * Handles user registration: validates input, creates a JSON file with user data,
     * and navigates to the next screen if successful.
     */
    private void registerUser() {
        String newUsername = editTextNewUsername.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String newDNI = editTextDni.getText().toString().trim();

        Log.d("RegisterActivity", "Username: " + newUsername + ", Password: " + newPassword + ", DNI: " + newDNI);

        // Validate input fields
        if (newUsername.isEmpty() || newPassword.isEmpty() || newDNI.isEmpty()) {
            Toast.makeText(this, getString(R.string.fillthefileds), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if a user with this DNI already exists
        File file = new File(getFilesDir(), newDNI + ".json");
        Log.d("RegisterActivity", "File created: " + file.getAbsolutePath());
        if (file.exists()) {
            Toast.makeText(this, getString(R.string.userexists), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a new JSON object with default and entered values
            JSONObject userJson = new JSONObject();
            userJson.put("dni", newDNI);
            userJson.put("username", newUsername);
            userJson.put("password", newPassword);
            userJson.put("name", "");
            userJson.put("age", "");
            userJson.put("gender", "");
            userJson.put("weight", "");
            userJson.put("height", "");
            userJson.put("infertility", "");
            userJson.put("family history", "");
            userJson.put("age at menarche", "");
            userJson.put("nulliparity", "");

            // Save the JSON object to a file named after the DNI
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(userJson.toString());
            }

            // Show confirmation and navigate to MaininformationActivity
            Toast.makeText(this, getString(R.string.userregistered), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, MaininformationActivity.class);
            intent.putExtra("dni", newDNI);
            startActivity(intent);

            finish(); // Close the current activity

        } catch (JSONException e) {
            // Handle JSON creation error
            Log.e("RegisterActivity", "Error creating JSON", e);
            Toast.makeText(this, getString(R.string.usererror), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Handle file writing error
            Log.e("RegisterActivity", "Error writing file", e);
            Toast.makeText(this, getString(R.string.datanotsaved), Toast.LENGTH_SHORT).show();
        }
    }
}
