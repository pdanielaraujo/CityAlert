package pt.atp.cityalert.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.InternalCoroutinesApi
import pt.atp.cityalert.*
import pt.atp.cityalert.adapters.NoteAdapter
import pt.atp.cityalert.entities.Note
import pt.atp.cityalert.viewModel.NoteViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [NotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotesFragment : Fragment(){

    private lateinit var note_fragment: View
    @InternalCoroutinesApi
    private lateinit var noteViewModel: NoteViewModel

    private val newNoteActivityRequestCode = 1

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
        val adapter = NoteAdapter(this.requireContext(), this)
        note_recycler.adapter = adapter
        note_recycler.layoutManager = LinearLayoutManager(this.context)

        // View Model
        noteViewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(viewLifecycleOwner, { notes ->
            notes?.let {
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

        if(requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.getStringArrayExtra(AddNoteActivity.EXTRA_REPLY)?.let {
                val note = Note(
                    titulo = it[0],
                    created_on = it[1],
                    updated_on = it[2],
                    descricao = it[3]
                )

                noteViewModel.insert(note)
            }
        }

        if(requestCode == 123 && resultCode == Activity.RESULT_OK){
            data?.getStringArrayExtra(ViewSpecificNoteActivity.EXTRA_REPLY)?.let {
                val note = Note(
                    id = it[0].toInt(),
                    titulo = it[1],
                    created_on = it[2],
                    updated_on = it[3],
                    descricao = it[4]
                )

                noteViewModel.updateNote(note)
            }
        }
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.view_notes_toolbar)
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

    @InternalCoroutinesApi
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.go_to_add_note_btn -> {
                val intent = Intent(this.context, AddNoteActivity::class.java)
                startActivityForResult(intent, newNoteActivityRequestCode)

                true
            }
            R.id.delete_all_btn -> {
                noteViewModel.deleteAll()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}