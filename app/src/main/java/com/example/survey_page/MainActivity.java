package com.example.survey_page;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText birthdateEditText;
    Spinner educationLevelSpinner, genderSpinner;
    LinearLayout aiModelLayout;
    Map<String, EditText> dynamicEditTexts = new HashMap<>();
    EditText aiUseCaseEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        birthdateEditText = findViewById(R.id.birthdateEditText);
        educationLevelSpinner = findViewById(R.id.educationLevelSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);
        aiModelLayout = findViewById(R.id.aiModelCheckboxes);

        setupDatePicker();
        setupSpinners();
        setupCheckBoxes();
        // Add a new EditText for "Any use case of AI that is beneficial in daily life?" before the Send Button
        addAIUseCaseEditText();

        // Setup for Send Button
        setupSendButton();

    }

    private void setupSendButton() {
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            try {
                if (validateInputs()) {
                    JSONObject surveyData = new JSONObject();
                    surveyData.put("name", ((EditText) findViewById(R.id.nameEditText)).getText().toString());
                    surveyData.put("surname", ((EditText) findViewById(R.id.surnameEditText)).getText().toString());
                    surveyData.put("birthdate", birthdateEditText.getText().toString());
                    surveyData.put("educationLevel", educationLevelSpinner.getSelectedItem().toString());
                    surveyData.put("city", ((EditText) findViewById(R.id.cityEditText)).getText().toString());
                    surveyData.put("gender", genderSpinner.getSelectedItem().toString());
                    surveyData.put("aiUseCase", aiUseCaseEditText.getText().toString());

                    // Handle dynamic feedback fields
                    JSONObject feedbacks = new JSONObject();
                    for (Map.Entry<String, EditText> entry : dynamicEditTexts.entrySet()) {
                        feedbacks.put(entry.getKey(), entry.getValue().getText().toString());
                    }
                    surveyData.put("feedbacks", feedbacks);

                    // Placeholder for sending data
                    // sendDataToServer(surveyData);

                    // For now, just show the data in a toast
                    Toast.makeText(MainActivity.this, surveyData.toString(), Toast.LENGTH_LONG).show();
                } else {
                    // Show alert
                    Toast.makeText(MainActivity.this, "Please fill name, surname, education level, city, gender, and final survey question fields.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error preparing data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        if (((EditText) findViewById(R.id.nameEditText)).getText().toString().trim().isEmpty() ||
                ((EditText) findViewById(R.id.surnameEditText)).getText().toString().trim().isEmpty() ||
                birthdateEditText.getText().toString().trim().isEmpty() ||
                ((EditText) findViewById(R.id.cityEditText)).getText().toString().trim().isEmpty() ||
                aiUseCaseEditText.getText().toString().trim().isEmpty()){
            return false;
        }
        return true;
    }

    private void sendDataToServer(JSONObject surveyData) {
        String url = "http://10.0.2.2:5000/submit_survey";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, surveyData,
                response -> Toast.makeText(MainActivity.this, "Survey submitted successfully!", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(MainActivity.this, "Failed to submit survey", Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Access the RequestQueue through your singleton class.
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    private void addAIUseCaseEditText() {
        aiUseCaseEditText = new EditText(this);
        aiUseCaseEditText.setHint("Any use case of AI that is beneficial in daily life?");
        aiUseCaseEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        aiUseCaseEditText.setSingleLine(false);
        aiUseCaseEditText.setMaxLines(4); // Adjust based on your needs
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 16, 0, 16); // Add some margin if needed

        // Add this EditText at the end of all views in the LinearLayout
        aiModelLayout.addView(aiUseCaseEditText, layoutParams);
    }


    private void setupDatePicker() {
        birthdateEditText.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, monthOfYear, dayOfMonth) ->
                        birthdateEditText.setText(String.format(Locale.getDefault(), "%d-%d-%d", dayOfMonth, monthOfYear + 1, selectedYear)),
                year, month, day);

        datePickerDialog.show();
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(this,
                R.array.education_levels, android.R.layout.simple_spinner_item);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationLevelSpinner.setAdapter(educationAdapter);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
    }

    private void setupCheckBoxes() {
        String[] aiModels = {"ChatGPT", "Bard", "Claude", "Copilot"};
        for (String model : aiModels) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(model);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> handleCheckboxChange(model, isChecked));
            aiModelLayout.addView(checkBox);
        }
    }

    private void handleCheckboxChange(String aiModelName, boolean isChecked) {
        if (isChecked) {
            // Add EditText
            EditText editText = new EditText(this);
            editText.setHint(aiModelName + " feedback");
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            aiModelLayout.addView(editText);
            dynamicEditTexts.put(aiModelName, editText);
        } else {
            // Remove EditText
            EditText editTextToRemove = dynamicEditTexts.remove(aiModelName);
            if (editTextToRemove != null) {
                aiModelLayout.removeView(editTextToRemove);
            }
        }
    }
}

