package pl.ogiba.keepcio.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.Note

/**
 * Created by robertogiba on 18.03.2018.
 */
class NotesAdapter(context: Context) : BaseAdapter() {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val items: ArrayList<Note> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: NotesAdapter.NoteViewHolder
        val rowView: View
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.list_view_note_item, parent, false)
            viewHolder = NoteViewHolder(rowView)
            rowView.tag = viewHolder
        } else {
            rowView = convertView
            viewHolder = rowView.tag as NoteViewHolder
        }

        viewHolder.titleView.text = items[position].title

        return rowView
    }

    override fun getItem(position: Int): Note {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    public fun setItems(items: ArrayList<Note>) {
        this.items.clear()
        this.items.addAll(items)

        notifyDataSetChanged()
    }

    class NoteViewHolder(itemView: View) {
        val titleView: TextView = itemView.findViewById(R.id.tv_note_title) as TextView
    }
}