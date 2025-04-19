package com.example.login;

import android.content.Intent;//lets start other activities or send data between them
import android.graphics.Color;//allows yo use and manipulate colors
import android.os.Bundle;// use to pass data when the activity is created
import android.util.Log;//wirte the logcat for debugging
import android.widget.Button;//UI component
import android.widget.LinearLayout;//UI component
import android.widget.TextView;//UI component

import androidx.appcompat.app.AppCompatActivity;//supports newer Android feautures while being back-ward-compatible

import org.json.JSONArray;//Use arrays with json format
import org.json.JSONObject;//manage objects with josn

import java.io.File;// create or check files
import java.io.FileReader;// to read a file
import java.io.FileWriter;// to write in a file
import java.util.Scanner;//scan and read input

public class CyclescreenActivity extends AppCompatActivity {
    private LinearLayout layoutButtons;
    private String dni;
    private File userFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Cyclescreen", "onCreate executed");
        setContentView(R.layout.cyclescreen);// initializes the creen layout

        int cycleNumber = getIntent().getIntExtra("cycleNumber", 1);// retrieve cyclenumebr form th eintent
        Log.d("Cyclescreen", "Number of cycle received:" + cycleNumber);

        TextView cycleTextView = findViewById(R.id.cycleTextView);
        String cycletext = getString(R.string.cycle);
        cycleTextView.setText(cycletext + cycleNumber);

        dni = getIntent().getStringExtra("dni");// retrieve the dni from the intent
        userFile = new File(getFilesDir(), dni + ".json");
        //you connect the XML components to Java so you can work with them
        layoutButtons = findViewById(R.id.layoutButtons);
        Button btnAddDay = findViewById(R.id.btnAddDay);
        Button btnReturn = findViewById(R.id.btnreturn);

        loadDaysForCycle(cycleNumber);// loads the previously saved days for the cycle

        btnAddDay.setOnClickListener(v -> {
            Log.d("Cyclescreen", "'Add Day' button pressed");
            addNewDayAndButton(cycleNumber);
        });

        btnReturn.setOnClickListener(v -> {
            Intent intent = new Intent(CyclescreenActivity.this, MainButtonsActivity.class);
            intent.putExtra("dni", dni);
            startActivity(intent);
            Log.d("Cyclescreen", "Returning to MainButtonsActivity");
        });
    }

    private void loadDaysForCycle(int cycleNumber) {
        try {
            Log.d("Cyclescreen", "Cleaning buttons before loading new ones");
            layoutButtons.removeAllViews(); // clear the old buttons

            String jsonString = readFile(userFile);// reads the json file
            JSONObject jsonObject = new JSONObject(jsonString.isEmpty() ? "{}" : jsonString);

            JSONArray cyclesArray = jsonObject.optJSONArray("cycles");
            if (cyclesArray == null) return;

            JSONObject cycleObject = null;
            for (int i = 0; i < cyclesArray.length(); i++) {
                JSONObject tempCycle = cyclesArray.getJSONObject(i);
                if (tempCycle.getInt("cycleNumber") == cycleNumber) {// finds th ecycle with cyclenumber
                    cycleObject = tempCycle;
                    break;
                }
            }

            if (cycleObject == null) return;

            JSONArray daysArray = cycleObject.optJSONArray("days");
            if (daysArray == null) return;

            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject day = daysArray.getJSONObject(i);
                addDayButton(day.getInt("dayNumber"), cycleNumber);// adds a button for each day using addDayButton()
            }
        } catch (Exception e) {
            Log.e("ERROR", "Error loading cycle days:" + e.getMessage());
        }
    }

    private void addNewDayAndButton(int cycleNumber) {
        Log.d("Cyclescreen", "addNewDayAndButton call");

        try {
            if (!userFile.exists()) {
                saveInitialJson();// if the file doesnt exist, it calls saveInitialJson() to create it
            }

            String jsonString = readFile(userFile);
            JSONObject jsonObject = new JSONObject(jsonString.isEmpty() ? "{}" : jsonString);

            JSONArray cyclesArray = jsonObject.optJSONArray("cycles");
            if (cyclesArray == null) {
                cyclesArray = new JSONArray();
            }

            JSONObject cycleObject = null;
            for (int i = 0; i < cyclesArray.length(); i++) {
                JSONObject tempCycle = cyclesArray.getJSONObject(i);
                if (tempCycle.getInt("cycleNumber") == cycleNumber) {// finds the cycleNumber in the json
                    cycleObject = tempCycle;
                    break;
                }
            }

            if (cycleObject == null) {
                cycleObject = new JSONObject();
                cycleObject.put("cycleNumber", cycleNumber);
                cycleObject.put("days", new JSONArray());
                cyclesArray.put(cycleObject);
            }

            JSONArray daysArray = cycleObject.getJSONArray("days");
            int newDayNumber = daysArray.length() + 1;

            JSONObject newDay = new JSONObject();
            newDay.put("dayNumber", newDayNumber);// adds anew day to that cycle
            daysArray.put(newDay);

            cycleObject.put("days", daysArray);
            jsonObject.put("cycles", cyclesArray);

            writeFile(userFile, jsonObject.toString());// writes updated json

            Log.d("Cyclescreen", "Day successfully added to cycle" + cycleNumber);
            addDayButton(newDayNumber, cycleNumber);// adds a button for the new day on the sreen

        } catch (Exception e) {
            Log.e("ERROR", "Error adding day:" + e.getMessage());
        }
    }

    private void addDayButton(int dayNumber, int cycleNumber) {// created a button for the day
        Button dayButton = new Button(this);
        String dayText = getString(R.string.day);
        dayButton.setText(dayText + dayNumber);
        dayButton.setBackgroundResource(R.drawable.rounded_red_button);
        dayButton.setTextColor(Color.WHITE);
        dayButton.setTextSize(20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 20, 0, 0); // margen superior como los demás
        dayButton.setLayoutParams(params);
        dayButton.setOnClickListener(v -> openDayDetails(dayNumber, cycleNumber));
        layoutButtons.addView(dayButton);
    }


    private void saveInitialJson() {// creates the initial json files
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dni", dni);
            JSONArray cyclesArray = new JSONArray();

            JSONObject firstCycle = new JSONObject();
            firstCycle.put("cycleNumber", 1);
            firstCycle.put("days", new JSONArray());

            cyclesArray.put(firstCycle);
            jsonObject.put("cycles", cyclesArray);

            writeFile(userFile, jsonObject.toString());
            Log.d("Cyclescreen", "File saved with the following data:" + jsonObject.toString());
        } catch (Exception e) {
            Log.e("ERROR", "Error creating initial JSON:" + e.getMessage());
        }
    }

    private String readFile(File file) {// read the json
        StringBuilder jsonString = new StringBuilder();
        try {
            if (file.exists()) {
                Scanner scanner = new Scanner(new FileReader(file));
                while (scanner.hasNext()) {
                    jsonString.append(scanner.nextLine());
                }
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString.toString();
    }

    private void writeFile(File file, String jsonString) {// write in the json
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(jsonString);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDayDetails(int dayNumber, int cycleNumber) {// this method is responsible for navigating from CyclescreenActivity to CycleinformationActivity, and it does so while passing some important data.
        Intent intent = new Intent(CyclescreenActivity.this, CycleinformationActivity.class);
        intent.putExtra("dayNumber", dayNumber);
        intent.putExtra("dni", dni);
        intent.putExtra("cycleNumber", cycleNumber);
        startActivity(intent);
    }
}