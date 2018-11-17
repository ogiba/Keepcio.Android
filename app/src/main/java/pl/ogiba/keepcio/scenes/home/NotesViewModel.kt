package pl.ogiba.keepcio.scenes.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.ogiba.keepcio.models.Note

class NotesViewModel: ViewModel(){
    private lateinit var notes: MutableLiveData<List<Note>>

    fun getNotes(): LiveData<List<Note>> {
        if (!::notes.isInitialized) {
            notes = MutableLiveData()
        }

        return notes
    }
}