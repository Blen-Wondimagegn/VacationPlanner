package com.example.vacationapplication.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.vacationapplication.entities.Vacation;

import java.util.List;



@Dao
public interface VacationDAO {
    @Insert(onConflict= OnConflictStrategy.IGNORE)
    void insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM VACATIONS ORDER BY id ASC")
    List<Vacation> getAllVacations();



}
