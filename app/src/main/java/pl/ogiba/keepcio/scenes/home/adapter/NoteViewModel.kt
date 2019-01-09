package pl.ogiba.keepcio.scenes.home.adapter

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import pl.ogiba.keepcio.models.Note
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(private val note: Note) : ViewModel() {

    val title = ObservableField<String>(note.title)
    val description = ObservableField<String>(note.content)
    val dateTime: ObservableField<String>
        get() {
            val formatedDate = SimpleDateFormat("dd-MM-YYYY'T'HH:mm:ss",
                    Locale.getDefault()).format(note.dateTime)

            return ObservableField(formatedDate)
        }

    fun navigateToNote() {
        Log.d("Test", "NoteTitle: ${note.title}")
    }
}