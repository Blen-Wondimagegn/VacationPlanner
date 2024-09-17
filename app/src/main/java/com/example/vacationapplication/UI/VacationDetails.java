package com.example.vacationapplication.UI;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vacationapplication.R;
import com.example.vacationapplication.entities.Excursion;
import com.example.vacationapplication.entities.Vacation;
import com.example.vacationapplication.database.Repository;
import android.view.View;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    String title;
    String hotel;
    String startDate;
    String endDate;
    int vacationID;
    EditText editTitle;
    EditText editHotel;
    EditText editStartDate;
    EditText editEndDate;
    Repository repository;
    Vacation currentVacation;
    int numExcursions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);

        // Retrieve data from the intent
        title = getIntent().getStringExtra("title");
        hotel = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");
        vacationID = getIntent().getIntExtra("id", -1);

        // Initialize UI elements
        editTitle = findViewById(R.id.title);
        editHotel = findViewById(R.id.hotel);
        editStartDate = findViewById(R.id.start_date);
        editEndDate = findViewById(R.id.end_date);

        // Set the fields with the retrieved data
        editTitle.setText(title);
        editHotel.setText(hotel);
        editStartDate.setText(startDate);
        editEndDate.setText(endDate);

        repository = new Repository(getApplication());
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);

        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationId() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacationId", vacationID);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.vacationSave) {
            Vacation vacation;
            String startDate = editStartDate.getText().toString();
            String endDate = editEndDate.getText().toString();

            if (!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
                Toast.makeText(VacationDetails.this, "Invalid date format. Please use MM/dd/yy.", Toast.LENGTH_LONG).show();
                return false;
            }

            if (!isEndDateAfterStartDate(startDate, endDate)) {
                Toast.makeText(VacationDetails.this, "End date must be after start date.", Toast.LENGTH_LONG).show();
                return false;
            }

            if (vacationID == -1) {
                if (repository.getAllVacations().size() == 0) {
                    vacationID = 1;
                } else {
                    vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getId() + 1;
                }
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(), startDate, endDate);
                repository.insert(vacation);
                Toast.makeText(VacationDetails.this, "Vacation saved successfully.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotel.getText().toString(), startDate, endDate);
                    repository.update(vacation);
                    Toast.makeText(VacationDetails.this, "Vacation updated successfully.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        if (item.getItemId() == R.id.vacationDelete) {
            // Find the current vacation based on vacationID
            for (Vacation vac : repository.getAllVacations()) {
                if (vac.getId() == vacationID) currentVacation = vac;
            }

            // Initialize the count of excursions associated with this vacation
            numExcursions = 0;
            for (Excursion excursion : repository.getAllExcursions()) {
                if (excursion.getVacationId() == vacationID) {  // Compare vacationID of the excursion, not the excursion ID
                    ++numExcursions;
                }
            }

            // Check if the vacation can be deleted
            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getTitle() + " was deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        if (item.getItemId() == R.id.addSampleExcursions) {
            if (vacationID == -1)
                Toast.makeText(VacationDetails.this, "Please save vacation before adding excursions", Toast.LENGTH_LONG).show();
            else {
                int excursionID;

                if (repository.getAllExcursions().size() == 0) excursionID = 1;
                else
                    excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getId() + 1;
                Excursion excursion = new Excursion(excursionID, "Art", "08/02/2024", vacationID);
                repository.insert(excursion);
                excursion = new Excursion(++excursionID, "City Tour", "08/02/2024", vacationID);
                repository.insert(excursion);
                RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
                final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
                recyclerView.setAdapter(excursionAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<Excursion> filteredExcursions = new ArrayList<>();
                for (Excursion e : repository.getAllExcursions()) {
                    if (e.getVacationId() == vacationID) filteredExcursions.add(e);
                }
                excursionAdapter.setExcursions(filteredExcursions);
                return true;
            }
        }
        if (item.getItemId() == R.id.notifyVacation) {
            // Parse the start date from the screen
            String startDateFromScreen = editStartDate.getText().toString();
            String endDateFromScreen = editEndDate.getText().toString();
            String myFormat = "MM/dd/yy"; // The date format used in your UI
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            // Parse start date
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = sdf.parse(startDateFromScreen);
                endDate = sdf.parse(endDateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (startDate != null) {
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startDate);
                startCalendar.set(Calendar.HOUR_OF_DAY, 12);
                startCalendar.set(Calendar.MINUTE, 0);
                startCalendar.set(Calendar.SECOND, 0);

                // Create a time picker dialog to allow the user to set a specific time for the start date
                int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
                int startMinute = startCalendar.get(Calendar.MINUTE);
                new TimePickerDialog(VacationDetails.this, (timePicker, selectedHour, selectedMinute) -> {
                    startCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    startCalendar.set(Calendar.MINUTE, selectedMinute);

                    // Schedule the start date notification
                    Long startTrigger = startCalendar.getTimeInMillis();
                    Intent startIntent = new Intent(VacationDetails.this, MyReceiver.class);
                    startIntent.putExtra("key", "Vacation: " + editTitle.getText().toString() + " is starting today!");
                    PendingIntent startSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, startIntent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, startSender); // Set the alarm for the selected start date and time

                    Toast.makeText(VacationDetails.this, "Start date notification set for " + sdf.format(startCalendar.getTime()) + " at " + selectedHour + ":" + String.format("%02d", selectedMinute), Toast.LENGTH_LONG).show();

                }, startHour, startMinute, true).show(); // true means 24-hour time
            } else {
                Toast.makeText(VacationDetails.this, "Invalid start date for vacation", Toast.LENGTH_SHORT).show();
            }

            if (endDate != null) {
                // Get the time portion of the date (default to noon) for the end date
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);
                endCalendar.set(Calendar.HOUR_OF_DAY, 12);
                endCalendar.set(Calendar.MINUTE, 0);
                endCalendar.set(Calendar.SECOND, 0);

                // Create a time picker dialog to allow the user to set a specific time for the end date
                int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);
                int endMinute = endCalendar.get(Calendar.MINUTE);
                new TimePickerDialog(VacationDetails.this, (timePicker, selectedHour, selectedMinute) -> {
                    endCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                    endCalendar.set(Calendar.MINUTE, selectedMinute);

                    // Schedule the end date notification
                    Long endTrigger = endCalendar.getTimeInMillis();
                    Intent endIntent = new Intent(VacationDetails.this, MyReceiver.class);
                    endIntent.putExtra("key", "Vacation: " + editTitle.getText().toString() + " is ending today!");
                    PendingIntent endSender = PendingIntent.getBroadcast(VacationDetails.this, ++MainActivity.numAlert, endIntent, PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, endSender); // Set the alarm for the selected end date and time


                    Toast.makeText(VacationDetails.this, "End date notification set for " + sdf.format(endCalendar.getTime()) + " at " + selectedHour + ":" + String.format("%02d", selectedMinute), Toast.LENGTH_LONG).show();

                }, endHour, endMinute, true).show(); // true means 24-hour time
            } else {
                Toast.makeText(VacationDetails.this, "Invalid end date for vacation", Toast.LENGTH_SHORT).show();
            }

            return true;
        }




        return super.onOptionsItemSelected(item);
    }


    private boolean isValidDateFormat(String date) {
        String datePattern = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isEndDateAfterStartDate(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return end != null && start != null && end.after(start);
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion e : repository.getAllExcursions()) {
            if (e.getVacationId() == vacationID) filteredExcursions.add(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }
}
