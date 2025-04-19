package com.example.login;

import android.content.Intent;
import android.content.SharedPreferences;//used to save data
import android.os.Bundle;
import android.util.Log;
import android.view.View;//UI components
import android.widget.Button;//UI components
import android.widget.EditText;//UI components
import android.widget.TextView;//UI components
import android.widget.Toast;////UI components->shows quick pop up message to the user

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    private EditText user, password, dniEditText;
    private TextView lblRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);// sets the layout login.xml
        //you connect the XML components to Java so you can work with them
        user = findViewById(R.id.txtusername);
        password = findViewById(R.id.txtpassword);
        dniEditText = findViewById(R.id.txtDNI); //variable for the dni, as we need to login
        lblRegister = findViewById(R.id.lblregister);
        btnLogin = findViewById(R.id.btnlogin);
        //whent the login button is clicked it calls login user
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        // if the user clicks on Register, it opens the registration screen
        lblRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String username = user.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String dni = dniEditText.getText().toString().trim(); // Obtener el DNI ingresado por el usuario

        Log.d("LoginActivity", "DNI received: " + dni); // Verifica el DNI ingresado

        // Validates if the dni is empty
        if (dni.isEmpty()) {
            Toast.makeText(this, getString(R.string.enterdni), Toast.LENGTH_SHORT).show();
            return;
        }

        // It makes sure that eh variable dir is not duplicate
        File dir = getFilesDir();
        File file = new File(dir, dni + ".json");

        // Verifies if the file with the id exists
        if (file.exists()) {
            Log.d("LoginActivity", "File exists: " + file.getAbsolutePath());
        } else {
            Log.d("LoginActivity", "File not found: " + file.getAbsolutePath());
        }
        //validates if the username and password are empty
        if (username.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, getString(R.string.fillthefileds), Toast.LENGTH_SHORT).show();
            return;
        }

        // Read the user files
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                try {
                    Scanner scanner = new Scanner(new FileReader(f));
                    StringBuilder jsonString = new StringBuilder();
                    while (scanner.hasNext()) {
                        jsonString.append(scanner.nextLine());
                    }
                    scanner.close();

                    JSONObject userJson = new JSONObject(jsonString.toString());
                    // Compares the username and password with the ones in the file
                    if (userJson.getString("username").equals(username) && userJson.getString("password").equals(pass)&& userJson.getString("dni").equals(dni)) {
                        // Saves the username is sharedpreferences
                        SharedPreferences prefs = getSharedPreferences("CyclePrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("currentUser", username);
                        editor.apply();

                        // Change of acitivity
                        Intent intent = new Intent(this, MainButtonsActivity.class);
                        intent.putExtra("dni", dni);
                        startActivity(intent);
                        finish();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //If the username, dni or password are not correct
        Toast.makeText(this, getString(R.string.Incorrectusername), Toast.LENGTH_SHORT).show();
    }
}
