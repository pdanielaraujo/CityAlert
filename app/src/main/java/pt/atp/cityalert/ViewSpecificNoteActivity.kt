package pt.atp.cityalert

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.InternalCoroutinesApi
import pt.atp.cityalert.entities.Note
import pt.atp.cityalert.fragments.NotesFragment
import pt.atp.cityalert.viewModel.NoteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ViewSpecificNoteActivity : AppCompatActivity() {

    @InternalCoroutinesApi
    private lateinit var noteViewModel: NoteViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_specific_note)

        // View Model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        val specific_note_title = findViewById<EditText>(R.id.specific_note_title)
        val specific_note_description = findViewById<EditText>(R.id.specific_note_description)
        val save_edit_note_btn = findViewById<Button>(R.id.save_edit_note_btn)

        // Receber os dados enviados do NotesFragment
        val note_id = intent.getIntExtra("noteId", -1).toString()
        val title = intent.getStringExtra("noteTitle")
        val created_on = intent.getStringExtra("noteCreatedOn")
        val description = intent.getStringExtra("noteDescription")
        specific_note_title.append(title)
        specific_note_description.append(description)

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.view_specific_note_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_go_back_foreground)
        toolbar.title = title
        toolbar.setTitleTextColor(resources.getColor(R.color.black, null))
        toolbar.setNavigationOnClickListener{
            finish()
        }

        toolbar.inflateMenu(R.menu.toolbar_view_specific_note_menu)
        toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        save_edit_note_btn.setOnClickListener {
            val replyIntent = Intent()

            val note_title = specific_note_title.text.toString()
            val note_description = specific_note_description.text.toString()

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val formatted = current.format(formatter)
            formatted.toString()

            if(TextUtils.isEmpty(note_title) || TextUtils.isEmpty(note_description)){
                Toast.makeText(this, R.string.empty_string, Toast.LENGTH_SHORT).show()

                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else{
                val arrayData : Array<String> = arrayOf(note_id, note_title, created_on, formatted, note_description)

                replyIntent.putExtra(EXTRA_REPLY, arrayData)
                setResult(Activity.RESULT_OK, replyIntent)

                Toast.makeText(this, R.string.edited_success, Toast.LENGTH_SHORT).show()
                Log.d("result", "$note_id, $note_title,$created_on, $formatted, $note_description")

                finish()
            }
        }
    }

    @InternalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = intent.getIntExtra("noteId", -1)
        val specific_note_title = findViewById<EditText>(R.id.specific_note_title)
        val specific_note_description = findViewById<EditText>(R.id.specific_note_description)
        val save_edit_note_btn = findViewById<Button>(R.id.save_edit_note_btn)
        return when (item.itemId){
            R.id.start_edit_note_btn -> {

                if(!specific_note_title.isEnabled && !specific_note_description.isEnabled){
                    specific_note_title.isEnabled = true
                    specific_note_description.isEnabled = true
                    save_edit_note_btn.visibility = VISIBLE
                }else{
                    specific_note_title.isEnabled = false
                    specific_note_description.isEnabled = false
                    save_edit_note_btn.visibility = INVISIBLE
                }

                true
            }
            R.id.delete_note_btn -> {
                noteViewModel.deleteByid(id)
                finish()

                Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object{
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}