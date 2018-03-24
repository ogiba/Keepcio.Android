package pl.ogiba.keepcio.scenes.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates
import pl.ogiba.keepcio.scenes.main.MainActivity
import pl.ogiba.keepcio.utils.bind


class LoginActivity : AppCompatActivity(), ILoginView, View.OnClickListener {
    private val TAG = "LoginActivity"

    private val userEmailView: EditText by bind(R.id.et_user_login)
    private val userPasswordView: EditText by bind(R.id.et_user_password)
    private val userRepeatPwView: EditText by bind(R.id.et_user_repeat_password)
    private val loginBtn: Button by bind(R.id.btn_login)
    private val registerNowView: TextView by bind(R.id.tv_register_now)
    private val progressBar: ProgressBar by bind(R.id.progress_bar)
    private val errorBoxView: TextView by bind(R.id.tv_error_box)

    private lateinit var presenter: ILoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkIfUserLoggedIn()) {
            navigateToMainActivity()
            return
        }

        setContentView(R.layout.activity_login)

        bindListeners()

        presenter = LoginPresenter()
        presenter.subscribe(this)
    }

    override fun onSubscribe() {
        Log.d(TAG, "View subscribed")
    }

    override fun onLoginUser() {
        changeViewState(false)

        navigateToMainActivity()
    }

    override fun onLoginFailed(stringId: Int) {
        changeViewState(false)

        errorBoxView.visibility = View.VISIBLE
        errorBoxView.text = resources.getString(stringId)
    }

    override fun onValidationError(type: LoginErrorTypes, stringId: Int) {
        when (type) {
            LoginErrorTypes.EMAIL -> {
                val errorMessage = resources.getString(R.string.activity_login_username_required,
                        resources.getString(stringId))
                userEmailView.error = errorMessage
            }
            LoginErrorTypes.PASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_password_required,
                        resources.getString(stringId))
                userPasswordView.error = errorMessage
            }
            LoginErrorTypes.REPASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_repeat_password_required,
                        resources.getString(stringId))
                userRepeatPwView.error = errorMessage
            }
        }
    }

    override fun onStateChange(state: LoginViewStates) {
        if (state == LoginViewStates.REGISTER) {
            userRepeatPwView.visibility = View.VISIBLE
            loginBtn.text = resources.getString(R.string.activity_login_btn_register_label)
            registerNowView.text = resources.getString(R.string.activity_login_back_to_login)
        } else {
            userRepeatPwView.visibility = View.GONE
            loginBtn.text = resources.getString(R.string.activity_login_btn_login_label)
            registerNowView.text = resources.getString(R.string.activity_login_register_now_label)
        }
    }

    override fun onRegistrationStarted() = performRegisterAction()

    override fun onPasswordNotMatch() {
        userPasswordView.error = resources.getString(R.string.activity_login_different_password)
        userRepeatPwView.error = resources.getString(R.string.activity_login_different_password)
    }

    override fun onClick(v: View?) {
        if (v == null) {
            return
        }

        when (v.id) {
            R.id.btn_login -> performLoginAction()
            R.id.tv_register_now -> presenter.changeState()
            else -> {
                print("Click event not supported for: " + v.id)
            }
        }
    }

    private fun bindListeners() {
        loginBtn.setOnClickListener(this)
        registerNowView.setOnClickListener(this)
    }

    private fun checkIfUserLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser !== null

    private fun performLoginAction() {
        changeViewState()

        val text = userEmailView.text.toString()
        val password = userPasswordView.text.toString()

        presenter.loginUser(text, password)
    }

    private fun performRegisterAction() {
        changeViewState()

        val text = userEmailView.text.toString()
        val pw = userPasswordView.text.toString()
        val repeatedPw = userRepeatPwView.text.toString()

        presenter.registerUser(text, pw, repeatedPw)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun changeViewState(inProgress: Boolean = true) {
        this.progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
        this.userPasswordView.isEnabled = !inProgress
        this.userRepeatPwView.isEnabled = !inProgress
        this.userEmailView.isEnabled = !inProgress
        this.loginBtn.isEnabled = !inProgress
        this.registerNowView.isEnabled = !inProgress

        this.errorBoxView.let {
            if (it.visibility == View.VISIBLE && inProgress) {
                it.visibility = View.INVISIBLE
            }
        }
    }
}
