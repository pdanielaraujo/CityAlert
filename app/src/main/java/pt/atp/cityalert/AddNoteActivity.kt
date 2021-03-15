package pt.atp.cityalert

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import org.w3c.dom.Text
import pt.atp.cityalert.fragments.HomeFragment
import pt.atp.cityalert.fragments.NotesFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddNoteActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        // Configurações Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.add_note_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_go_back_foreground)
        toolbar.setTitle(R.string.title_activity_add_note)
        toolbar.setTitleTextColor(resources.getColor(R.color.black, null))
        toolbar.setNavigationOnClickListener{
            //val transaction = supportFragmentManager.beginTransaction()
            //transaction.replace(R.id.layout_fragment, NotesFragment())
            //transaction.addToBackStack(null)
            //transaction.commit()
            finish()
        }

        val edit_text_titulo = findViewById<EditText>(R.id.note_title)
        val edit_text_description = findViewById<EditText>(R.id.note_description)

        val add_note_btn = findViewById<Button>(R.id.add_note_btn)

        add_note_btn.setOnClickListener {
            val replyIntent = Intent()

            if(TextUtils.isEmpty(edit_text_titulo.text) && TextUtils.isEmpty(edit_text_description.text)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else{
                val note_title = edit_text_titulo.text.toString()
                val note_description = edit_text_description.text.toString()
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val formatted = current.format(formatter)
                formatted.toString()
                replyIntent.putExtra(EXTRA_REPLY, arrayOf(note_title, formatted, note_description))
                setResult(Activity.RESULT_OK, replyIntent)

                Toast.makeText(this, note_title, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, note_title, Toast.LENGTH_SHORT).show()
                Toast.makeText(this, note_description, Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    companion object{
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}