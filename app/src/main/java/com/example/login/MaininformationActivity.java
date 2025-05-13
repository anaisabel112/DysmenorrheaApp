package com.example.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

public class MaininformationActivity extends AppCompatActivity {
    // Declare UI components and variables
    private EditText editName, editAge, editGender, editWeight, editHeight,editAgeAtMenarche;

    private RadioGroup radioGroupInfertility, radioGroupFamilyHistory, radioGroupNulliparity;


    private Button btnContinue4;
    private String dni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MaininformationActivity", "onCreate called");
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);// Force light mode, for color problem
        setContentView(R.layout.maininformation);

        // Connect the UI elements to their respective fields in the layout
        editName = findViewById(R.id.editName);
        if (editName == null) {
            Log.e("MaininformationActivity", "editTextName is null");
        }
        editAge = findViewById(R.id.editAge);
        if (editAge == null) {
            Log.e("MaininformationActivity", "editTextAge is null");
        }
        editGender = findViewById(R.id.editGender);
        if (editGender == null) {
            Log.e("MaininformationActivity", "editGender is null");
        }
        editWeight = findViewById(R.id.editWeight);
        if (editWeight == null) {
            Log.e("MaininformationActivity", "editWeight is null");
        }
        editHeight = findViewById(R.id.editHeight);
        if (editHeight == null) {
            Log.e("MaininformationActivity", "editTHeight is null");
        }
        radioGroupInfertility = findViewById(R.id.radioGroupInfertility);
        radioGroupFamilyHistory = findViewById(R.id.radioGroupFamilyHistory);
        editAgeAtMenarche = findViewById(R.id.editAgeAtMenarche);
        radioGroupNulliparity = findViewById(R.id.radioGroupNulliparity);
        btnContinue4 = findViewById(R.id.btnContinue4);

        // Get the 'dni' passed from the previous activity
        dni = getIntent().getStringExtra("dni");


        // Load any existing user data from the file
        loadUserData();

        // Set the action for the continue button
        btnContinue4.setOnClickListener(v -> {

            // Validate input fields before saving
            if (validateFields()) {

                saveUserData(); // Save the data if all fields are valid
                // Navigate to the next activity
                Intent intent = new Intent(MaininformationActivity.this, MainButtonsActivity.class);
                intent.putExtra("dni", dni);
                startActivity(intent);
                finish();
            } else {
                // Show a message if any field is empty
                Toast.makeText(MaininformationActivity.this, getString(R.string.fillthefileds), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Validates the input fields to ensure that they are not empty.
     * @return true if all fields are filled, false otherwise
     */
    private boolean validateFields() {
        // Check if any input field is empty
        return !editName.getText().toString().trim().isEmpty() &&
                !editAge.getText().toString().trim().isEmpty() &&
                !editGender.getText().toString().trim().isEmpty() &&
                !editWeight.getText().toString().trim().isEmpty() &&
                !editHeight.getText().toString().trim().isEmpty() &&
                radioGroupInfertility.getCheckedRadioButtonId() != -1 &&
                radioGroupFamilyHistory.getCheckedRadioButtonId() != -1 &&
                !editAgeAtMenarche.getText().toString().trim().isEmpty() &&
                radioGroupNulliparity.getCheckedRadioButtonId() != -1;
    }

    /**
     * Saves the user data to a JSON file.
     * It either creates a new file or updates an existing one.
     */
    private void saveUserData() {
        try {
            // Create or open the JSON file using the user's DNI as the filename
            File file = new File(getFilesDir(), dni + ".json");
            // Read the existing data if the file exists, otherwise create a new JSON object
            JSONObject userJson = file.exists() ? new JSONObject(readFile(file)) : new JSONObject();
            String infertility = ((RadioButton) findViewById(radioGroupInfertility.getCheckedRadioButtonId())).getText().toString();
            String familyHistory = ((RadioButton) findViewById(radioGroupFamilyHistory.getCheckedRadioButtonId())).getText().toString();
            String nulliparity = ((RadioButton) findViewById(radioGroupNulliparity.getCheckedRadioButtonId())).getText().toString();
            // Add the user data to the JSON object
            userJson.put("name", editName.getText().toString().trim());
            userJson.put("age", editAge.getText().toString().trim());
            userJson.put("gender", editGender.getText().toString().trim());
            userJson.put("weight", editWeight.getText().toString().trim());
            userJson.put("height", editHeight.getText().toString().trim());
            userJson.put("infertility", infertility);
            userJson.put("family_history", familyHistory);
            userJson.put("age_at_menarche", editAgeAtMenarche.getText().toString().trim());
            userJson.put("nulliparity", nulliparity);

            // Remove incorrect or outdated field names
            userJson.remove("family history");
            userJson.remove("age at menarche");

            // Write the updated JSON data to the file
            writeFile(file, userJson.toString());

            //exports the JSON document to Downloads of the phone
            FileUtils.exportJsonToDownloads(file, dni + ".json");
            // It also saves the information in the SAF document if exists
            //Write to SAF (Storage Access Framework) location if previously saved URI is available
            try {
                String uriString = getSharedPreferences("app_prefs", MODE_PRIVATE)// it retrieves the URI that contains the location of the document where the data is going to be saved
                        .getString("user_file_uri", null);

                if (uriString != null) {
                    Uri uri = Uri.parse(uriString);
                    try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            outputStream.write(userJson.toString().getBytes());
                            outputStream.flush();
                            Log.d("MaininformationActivity", "Data also saves in SAF" + uri.toString());
                        }
                    }
                } else {
                    Log.d("MaininformationActivity", "There is no URI saved for SAF.");
                }
            } catch (Exception e) {
                Log.e("MaininformationActivity", "Error saving SAF", e);
            }




            // Show a success message and log the saved data
            Toast.makeText(this, getString(R.string.datasaved), Toast.LENGTH_SHORT).show();
            Log.d("MaininformationActivity", "User data saved: " + userJson.toString());
        } catch (Exception e) {
            // Handle any exceptions during the save process
            Log.e("MaininformationActivity", "Error saving data", e);
            Toast.makeText(this, getString(R.string.datanotsaved), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads user data from the JSON file and populates the input fields.
     * If no data is found, a message is shown.
     */
    private void loadUserData() {
        if (dni == null || dni.isEmpty()) {

            // Show an error message if the DNI is invalid
            Toast.makeText(this, getString(R.string.dninotfound), Toast.LENGTH_SHORT).show();
            return;
        }

        // Locate the user's JSON file
        File file = new File(getFilesDir(), dni + ".json");

        // If the file exists, load the data into the fields
        if (file.exists()) {
            try {
                JSONObject userJson = new JSONObject(readFile(file));
                Log.d("MaininformationActivity", "Datos cargados del archivo: " + userJson.toString());


                // Set the EditText fields with the loaded data
                editName.setText(userJson.optString("name", ""));
                editAge.setText(userJson.optString("age", ""));
                editGender.setText(userJson.optString("gender", ""));
                editWeight.setText(userJson.optString("weight", ""));
                editHeight.setText(userJson.optString("height", ""));
                String infertility = userJson.optString("infertility", "");
                if (infertility.equalsIgnoreCase("Yes")) {
                    radioGroupInfertility.check(R.id.radioInfertilityYes);
                } else if (infertility.equalsIgnoreCase("No")) {
                    radioGroupInfertility.check(R.id.radioInfertilityNo);
                }
                String familyHistory = userJson.optString("family_history", "");
                if (familyHistory.equalsIgnoreCase("Yes")) {
                    radioGroupFamilyHistory.check(R.id.radioFamilyHistoryYes);
                } else if (familyHistory.equalsIgnoreCase("No")) {
                    radioGroupFamilyHistory.check(R.id.radioFamilyHistoryNo);
                }
                editAgeAtMenarche.setText(userJson.optString("age_at_menarche", ""));
                String nulliparity = userJson.optString("nulliparity", "");
                if (nulliparity.equalsIgnoreCase("Yes")) {
                    radioGroupNulliparity.check(R.id.radioNulliparityYes);
                } else if (nulliparity.equalsIgnoreCase("No")) {
                    radioGroupNulliparity.check(R.id.radioNulliparityNo);
                }

                Log.d("MaininformationActivity", "User data loaded successfully for DNI: " + dni);
            } catch (Exception e) {
                // Handle any errors during the load process
                Log.e("MaininformationActivity", "Error loading user data", e);
                Toast.makeText(this, getString(R.string.datanotsaved), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show a message if no data is found for the user
            Toast.makeText(this, getString(R.string.notdatafound)+ dni, Toast.LENGTH_SHORT).show();
            Log.d("MaininformationActivity", "No JSON file found for DNI: " + dni);
        }
    }

    /**
     * Reads the content of a file and returns it as a String.
     * @param file the file to read from
     * @return the file content as a String
     */
    private String readFile(File file) {
        StringBuilder jsonString = new StringBuilder();
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
        } catch (IOException e) {
            // Handle any errors while reading the file
            Log.e("MaininformationActivity", "Error reading file", e);
        }
        return jsonString.toString();
    }

    /**
     * Writes data to a file.
     * @param file the file to write to
     * @param data the data to write to the file
     */
    private void writeFile(File file, String data) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            // Handle any errors while writing to the file
            Log.e("MaininformationActivity", "Error writing to file", e);
        }
    }
}

