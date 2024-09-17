package com.example.vacationapplication.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacationapplication.R;
import com.example.vacationapplication.database.Repository;
import com.example.vacationapplication.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.MenuItem;
import java.util.List;


public class VacationList extends AppCompatActivity {
    private Repository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_list);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VacationList.this, VacationDetails.class);
                startActivity(intent);
            }
        });
        repository = new Repository(getApplication());
        List<Vacation> allVacations = repository.getAllVacations();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final VacationAdapter vacationAdapter = new VacationAdapter(this);
        recyclerView.setAdapter(vacationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vacationAdapter.setVacations(allVacations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacation_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.addSampleVacations) {
            Repository repo = new Repository(getApplication());
            Vacation vacation = new Vacation(1,"Summer break", "Flamingo", "02/23/24", "03/23/24");
            repo.insert(vacation);
            vacation = new Vacation(2,"Spring break", "Linq", "04/12/24", "04/20/24");
            repo.insert(vacation);
            List<Vacation> allVacations = repository.getAllVacations();
            RecyclerView recyclerView = findViewById(R.id.recyclerview);
            final VacationAdapter vacationAdapter = new VacationAdapter(this);
            recyclerView.setAdapter(vacationAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            vacationAdapter.setVacations(allVacations);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}