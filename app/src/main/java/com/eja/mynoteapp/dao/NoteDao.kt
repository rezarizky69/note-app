package com.eja.mynoteapp.dao

import androidx.room.*
import com.eja.mynoteapp.model.ModelNote

@Dao
interface NoteDao {

    @get:Query("SELECT * FROM notes ORDER BY id DESC")
    val allNote: List<ModelNote?>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(modelNote: ModelNote?)

    @Delete
    fun delete(modelNote: ModelNote?)
}