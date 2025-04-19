package com.example.login;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class CycleinformationActivity extends AppCompatActivity {
    private EditText periodDate, painIntensity, menstrualFlow, stress, depression, diet,
            alcoholConsumption, exercise, smoking, oralContraceptives, drugs, transcutaneous, heat, acupressure, acupuncture, herbalMedicine;
    private Button btnSave;
    private String dni;
    private int dayNumber, cycleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cycleinformation1);

        periodDate = findViewById(R.id.perioddate);
        painIntensity = findViewById(R.id.painintensity);
        menstrualFlow = findViewById(R.id.menstrualflow);
        stress = findViewById(R.id.stress);
        depression = findViewById(R.id.depression);
        diet = findViewById(R.id.diet);
        alcoholConsumption = findViewById(R.id.alcoholconsumption);
        exercise = findViewById(R.id.exercise);
        smoking = findViewById(R.id.smoking);
        oralContraceptives = findViewById(R.id.oralcontraceptives);
        drugs = findViewById(R.id.drugs);
        transcutaneous = findViewById(R.id.transcutaneous);
        heat = findViewById(R.id.heat);
        acupressure = findViewById(R.id.acupressure);
        acupuncture = findViewById(R.id.acupuncture);
        herbalMedicine = findViewById(R.id.herbalmedicine);
        btnSave = findViewById(R.id.btnsave);

        dni = getIntent().getStringExtra("dni");
        dayNumber = getIntent().getIntExtra("dayNumber", 1);
        cycleNumber = getIntent().getIntExtra("cycleNumber", 1);

        loadData();

        btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                saveData();
                Intent intent = new Intent(CycleinformationActivity.this, CyclescreenActivity.class);
                intent.putExtra("dni", dni);
                intent.putExtra("cycleNumber", cycleNumber);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CycleinformationActivity.this, getString(R.string.fillthefileds), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateFields() {
        // Verificar que todos los campos estén llenos
        return !periodDate.getText().toString().trim().isEmpty() &&
                !painIntensity.getText().toString().trim().isEmpty() &&
                !menstrualFlow.getText().toString().trim().isEmpty() &&
                !stress.getText().toString().trim().isEmpty() &&
                !depression.getText().toString().trim().isEmpty() &&
                !diet.getText().toString().trim().isEmpty() &&
                !alcoholConsumption.getText().toString().trim().isEmpty() &&
                !exercise.getText().toString().trim().isEmpty() &&
                !smoking.getText().toString().trim().isEmpty() &&
                !oralContraceptives.getText().toString().trim().isEmpty() &&
                !drugs.getText().toString().trim().isEmpty() &&
                !transcutaneous.getText().toString().trim().isEmpty() &&
                !heat.getText().toString().trim().isEmpty() &&
                !acupressure.getText().toString().trim().isEmpty() &&
                !acupuncture.getText().toString().trim().isEmpty() &&
                !herbalMedicine.getText().toString().trim().isEmpty();
    }

    private void loadData() {
        if (dni == null || dni.isEmpty()) {
            Toast.makeText(this, getString(R.string.dninotfound), Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(getFilesDir(), dni + ".json");
        if (!file.exists()) return;

        try {
            JSONObject userJson = new JSONObject(new Scanner(new FileReader(file)).useDelimiter("\\Z").next());
            JSONArray cyclesArray = userJson.optJSONArray("cycles");
            if (cyclesArray == null) return;

            for (int i = 0; i < cyclesArray.length(); i++) {
                JSONObject cycle = cyclesArray.getJSONObject(i);
                if (cycle.getInt("cycleNumber") == cycleNumber) {
                    JSONArray daysArray = cycle.getJSONArray("days");
                    for (int j = 0; j < daysArray.length(); j++) {
                        JSONObject day = daysArray.getJSONObject(j);
                        if (day.getInt("dayNumber") == dayNumber) {
                            periodDate.setText(day.optString("periodDate", ""));
                            painIntensity.setText(day.optString("painIntensity", ""));
                            menstrualFlow.setText(day.optString("menstrualFlow", ""));
                            stress.setText(day.optString("stress", ""));
                            depression.setText(day.optString("depression", ""));
                            diet.setText(day.optString("diet", ""));
                            alcoholConsumption.setText(day.optString("alcoholConsumption", ""));
                            exercise.setText(day.optString("exercise", ""));
                            smoking.setText(day.optString("smoking", ""));
                            oralContraceptives.setText(day.optString("oralContraceptives", ""));
                            drugs.setText(day.optString("drugs", ""));
                            transcutaneous.setText(day.optString("transcutaneous", ""));
                            heat.setText(day.optString("heat", ""));
                            acupressure.setText(day.optString("acupressure", ""));
                            acupuncture.setText(day.optString("acupuncture", ""));
                            herbalMedicine.setText(day.optString("herbalMedicine", ""));
                            return;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("CycleinformationActivity", "Error loading cycle data", e);
        }
    }

    private void saveData() {
        if (dni == null || dni.isEmpty()) {
            Toast.makeText(this, getString(R.string.dninotfound), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File file = new File(getFilesDir(), dni + ".json");
            JSONObject userJson = file.exists() ? new JSONObject(new Scanner(new FileReader(file)).useDelimiter("\\Z").next()) : new JSONObject();
            JSONArray cyclesArray = userJson.optJSONArray("cycles");
            if (cyclesArray == null) cyclesArray = new JSONArray();

            JSONObject currentCycle = null;
            for (int i = 0; i < cyclesArray.length(); i++) {
                JSONObject cycle = cyclesArray.getJSONObject(i);
                if (cycle.getInt("cycleNumber") == cycleNumber) {
                    currentCycle = cycle;
                    break;
                }
            }

            if (currentCycle == null) {
                currentCycle = new JSONObject();
                currentCycle.put("cycleNumber", cycleNumber);
                currentCycle.put("days", new JSONArray());
                cyclesArray.put(currentCycle);
            }

            JSONArray daysArray = currentCycle.getJSONArray("days");

            boolean dayExists = false;
            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject existingDay = daysArray.getJSONObject(i);
                if (existingDay.getInt("dayNumber") == dayNumber) {
                    updateDayData(existingDay);
                    dayExists = true;
                    break;
                }
            }

            if (!dayExists) {
                JSONObject newDay = new JSONObject();
                newDay.put("dayNumber", dayNumber);
                updateDayData(newDay);
                daysArray.put(newDay);
            }

            userJson.put("cycles", cyclesArray);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(userJson.toString());
            }
        } catch (Exception e) {
            Log.e("CycleinformationActivity", "Error saving cycle data", e);
        }
    }

    private void updateDayData(JSONObject day) throws JSONException {
        day.put("periodDate", periodDate.getText().toString().trim());
        day.put("painIntensity", painIntensity.getText().toString().trim());
        day.put("menstrualFlow", menstrualFlow.getText().toString().trim());
        day.put("stress", stress.getText().toString().trim());
        day.put("depression", depression.getText().toString().trim());
        day.put("diet", diet.getText().toString().trim());
        day.put("alcoholConsumption", alcoholConsumption.getText().toString().trim());
        day.put("exercise", exercise.getText().toString().trim());
        day.put("smoking", smoking.getText().toString().trim());
        day.put("oralContraceptives", oralContraceptives.getText().toString().trim());
        day.put("drugs", drugs.getText().toString().trim());
        day.put("transcutaneous", transcutaneous.getText().toString().trim());
        day.put("heat", heat.getText().toString().trim());
        day.put("acupressure", acupressure.getText().toString().trim());
        day.put("acupuncture", acupuncture.getText().toString().trim());
        day.put("herbalMedicine", herbalMedicine.getText().toString().trim());
    }
}


