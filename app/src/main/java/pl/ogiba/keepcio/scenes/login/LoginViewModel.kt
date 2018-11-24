package pl.ogiba.keepcio.scenes.login

import androidx.annotation.VisibleForTesting
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates

class LoginViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    var currentState = MutableLiveData<LoginViewStates>().apply {
        this.value = LoginViewStates.LOGIN
    }

    fun loginUser(username: String, pw: String) {
        if (currentState == LoginViewStates.LOGIN) {
//            when {
//                username.isNotBlank() and pw.isNotBlank() -> {
//                    firebaseAuth.signInWithEmailAndPassword(username, pw).addOnCompleteListener(this)
//                }
//                username.isBlank() -> {
//                    loginView.onValidationError(LoginErrorTypes.EMAIL, R.string.activity_login_login_error_label)
//                }
//                pw.isBlank() -> {
//                    loginView.onValidationError(LoginErrorTypes.PASSWORD, R.string.activity_login_login_error_label)
//                }
//            }
        } else {
//            when {
//                username.isBlank() -> loginView.onValidationError(LoginErrorTypes.EMAIL,
//                        R.string.activity_login_register_error_label)
//                pw.isBlank() -> loginView.onValidationError(LoginErrorTypes.PASSWORD,
//                        R.string.activity_login_register_error_label)
//                else -> {
//                    loginView.onRegistrationStarted()
//                }
//            }
        }
    }

//    fun changeState() {
//        currentState = if (currentState == LoginViewStates.LOGIN) {
//            LoginViewStates.REGISTER
//        } else {
//            LoginViewStates.LOGIN
//        }
//    }
}