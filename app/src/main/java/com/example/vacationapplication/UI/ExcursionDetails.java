package com.example.vacationapplication.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.vacationapplication.database.Repository;
import com.example.vacationapplication.entities.Excursion;
import com.example.vacationapplication.entities.Vacation;
import com.example.vacationapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    String title;
    String excursionDate;
    int excursionID;
    int vacationID;
    EditText editTitle;
    TextView editDate;
    Excursion currentExcursion;
    Repository repository;
    DatePickerDialog.OnDateSetListener dateSetListener;
    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        repository = new Repository(getApplication());
        title = getIntent().getStringExtra("title");
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationId", -1);
        excursionDate = getIntent().getStringExtra("date");


        editTitle = findViewById(R.id.excursionTitle);
        editDate = findViewById(R.id.excursionDate);


        editTitle.setText(title);
        if (excursionDate != null && !excursionDate.isEmpty()) {
            editDate.setText(excursionDate);
        }


        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


        ArrayList<Vacation> vacationArrayList = new ArrayList<>();
        vacationArrayList.addAll(repository.getAllVacations());
        ArrayList<Integer> vacationIdList = new ArrayList<>();
        for (Vacation vacation : vacationArrayList) {
            vacationIdList.add(vacation.getId());
        }
        ArrayAdapter<Integer> vacationIdAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacationIdList);
        Spinner vacationSpinner = findViewById(R.id.vacationSpinner);
        vacationSpinner.setAdapter(vacationIdAdapter);

        // Setup date picker
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateText = editDate.getText().toString();
                if (dateText.equals("")) dateText = "01/01/24"; // Default date
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                try {
                    calendar.setTime(sdf.parse(dateText));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(ExcursionDetails.this, dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editDate.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.saveExcursion) {
            // Validate that the excursion date is formatted correctly
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy"; // The date format used in your UI
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date excursionDate = null;
            try {
                excursionDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                Toast.makeText(ExcursionDetails.this, "Invalid date format. Please use MM/dd/yy.", Toast.LENGTH_LONG).show();
                return true; // Exit if date format is incorrect
            }

            // Retrieve the selected vacation ID from the Spinner
            Spinner vacationSpinner = findViewById(R.id.vacationSpinner);
            if (vacationSpinner.getSelectedItem() != null) {
                vacationID = (int) vacationSpinner.getSelectedItem();
            } else {
                Toast.makeText(ExcursionDetails.this, "Please select a valid vacation.", Toast.LENGTH_LONG).show();
                return true; // Exit if no vacation is selected
            }

            // Validate that the excursion date is during the associated vacation
            Vacation associatedVacation = null;
            for (Vacation vacation : repository.getAllVacations()) {
                if (vacation.getId() == vacationID) {
                    associatedVacation = vacation;
                    break;
                }
            }

            if (associatedVacation != null) {
                Date vacationStartDate = null;
                Date vacationEndDate = null;
                try {
                    vacationStartDate = sdf.parse(associatedVacation.getStartDate());
                    vacationEndDate = sdf.parse(associatedVacation.getEndDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (vacationStartDate != null && vacationEndDate != null) {
                    if (excursionDate.before(vacationStartDate) || excursionDate.after(vacationEndDate)) {
                        Toast.makeText(ExcursionDetails.this, "Excursion date must be within the vacation period (" +
                                associatedVacation.getStartDate() + " to " + associatedVacation.getEndDate() + ").", Toast.LENGTH_LONG).show();
                        return true; // Exit if excursion date is outside vacation period
                    }
                } else {
                    Toast.makeText(ExcursionDetails.this, "Invalid vacation dates.", Toast.LENGTH_SHORT).show();
                    return true; // Exit if vacation dates are invalid
                }
            } else {
                Toast.makeText(ExcursionDetails.this, "Associated vacation not found.", Toast.LENGTH_SHORT).show();
                return true; // Exit if vacation is not found
            }

            // If validation passes, proceed with saving or updating the excursion
            Excursion excursion;
            if (excursionID == -1) {
                if (repository.getAllExcursions().size() == 0) {
                    excursionID = 1;
                } else {
                    excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getId() + 1;
                }
                excursion = new Excursion(excursionID, editTitle.getText().toString(), editDate.getText().toString(), vacationID);
                repository.insert(excursion);
                Toast.makeText(ExcursionDetails.this, "Excursion saved successfully.", Toast.LENGTH_SHORT).show();
            } else {
                excursion = new Excursion(excursionID, editTitle.getText().toString(), editDate.getText().toString(), vacationID);
                repository.update(excursion);
                Toast.makeText(ExcursionDetails.this, "Excursion updated successfully.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.deleteExcursion) {
            Excursion currentExcursion = null;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getId() == excursionID) {
                    currentExcursion = excursion;
                    break;
                }
            }
            if (currentExcursion != null) {
                repository.delete(currentExcursion);
                Toast.makeText(ExcursionDetails.this, currentExcursion.getTitle() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ExcursionDetails.this, "Excursion not found", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        if(item.getItemId() == R.id.notifyExcursion) {
            // Parse the date from the screen
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy"; // The date format used in your UI
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (myDate != null) {
                // Get the time portion of the date (default to noon)
                Calendar excursionCalendar = Calendar.getInstance();
                excursionCalendar.setTime(myDate);
                excursionCalendar.set(Calendar.HOUR_OF_DAY, 12);
                excursionCalendar.set(Calendar.MINUTE, 0);
                excursionCalendar.set(Calendar.SECOND, 0);

                // Create a time picker dialog to allow the user to set a specific time
                int hour = excursionCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = excursionCalendar.get(Calendar.MINUTE);
                new TimePickerDialog(ExcursionDetails.this, (timePicker, selectedHour, selectedMinute) -> {
                    excursionCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    excursionCalendar.set(Calendar.MINUTE, selectedMinute);

                    // Schedule the notification
                    Long trigger = excursionCalendar.getTimeInMillis();
                    Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                    intent.putExtra("key", "Excursion: " + editTitle.getText().toString() + " is happening today!");
                    PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender); // Set the alarm for the selected date and time

                    Toast.makeText(ExcursionDetails.this, "Notification set for " + sdf.format(excursionCalendar.getTime()) + " at " + selectedHour + ":" + String.format("%02d", selectedMinute), Toast.LENGTH_LONG).show();

                }, hour, minute, true).show(); // true means 24 hour time

            } else {
                Toast.makeText(ExcursionDetails.this, "Invalid date for excursion", Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
