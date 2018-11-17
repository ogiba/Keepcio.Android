package pl.ogiba.keepcio.scenes.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import pl.ogiba.keepcio.R

class MainActivity : AppCompatActivity(), IMainView {
    private val TAG = MainActivity::class.simpleName

    private lateinit var presenter: IMainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp() = findNavController(this, R.id.nav_host_fragment).navigateUp()

    override fun onSubscribe() {
        Log.d(TAG, "View subscribed")
    }
}
