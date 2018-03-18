package pl.ogiba.keepcio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity(), IMainView {
    private val TAG = MainActivity::class.simpleName

    private lateinit var presenter: IMainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = MainPresenter()
        presenter.subscribe(this)
    }

    override fun onSubscribe() {
        Log.d(TAG, "View subscribed")
    }
}
