package pt.atp.cityalert.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.atp.cityalert.R
import pt.atp.cityalert.dataClasses.Note

class NoteAdapter(val list: ArrayList<Note>):RecyclerView.Adapter<LineViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.recycler_line, parent, false)
        return LineViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currentPlace = list[position]

        holder.title_holder.text = currentPlace.title
        holder.subtitle_holder.text = currentPlace.subtitle
        holder.text_holder.text = currentPlace.text
    }
}

class LineViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
    val title_holder = itemView.findViewById<TextView>(R.id.titulo)
    val subtitle_holder = itemView.findViewById<TextView>(R.id.sub_titulo)
    val text_holder = itemView.findViewById<TextView>(R.id.texto_nota)
}
