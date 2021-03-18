package pt.atp.cityalert.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import pt.atp.cityalert.db.NoteDB
import pt.atp.cityalert.db.NoteRepository
import pt.atp.cityalert.entities.Note

@InternalCoroutinesApi
class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    val allNotes: LiveData<List<Note>>

    init {
        val notesDao = NoteDB.getInstance(application, viewModelScope).noteDao()
        repository = NoteRepository(notesDao)
        allNotes = repository.allNotes
    }

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch{
        repository.updateNote(note)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO){
        repository.deleteAll()
    }

    fun deleteByid(id: Int) = viewModelScope.launch(Dispatchers.IO){
        repository.deleteById(id)
    }

}