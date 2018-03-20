package pl.ogiba.keepcio.scenes.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import pl.ogiba.keepcio.R

class LoginActivity : AppCompatActivity(), ILoginView, View.OnClickListener {

    private var userNameView: EditText? = null
    private var userPasswordView: EditText? = null
    private var userRepeatPwView: EditText? = null
    private var loginBtn: Button? = null

    private lateinit var presenter: ILoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bindViews()
        bindListeners()

        presenter = LoginPresenter()
        presenter.subscribe(this)
    }

    override fun onSubscribe() {

    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.tv_login -> Toast.makeText(this, "Login pressed", Toast.LENGTH_LONG).show()
            else -> {
                print("Click event not supported for: " + v.id)
            }
        }
    }

    private fun bindViews() {
        userNameView = findViewById(R.id.et_user_login)
        userPasswordView = findViewById(R.id.et_user_password)
        userRepeatPwView = findViewById(R.id.et_user_repeat_password)
        loginBtn = findViewById(R.id.tv_login)
    }

    private fun bindListeners() {
        loginBtn?.setOnClickListener(this)
    }
}
