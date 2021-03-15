package pt.atp.cityalert.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.InternalCoroutinesApi
import pt.atp.cityalert.AddNoteActivity
import pt.atp.cityalert.R
import pt.atp.cityalert.adapters.NoteAdapter
import pt.atp.cityalert.entities.Note
import pt.atp.cityalert.viewModel.NoteViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * A simple [Fragment] subclass.
 * Use the [NotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotesFragment : Fragment() {

    private lateinit var note_fragment: View
    @InternalCoroutinesApi
    private lateinit var noteViewModel: NoteViewModel

    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }

    @InternalCoroutinesApi
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        note_fragment = inflater.inflate(R.layout.fragment_notes, container, false)

        // Recycler View
        val note_recycler = note_fragment.findViewById<RecyclerView>(R.id.note_recycler)
        val adapter = NoteAdapter(this.requireContext())
        note_recycler.adapter = adapter
        note_recycler.layoutManager = LinearLayoutManager(this.context)

        // View Model
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this.viewLifecycleOwner, { notes ->
            notes.let{
                adapter.setNotes(it)
            }
        })

        // Inflate the layout for this fragment
        return note_fragment
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @InternalCoroutinesApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.getStringArrayExtra(AddNoteActivity.EXTRA_REPLY)?.let {

                val note = Note(titulo = it[0], created_on = it[1], descricao = it[2])
                //val data_criacao = view?.findViewById<TextView>(R.id.data_criacao)


                //data_criacao?.setText(formatted)

                noteViewModel.insert(note)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.view_notes_toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_go_back_foreground)
        toolbar.setTitle(R.string.title_fragment_view_notes)
        toolbar.setTitleTextColor(resources.getColor(R.color.black, null))
        toolbar.setNavigationOnClickListener{
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.layout_fragment, HomeFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }

        toolbar.inflateMenu(R.menu.toolbar_view_notes_menu)
        toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.go_to_add_note_btn -> {
                val intent = Intent(this.context, AddNoteActivity::class.java)
                startActivityForResult(intent, newWordActivityRequestCode)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}