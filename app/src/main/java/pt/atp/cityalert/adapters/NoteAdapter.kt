package pt.atp.cityalert.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.atp.cityalert.R
import pt.atp.cityalert.ViewSpecificNoteActivity
import pt.atp.cityalert.entities.Note
import pt.atp.cityalert.fragments.NotesFragment

class NoteAdapter internal constructor(val context: Context, val fragment: NotesFragment):RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    inner class NoteViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val titleViewHolder: TextView = itemView.findViewById(R.id.titulo)
        val datecreatedViewHolder: TextView = itemView.findViewById(R.id.data_criacao)
        val dateupdatedViewHolder: TextView = itemView.findViewById(R.id.data_atualizacao)
        val descriptionViewHolder: TextView = itemView.findViewById(R.id.texto_nota)

        init{

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_line, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]

        holder.titleViewHolder.text = holder.itemView.context.getString(R.string.note_title_bind_view_holder, currentNote.titulo)
        holder.datecreatedViewHolder.text = holder.itemView.context.getString(R.string.note_created_bind_view_holder, currentNote.created_on)
        holder.dateupdatedViewHolder.text = holder.itemView.context.getString(R.string.note_updated_bind_view_holder, currentNote.updated_on)
        holder.descriptionViewHolder.text = holder.itemView.context.getString(R.string.note_description_bind_view_holder, currentNote.descricao)

        if(holder.dateupdatedViewHolder.text.isEmpty()){
            holder.datecreatedViewHolder.visibility = INVISIBLE
            holder.dateupdatedViewHolder.visibility = VISIBLE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(this.fragment.context, ViewSpecificNoteActivity::class.java).apply {
                putExtra("noteId", currentNote.id)
                putExtra("noteTitle", currentNote.titulo)
                putExtra("noteCreatedOn", currentNote.created_on)
                putExtra("noteDescription", currentNote.descricao)
            }
            fragment.startActivityForResult(intent, 123)
        }

    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

}
