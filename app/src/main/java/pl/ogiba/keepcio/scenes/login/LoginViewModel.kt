package pl.ogiba.keepcio.scenes.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.scenes.login.utils.LoginError
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates

class LoginViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    val state = ObservableField<LoginViewStates>(LoginViewStates.LOGIN)
    val username = ObservableField<String>()
    val pw = ObservableField<String>()
    val repeatedPw = ObservableField<String>()

    val error = MutableLiveData<LoginError?>()

    fun loginUser() {
        Log.d("LoginViewModel", "Login: ${username.get()}; Pw: ${pw.get()}")

        state.get()?.run {
            if (this == LoginViewStates.LOGIN) {

                val usernameValue = username.get() ?: ""
                val pwValue = pw.get() ?: ""

                when {
                    usernameValue.isNotBlank() and pwValue.isNotBlank() -> {
//                    firebaseAuth.signInWithEmailAndPassword(username, pw).addOnCompleteListener(this)
                    }
                    usernameValue.isBlank() -> {
//                    loginView.onValidationError(LoginErrorType.EMAIL, R.string.activity_login_login_error_label)
                    }
                    pwValue.isBlank() -> {
//                    loginView.onValidationError(LoginErrorType.PASSWORD, R.string.activity_login_login_error_label)
//                        error =
                    }
                }
            } else {
//            when {
//                username.isBlank() -> loginView.onValidationError(LoginErrorType.EMAIL,
//                        R.string.activity_login_register_error_label)
//                pw.isBlank() -> loginView.onValidationError(LoginErrorType.PASSWORD,
//                        R.string.activity_login_register_error_label)
//                else -> {
//                    loginView.onRegistrationStarted()
//                }
//            }
            }
        }
    }

    fun changeState() {
        state.get()?.run {
            if (this == LoginViewStates.LOGIN) {
                state.set(LoginViewStates.REGISTER)
            } else {
                state.set(LoginViewStates.LOGIN)
            }
        }
    }
}