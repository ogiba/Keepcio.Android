package pl.ogiba.keepcio.scenes.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import kotlinx.android.synthetic.main.activity_main.*
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.Note
import pl.ogiba.keepcio.scenes.login.LoginActivity
import pl.ogiba.keepcio.scenes.main.adapter.NotesAdapter

class MainActivity : AppCompatActivity(), IMainView {
    private val TAG = MainActivity::class.simpleName

    private lateinit var presenter: IMainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        setupAdapter()
//        setupToolbar()
//
//        presenter = MainPresenter()
//        presenter.subscribe(this)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menu?.let {
//            menuInflater.inflate(R.menu.menu_main, it)
//        }
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.menu_logout -> presenter.logoutUser()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()

    override fun onSubscribe() {
        Log.d(TAG, "View subscribed")
    }

    override fun onLogout() {
        navigateToLoginActivity()
    }

    private fun setupAdapter() {
        val adapter = NotesAdapter(this)

//        lv_notes.adapter = adapter

        val mockedNotes = ArrayList<Note>()
        for (i in 0..1) {
            mockedNotes.add(Note(String.format("Title %s", i), String.format("Desc %s", i)))
        }

        adapter.setItems(mockedNotes)
    }

    private fun setupToolbar() {
//        setSupportActionBar(toolbar)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
