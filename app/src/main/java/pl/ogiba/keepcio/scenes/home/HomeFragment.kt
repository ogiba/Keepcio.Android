package pl.ogiba.keepcio.scenes.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.main_content.view.*
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.databinding.FragmentHomeBinding
import pl.ogiba.keepcio.scenes.home.adapter.NotesAdapter
import pl.ogiba.keepcio.scenes.login.LoginActivity

class HomeFragment : Fragment(), IHomeView {
    private lateinit var viewModel: NotesViewModel
    private lateinit var binding: FragmentHomeBinding

    private var adapter: NotesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        subscribeUi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()

        viewModel.getNotes()
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
                R.id.menu_logout -> viewModel.logoutUser()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun subscribeUi() {
        viewModel = ViewModelProviders.of(this).get(NotesViewModel::class.java)

        viewModel.notes.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                binding.hasNotes = true
                adapter?.setItems(it)
            } else {
                binding.hasNotes = false
            }
        })

        viewModel.loggedIn.observe(viewLifecycleOwner, Observer { isLogged ->
            if (!isLogged) {
                navigateToLoginActivity()
            }
        })
    }

    private fun setupAdapter() {
        context?.run {
            adapter = NotesAdapter()

            binding.root.lv_notes.layoutManager = LinearLayoutManager(this)
            binding.root.lv_notes.adapter = adapter
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