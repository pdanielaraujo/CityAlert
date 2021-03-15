package pt.atp.cityalert.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.atp.cityalert.R
import pt.atp.cityalert.entities.Note

class NoteAdapter internal constructor(context: Context):RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()

    class NoteViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val titleViewHolder: TextView = itemView.findViewById(R.id.titulo)
        val dateViewHolder: TextView = itemView.findViewById(R.id.data_criacao)
        val descriptionViewHolder: TextView = itemView.findViewById(R.id.texto_nota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = inflater.inflate(R.layout.recycler_line, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentPlace = notes[position]

        holder.titleViewHolder.text = currentPlace.titulo
        holder.dateViewHolder.text = currentPlace.created_on
        holder.descriptionViewHolder.text = currentPlace.descricao
    }

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return notes.size
    }


}