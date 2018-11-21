package pl.ogiba.keepcio.scenes.home.adapter

import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import pl.ogiba.keepcio.models.Note

class NoteViewModel(val context: Context,
                    val note: Note) : ViewModel() {

    val title = ObservableField<String>(note.title)
}