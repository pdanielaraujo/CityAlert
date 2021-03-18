package pt.atp.cityalert.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pt.atp.cityalert.entities.Note


@Dao
interface NoteDao {

    @Query("SELECT * FROM note")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM note")
    suspend fun deleteAll()

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteById(id: Int)

}