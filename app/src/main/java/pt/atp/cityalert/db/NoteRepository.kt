package pt.atp.cityalert.db

import androidx.lifecycle.LiveData
import pt.atp.cityalert.dao.NoteDao
import pt.atp.cityalert.entities.Note

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note){
        noteDao.insert(note)
    }

}