package pl.ogiba.keepcio.scenes.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.login.utils.LoginError
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorType
import pl.ogiba.keepcio.scenes.login.utils.LoginViewState

class LoginViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val username = ObservableField<String>()
    val pw = ObservableField<String>()
    val repeatedPw = ObservableField<String>()

    val state = MutableLiveData<LoginViewState>().apply {
        this.value = LoginViewState.LOGIN
    }
    val error = MutableLiveData<LoginError?>()

    fun loginUser() {
        Log.d("LoginViewModel", "Login: ${username.get()}; Pw: ${pw.get()}")

        state.value?.run {
            val usernameValue = username.get() ?: ""
            val pwValue = pw.get() ?: ""

            if (this == LoginViewState.LOGIN) {

                when {
                    usernameValue.isNotBlank() and pwValue.isNotBlank() -> {
//                    firebaseAuth.signInWithEmailAndPassword(username, pw).addOnCompleteListener(this)
                        state.value = LoginViewState.IN_PROGRESS
                    }
                    usernameValue.isBlank() -> {
                        error.value = LoginError(LoginErrorType.EMAIL, R.string.activity_login_login_error_label)
                    }
                    pwValue.isBlank() -> {
                        error.value = LoginError(LoginErrorType.PASSWORD, R.string.activity_login_login_error_label)
                    }
                }
            } else {
                val repeatedPwValue = repeatedPw.get() ?: ""

                when {
                    usernameValue.isBlank() -> error.value = LoginError(LoginErrorType.EMAIL,
                            R.string.activity_login_register_error_label)
                    pwValue.isBlank() -> error.value = LoginError(LoginErrorType.PASSWORD,
                            R.string.activity_login_register_error_label)
                    repeatedPwValue.isBlank() -> error.value = LoginError(LoginErrorType.REPASSWORD,
                            R.string.activity_login_register_error_label)
                    else -> {
//                        loginView.onRegistrationStarted()
                    }
                }
            }
        }
    }

    fun changeState() {
        state.value?.run {
            if (this == LoginViewState.LOGIN) {
                state.value = LoginViewState.REGISTER
            } else {
                state.value = LoginViewState.LOGIN
            }
        }
    }
}