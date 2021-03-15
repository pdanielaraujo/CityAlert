package pt.atp.cityalert.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")

class Note(
        @PrimaryKey(autoGenerate = true) val id: Int? = null,
        @ColumnInfo(name = "titulo") val titulo: String,
        @ColumnInfo(name = "created_on") val created_on: String,
        @ColumnInfo(name = "updated_on") var updated_on: String? = null,
        @ColumnInfo(name = "descricao") val descricao: String

)