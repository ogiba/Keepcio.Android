package pl.ogiba.keepcio.scenes.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.models.Note

class NotesViewModel : ViewModel() {
    var notes: MutableLiveData<ArrayList<Note>> = MutableLiveData()

    var loggedIn: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        this.value = FirebaseAuth.getInstance().currentUser != null
    }

    fun getNotes() {
        loadNotes()
    }

    fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

        loggedIn.value = false
    }

    private fun loadNotes() {
        val mockedNotes = ArrayList<Note>()
        for (i in 0..1) {
            mockedNotes.add(Note(String.format("Title %s", i), String.format("Desc %s", i)))
        }

        notes.value = mockedNotes
    }
}