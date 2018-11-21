package pl.ogiba.keepcio.scenes.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.databinding.ListViewNoteItemBinding
import pl.ogiba.keepcio.models.Note

/**
 * Created by robertogiba on 18.03.2018.
 */
class NotesAdapter(val context: Context) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    private val items: ArrayList<Note> = ArrayList()

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.list_view_note_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        items[position].let {
            with(holder) {
                itemView.tag = it
                bind(it)
            }
        }
    }

    public fun setItems(items: ArrayList<Note>) {
        this.items.clear()
        this.items.addAll(items)

        notifyDataSetChanged()
    }

    class NoteViewHolder(private val binding: ListViewNoteItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            with(binding) {
                viewModel = NoteViewModel(itemView.context, note)
                executePendingBindings()
            }
        }
    }
}