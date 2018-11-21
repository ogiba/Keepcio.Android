package pl.ogiba.keepcio.scenes.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.models.Note

class NotesViewModel : ViewModel() {
    private lateinit var notes: MutableLiveData<ArrayList<Note>>

    var loggedIn: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        this.value = FirebaseAuth.getInstance().currentUser != null
    }

    fun getNotes(): LiveData<ArrayList<Note>> {
        if (!::notes.isInitialized) {
            notes = MutableLiveData()
            loadNotes()
        }

        return notes
    }

    fun checkUserState() {
        loggedIn.value = FirebaseAuth.getInstance().currentUser != null
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