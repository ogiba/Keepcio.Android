package pl.ogiba.keepcio.scenes.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ListView
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.main.adapter.NotesAdapter
import pl.ogiba.keepcio.models.Note

class MainActivity : AppCompatActivity(), IMainView {
    private val TAG = MainActivity::class.simpleName

    private var toolbar: Toolbar? = null
    private var notesListView: ListView? = null

    private lateinit var presenter: IMainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupAdapter()

        presenter = MainPresenter()
        presenter.subscribe(this)
    }

    private fun bindViews() {
        toolbar = this.findViewById(R.id.toolbar) as Toolbar
        notesListView = this.findViewById(R.id.lv_notes) as ListView
    }

    override fun onSubscribe() {
        Log.d(TAG, "View subscribed")
    }

    private fun setupAdapter() {
        val adapter = NotesAdapter(this)

        notesListView?.adapter = adapter

        val mockedNotes = ArrayList<Note>()
        for (i in 0..1) {
            mockedNotes.add(Note(String.format("Title %s", i), String.format("Desc %s", i)))
        }

        adapter.setItems(mockedNotes)
    }
}
