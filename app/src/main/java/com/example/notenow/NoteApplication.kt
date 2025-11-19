package com.example.notenow

import android.app.Application
import com.example.notenow.data.NoteDatabase
import com.example.notenow.data.NoteRepository

class NoteApplication : Application() {
    val database by lazy { NoteDatabase.getDatabase(this) }
    val repository by lazy { NoteRepository(database.noteDao()) }
}