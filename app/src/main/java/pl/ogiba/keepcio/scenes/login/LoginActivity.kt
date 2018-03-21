package pl.ogiba.keepcio.scenes.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.main.MainActivity

class LoginActivity : AppCompatActivity(), ILoginView, View.OnClickListener {

    private var userNameView: EditText? = null
    private var userPasswordView: EditText? = null
    private var userRepeatPwView: EditText? = null
    private var loginBtn: Button? = null

    private lateinit var presenter: ILoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkIfUserLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            return
        }

        setContentView(R.layout.activity_login)

        bindViews()
        bindListeners()

        presenter = LoginPresenter()
        presenter.subscribe(this)
    }

    override fun onSubscribe() {

    }

    override fun onLoginUser() {

    }

    override fun onLoginFailed(stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }

    override fun onValidationError(type: LoginErrorTypes, stringId: Int) {
        when (type) {
            LoginErrorTypes.EMAIL -> {
                val errorMessage = resources.getString(R.string.activity_login_username_required,
                        resources.getString(stringId))
                userNameView?.error = errorMessage
            }
            LoginErrorTypes.PASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_password_required,
                        resources.getString(stringId))
                userPasswordView?.error = errorMessage
            }
            LoginErrorTypes.REPASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_repeat_password_required,
                        resources.getString(stringId))
                userRepeatPwView?.error = errorMessage
            }
        }
    }

    override fun onRegistrationStarted() {
        performRegisterAction()
    }

    override fun onPasswordNotMatch() {
        userPasswordView?.error = resources.getString(R.string.activity_login_different_password)
        userRepeatPwView?.error = resources.getString(R.string.activity_login_different_password)
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.tv_login -> performLoginAction()
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

    private fun checkIfUserLoggedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser !== null
    }

    private fun performLoginAction() {
        val text = userNameView?.text.toString()
        val password = userPasswordView?.text.toString()

        presenter.loginUser(text, password)
    }

    private fun performRegisterAction() {
        val text = userNameView?.text.toString()
        val pw = userPasswordView?.text.toString()
        val repeatedPw = userRepeatPwView?.text.toString()

        presenter.registerUser(text, pw, repeatedPw)
    }
}
