package pl.ogiba.keepcio.scenes.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.databinding.ActivityLoginBinding
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorType
import pl.ogiba.keepcio.scenes.login.utils.LoginViewState
import pl.ogiba.keepcio.scenes.main.MainActivity


class LoginActivity : AppCompatActivity(), ILoginView, View.OnClickListener {
    private val TAG = "LoginActivity"

    private lateinit var presenter: ILoginPresenter
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkIfUserLoggedIn()) {
            navigateToMainActivity()
            return
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        bindListeners()
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

    override fun onValidationError(type: LoginErrorType, stringId: Int) {
        when (type) {
            LoginErrorType.EMAIL -> {
                val errorMessage = resources.getString(R.string.activity_login_username_required,
                        resources.getString(stringId))
                et_user_login.error = errorMessage
            }
            LoginErrorType.PASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_password_required,
                        resources.getString(stringId))
                et_user_password.error = errorMessage
            }
            LoginErrorType.REPASSWORD -> {
                val errorMessage = resources.getString(R.string.activity_login_repeat_password_required,
                        resources.getString(stringId))
                et_user_repeat_password.error = errorMessage
            }
        }
    }

    override fun onStateChange(state: LoginViewState) {
        if (state == LoginViewState.REGISTER) {
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
        binding.viewModel = viewModel

        viewModel.error.observe(this, Observer {
            it?.run {
                when (this.type) {
                    LoginErrorType.EMAIL -> {

                        val errorMessage = resources.getString(R.string.activity_login_username_required,
                                resources.getString(data as Int))

                        binding.errorType = this.type
                        binding.errorMessage = errorMessage
                    }
                    LoginErrorType.PASSWORD -> {
                        val errorMessage = resources.getString(R.string.activity_login_password_required,
                                resources.getString(data as Int))

                        binding.errorType = this.type
                        binding.errorMessage = errorMessage
                    }
                    LoginErrorType.REPASSWORD -> {
                        val errorMessage = resources.getString(R.string.activity_login_repeat_password_required,
                                resources.getString(data as Int))

                        binding.errorType = this.type
                        binding.errorMessage = errorMessage
                    }
                    else -> {
                        val errorMessage = if (data is Int) {
                            resources.getString(data)
                        } else {
                            data as String
                        }

                        binding.errorType = this.type
                        binding.errorMessage = errorMessage
                    }
                }
            }
        })

        viewModel.state.observe(this, Observer {
            binding.state = it
        })
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
