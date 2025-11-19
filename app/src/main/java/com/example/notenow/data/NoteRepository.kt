package com.example.notenow.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun getNoteById(id: Int): Flow<Note> = noteDao.getNoteById(id)

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun deleteNotes(ids: List<Int>) {
        noteDao.deleteNotes(ids)
    }
}