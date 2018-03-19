package pl.ogiba.keepcio.scenes.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import pl.ogiba.keepcio.R

class LoginActivity : AppCompatActivity(), ILoginView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    override fun onSubscribe() {
        
    }
}
