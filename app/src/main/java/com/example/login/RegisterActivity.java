package com.example.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.OutputStream;

public class RegisterActivity extends AppCompatActivity {

    // UI components
    private EditText editTextNewUsername, editTextNewPassword, editTextDni;
    private Button buttonRegister;
    private TextView lblLogin;
    private static final int CREATE_FILE_REQUEST_CODE = 1001;
    private String jsonToSave = "";
    private String fileName = "";


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
            // After saving the file internally, it calls FileUtils.exportJsonToDownloads() to move the file ot the Downloads folder in the phone
            FileUtils.exportJsonToDownloads(file, newDNI + ".json");
            jsonToSave = userJson.toString();
            fileName = newDNI + ".json";

            // Launching SAF for File creation for Android 10+->it allows the app to request permission from the user to create and store the file in a location they choose
            createJsonFileInDownloads();


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
    private void createJsonFileInDownloads() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);// creates an Intent which opens a system dialog allowing the user to choose where to save the file
        intent.setType("application/json");// indicates we are saving a json file
        intent.putExtra(Intent.EXTRA_TITLE, fileName);// specifies the file name
        intent.addCategory(Intent.CATEGORY_OPENABLE);//ensures that only app that can handle file creation will appear in the picker
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);// starts the file picker activity
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {//checks if the result is from the "create document" request and if the user actually completed it.
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();// the location URI the user chose to save the file

                try {

                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);// gives the app the permission to read/write the URI (document)


                    getSharedPreferences("app_prefs", MODE_PRIVATE)// saves the URI as a string in the app's shared preferences so if the app needs to acces the file again later, it knows where it is
                            .edit()
                            .putString("user_file_uri", uri.toString())
                            .apply();


                    try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            outputStream.write(jsonToSave.getBytes());// wirtes the JSON data to the file the user chose
                            outputStream.flush();// ensures all data is written out
                        }
                    }

                    Toast.makeText(this, getString(R.string.userregistered), Toast.LENGTH_SHORT).show();

                    //The app goes to the next activity
                    Intent intent = new Intent(RegisterActivity.this, MaininformationActivity.class);
                    intent.putExtra("dni", editTextDni.getText().toString().trim());
                    startActivity(intent);
                    finish();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, getString(R.string.datanotsaved), Toast.LENGTH_SHORT).show();
                } catch (SecurityException se) {
                    se.printStackTrace();
                    Toast.makeText(this, "Insufficient permissions to access the file.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }





}
