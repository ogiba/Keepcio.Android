package pl.ogiba.keepcio.scenes.login

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.User
import pl.ogiba.keepcio.scenes.login.utils.LoginError
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorType
import pl.ogiba.keepcio.scenes.login.utils.LoginViewState

class LoginViewModel : ViewModel(), FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
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

        state.value?.let {
            val usernameValue = username.get() ?: ""
            val pwValue = pw.get() ?: ""

            if (it == LoginViewState.LOGIN) {
                when {
                    usernameValue.isNotBlank() and pwValue.isNotBlank() -> {
                        firebaseAuth.signInWithEmailAndPassword(usernameValue, pwValue).addOnCompleteListener(this)
                        state.value = LoginViewState.LOGIN_IN_PROGRESS
                        error.value = null
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

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in: ${user.uid}")
        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out")
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        val isSuccessful = task.isSuccessful
        Log.d(TAG, "signInWithEmail:onComplete: $isSuccessful")

        if (isSuccessful) {
            val user = firebaseAuth.currentUser

            if (state.value == LoginViewState.REGISTER_IN_PROGRESS) {
                user?.let {
                    addNewUserToDB(it)
                }
            } else {
                state.value = LoginViewState.LOGGED_IN
            }
        } else {
            val taskException = task.exception
            Log.w(TAG, "signInWithEmail:failed", taskException)

            if (state.value == LoginViewState.LOGIN_IN_PROGRESS) {
                state.value = LoginViewState.LOGIN
            } else if (state.value == LoginViewState.REGISTER_IN_PROGRESS) {
                state.value = LoginViewState.REGISTER
            }

            when (taskException) {
                is FirebaseAuthWeakPasswordException -> {
                    error.value = LoginError(LoginErrorType.DEFAULT, R.string.activity_login_register_error_weak_pw)
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    error.value = LoginError(LoginErrorType.DEFAULT, R.string.activity_login_register_error_invalid_email_format)
                }
                is FirebaseException -> {
                    error.value = LoginError(LoginErrorType.DEFAULT, R.string.activity_login_auth_failed)
                }
                else -> {
                    error.value = LoginError(LoginErrorType.DEFAULT, R.string.connection_problem_message)
                }
            }
        }
    }

    private fun addNewUserToDB(user: FirebaseUser) {
        if (user.uid.isBlank() || user.email.isNullOrBlank()) {
            error.value = LoginError(LoginErrorType.DEFAULT, R.string.activity_login_auth_failed)
            return
        }

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")

        user.email?.let { email ->
            val userModel = User(email)
            reference.child(user.uid).setValue(userModel).addOnCompleteListener {
                state.value = LoginViewState.LOGGED_IN
            }
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}