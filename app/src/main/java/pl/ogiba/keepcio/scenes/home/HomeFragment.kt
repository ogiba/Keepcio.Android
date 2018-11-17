package pl.ogiba.keepcio.scenes.home

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.main_content.view.*
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.Note
import pl.ogiba.keepcio.scenes.home.adapter.NotesAdapter
import pl.ogiba.keepcio.scenes.login.LoginActivity

class HomeFragment : Fragment(), IHomeView {

    private val presenter: IHomePresenter = HomePresenter()

    private lateinit var viewModel: NotesViewModel
    private lateinit var binding: ViewDataBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()

        presenter.subscribe(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.let {
            inflater?.inflate(R.menu.menu_main, it)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.menu_logout -> presenter.logoutUser()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSubscribe() {
        Log.d("HomeFragment", "View subscribed")
    }

    override fun onLogout() {
        navigateToLoginActivity()
    }

    private fun setupAdapter() {
        context?.run {
            val adapter = NotesAdapter(this)

            binding.root.lv_notes.adapter = adapter

            val mockedNotes = ArrayList<Note>()
            for (i in 0..1) {
                mockedNotes.add(Note(String.format("Title %s", i), String.format("Desc %s", i)))
            }

            adapter.setItems(mockedNotes)
        }
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)


        val supportActivity = activity as? AppCompatActivity
        supportActivity?.setSupportActionBar(binding.root.toolbar)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        activity?.finish()
    }
}