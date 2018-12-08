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


class LoginActivity : AppCompatActivity(), ILoginView {
    private val TAG = "LoginActivity"

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
            } ?: run {
                binding.errorType = null
                binding.errorMessage = null
            }
        })

        viewModel.state.observe(this, Observer {
            if (it == LoginViewState.LOGGED_IN) {
                navigateToMainActivity()
            } else {
                binding.state = it
            }
        })
    }

    private fun checkIfUserLoggedIn(): Boolean = FirebaseAuth.getInstance().currentUser !== null

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
