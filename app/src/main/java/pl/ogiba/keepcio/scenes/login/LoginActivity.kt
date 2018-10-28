package pl.ogiba.keepcio.scenes.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates
import pl.ogiba.keepcio.scenes.main.MainActivity


class LoginActivity : AppCompatActivity(), ILoginView, View.OnClickListener {
    private val TAG = "LoginActivity"

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

        tv_error_box.visibility = View.VISIBLE
        tv_error_box.text = resources.getString(stringId)
    }

    override fun onValidationError(type: LoginErrorTypes, stringId: Int) {
        when (type) {
            LoginErrorTypes.EMAIL -> {
                val errorMessage = resources.getString(R.string.activity_login_username_required,
                        resources.getString(stringId))
                et_user_login.error = errorMessage
            }
            LoginErrorTypes.PASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_password_required,
                        resources.getString(stringId))
                et_user_password.error = errorMessage
            }
            LoginErrorTypes.REPASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_repeat_password_required,
                        resources.getString(stringId))
                et_user_repeat_password.error = errorMessage
            }
        }
    }

    override fun onStateChange(state: LoginViewStates) {
        if (state == LoginViewStates.REGISTER) {
            et_user_repeat_password.visibility = View.VISIBLE
            btn_login.text = resources.getString(R.string.activity_login_btn_register_label)
            tv_register_now.text = resources.getString(R.string.activity_login_back_to_login)
        } else {
            et_user_repeat_password.visibility = View.GONE
            btn_login.text = resources.getString(R.string.activity_login_btn_login_label)
            tv_register_now.text = resources.getString(R.string.activity_login_register_now_label)
        }
    }

    override fun onRegistrationStarted() = performRegisterAction()

    override fun onPasswordNotMatch() {
        et_user_password.error = resources.getString(R.string.activity_login_different_password)
        et_user_repeat_password.error = resources.getString(R.string.activity_login_different_password)
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
        btn_login.setOnClickListener(this)
        tv_register_now.setOnClickListener(this)
    }

    private fun checkIfUserLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser !== null

    private fun performLoginAction() {
        changeViewState()

        val text = et_user_login.text.toString()
        val password = et_user_password.text.toString()

        presenter.loginUser(text, password)
    }

    private fun performRegisterAction() {
        changeViewState()

        val text = et_user_login.text.toString()
        val pw = et_user_password.text.toString()
        val repeatedPw = et_user_repeat_password.text.toString()

        presenter.registerUser(text, pw, repeatedPw)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun changeViewState(inProgress: Boolean = true) {
        this.progress_bar.visibility = if (inProgress) View.VISIBLE else View.GONE
        this.et_user_password.isEnabled = !inProgress
        this.et_user_repeat_password.isEnabled = !inProgress
        this.et_user_login.isEnabled = !inProgress
        this.btn_login.isEnabled = !inProgress
        this.tv_register_now.isEnabled = !inProgress

        this.tv_error_box.run {
            if (this.visibility == View.VISIBLE && inProgress) {
                this.visibility = View.INVISIBLE
            }
        }
    }
}
