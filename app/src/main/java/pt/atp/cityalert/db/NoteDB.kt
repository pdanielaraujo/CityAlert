package pt.atp.cityalert.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import pt.atp.cityalert.dao.NoteDao
import pt.atp.cityalert.entities.Note

@Database(entities = [Note::class], version = 8, exportSchema = false)
abstract class NoteDB : RoomDatabase(){

    abstract fun noteDao(): NoteDao

    companion object{

        @Volatile
        private var INSTANCE: NoteDB? = null

        @InternalCoroutinesApi
        fun getInstance(context: Context, scope: CoroutineScope): NoteDB{
            synchronized(this){
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            NoteDB::class.java,
                            "note_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
                INSTANCE = instance
                return instance
            }
        }
    }

}