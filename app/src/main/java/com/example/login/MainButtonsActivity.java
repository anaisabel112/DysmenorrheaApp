package com.example.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainButtonsActivity extends AppCompatActivity {
    private LinearLayout cyclesLayout;
    private String dni;
    private File userFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainbuttons);// sets the layout MainButtons.xml
        //you connect the XML components to Java so you can work with them
        Button btnAddMenstrualCycle = findViewById(R.id.btnAddMenstrualCycle);
        Button btnExit = findViewById(R.id.btnExit);
        Button btnEditInformation = findViewById(R.id.btnEditInformation);
        cyclesLayout = findViewById(R.id.cyclesLayout);

        // Obtains the id
        dni = getIntent().getStringExtra("dni");
        if (dni == null || dni.isEmpty()) {
            Toast.makeText(this, getString(R.string.dninotfound), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Creates a JSON File based on the id
        userFile = new File(getFilesDir(), dni + ".json");

        // Loads the cycles saved previously
        loadUserCycles();

        // Button to add a new mentrual cycle
        btnAddMenstrualCycle.setOnClickListener(v -> addNewCycle());

        // Exit button
        btnExit.setOnClickListener(v -> startActivity(new Intent(MainButtonsActivity.this, LoginActivity.class)));

        // Edit information button
        btnEditInformation.setOnClickListener(v -> {
            Intent intent = new Intent(MainButtonsActivity.this, MaininformationActivity.class);
            intent.putExtra("dni", dni);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserCycles(); // "Reloads the cycles when the activity comes to the foreground."
    }

    //"Loads the user's cycles from the JSON file."
    private void loadUserCycles() {
        if (!userFile.exists()) {
            saveInitialJson();
        }

        try {
            String jsonString = readFile(userFile);
            if (jsonString.isEmpty()) return;

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray cyclesArray = jsonObject.optJSONArray("cycles");
            if (cyclesArray == null) return;

            // Clear the view before adding the buttons again.
            cyclesLayout.removeAllViews();

            for (int i = 0; i < cyclesArray.length(); i++) {
                createCycleButton(i);
            }
        } catch (Exception e) {
            Log.e("MainButtonsActivity", "Error loading cycles:" + e.getMessage());
        }
    }

    //Adds a new menstrual cycle to the JSON file and displays it on the interface.
    private void addNewCycle() {
        try {
            if (!userFile.exists()) {
                saveInitialJson();
            }

            String jsonString = readFile(userFile);
            JSONObject jsonObject = jsonString.isEmpty() ? new JSONObject() : new JSONObject(jsonString);

            // Ensure that the cycles array exists

            JSONArray cyclesArray = jsonObject.optJSONArray("cycles");
            if (cyclesArray == null) {
                cyclesArray = new JSONArray();
                jsonObject.put("cycles", cyclesArray);
            }

            // Create a new cycle with the correct number

            int cycleNumber = cyclesArray.length() + 1;

            JSONObject newCycle = new JSONObject();
            newCycle.put("cycleNumber", cycleNumber);
            newCycle.put("days", new JSONArray()); // Initially empty

            // Add the new cycle to the array

            cyclesArray.put(newCycle);
            jsonObject.put("cycles", cyclesArray);

            // Save the updated JSON to the file

            writeFile(userFile, jsonObject.toString());

            // Add the button for the new cycle to the interface

            createCycleButton(cyclesArray.length() - 1);

        } catch (Exception e) {
            Log.e("MainButtonsActivity", "Error adding cycle:" + e.getMessage());
        }
    }

    //Creates an initial JSON file if it doesn't exist.
    private void saveInitialJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dni", dni);
            jsonObject.put("cycles", new JSONArray());
            writeFile(userFile, jsonObject.toString());
        } catch (Exception e) {
            Log.e("MainButtonsActivity", "Error creating initial JSON:" + e.getMessage());
        }
    }

    //Reads a JSON file and returns its content as a String.
    private String readFile(File file) {
        StringBuilder jsonString = new StringBuilder();
        try (Scanner scanner = new Scanner(new FileReader(file))) {
            while (scanner.hasNextLine()) {
                jsonString.append(scanner.nextLine());
            }
        } catch (IOException e) {
            Log.e("MainButtonsActivity", "Error reading file: " + e.getMessage());
        }
        return jsonString.toString();
    }

    //Writes data to a JSON file.
    private void writeFile(File file, String data) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            Log.e("MainButtonsActivity", "Error writing to file:" + e.getMessage());
        }
    }

    //Creates a button in the interface to access a specific cycle.
    private void createCycleButton(final int cycleIndex) {
        Button cycleButton = new Button(this);
        String cycletext = getString(R.string.cycle);
        cycleButton.setText(cycletext + (cycleIndex + 1));

        // Custom design: same as the other buttons

        cycleButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Margins (same as the other buttons)

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cycleButton.getLayoutParams();
        params.setMargins(0, 20, 0, 0); // 20dp top margin
        cycleButton.setLayoutParams(params);

        // Background color

        cycleButton.setBackgroundTintList(getResources().getColorStateList(R.color.button_color, getTheme())); // usa colores definidos

        // Text size

        cycleButton.setTextSize(20);
        // To make the text white
        cycleButton.setTextColor(Color.WHITE);

        // Rounded corners: if you use `cornerRadius`, make sure you're using MaterialButton or use a drawable
        // If you're using regular buttons, you can use an XML drawable as the background

        cycleButton.setBackgroundResource(R.drawable.custom_edittext); // If you have a custom drawable

        // Button action
        cycleButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainButtonsActivity.this, CyclescreenActivity.class);
            intent.putExtra("dni", dni);
            intent.putExtra("cycleNumber", cycleIndex + 1);
            startActivity(intent);
        });

        // Add to the view
        cyclesLayout.addView(cycleButton);
    }

}
