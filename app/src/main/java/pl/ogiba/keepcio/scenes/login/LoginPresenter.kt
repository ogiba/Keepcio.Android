package pl.ogiba.keepcio.scenes.login

import androidx.annotation.VisibleForTesting
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.User
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorType
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates


/**
 * Created by robertogiba on 19.03.2018.
 */
class LoginPresenter : ILoginPresenter, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    private val TAG = LoginPresenter::class.simpleName

    private lateinit var loginView: ILoginView

    private val firebaseAuth = FirebaseAuth.getInstance()

    @VisibleForTesting
    var registerMode = LoginViewStates.LOGIN

    override fun subscribe(view: ILoginView) {
        this.loginView = view

        this.loginView.onSubscribe()
    }

    override fun loginUser(username: String, pw: String) {
        if (registerMode == LoginViewStates.LOGIN) {
            when {
                username.isNotBlank() and pw.isNotBlank() -> {
                    firebaseAuth.signInWithEmailAndPassword(username, pw).addOnCompleteListener(this)
                }
                username.isBlank() -> {
                    loginView.onValidationError(LoginErrorType.EMAIL, R.string.activity_login_login_error_label)
                }
                pw.isBlank() -> {
                    loginView.onValidationError(LoginErrorType.PASSWORD, R.string.activity_login_login_error_label)
                }
            }
        } else {
            when {
                username.isBlank() -> loginView.onValidationError(LoginErrorType.EMAIL,
                        R.string.activity_login_register_error_label)
                pw.isBlank() -> loginView.onValidationError(LoginErrorType.PASSWORD,
                        R.string.activity_login_register_error_label)
                else -> {
                    loginView.onRegistrationStarted()
                }
            }
        }
    }

    override fun changeState() {
        registerMode = if (registerMode == LoginViewStates.LOGIN) {
            LoginViewStates.REGISTER
        } else {
            LoginViewStates.LOGIN
        }

        loginView.onStateChange(registerMode)
    }

    override fun registerUser(username: String, pw: String, repeatedPw: String) {
        if (repeatedPw.isBlank()) {
            loginView.onValidationError(LoginErrorType.REPASSWORD, R.string.activity_login_register_error_label);
            return
        }

        if (pw != repeatedPw) {
            loginView.onPasswordNotMatch()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(username, pw).addOnCompleteListener(this)
    }

    override fun onComplete(task: Task<AuthResult>) {
        val isSuccessful = task.isSuccessful
        Log.d(TAG, "signInWithEmail:onComplete: $isSuccessful")

        if (isSuccessful) {
            val user = firebaseAuth.currentUser

            if (registerMode == LoginViewStates.REGISTER) {
                user?.let {
                    addNewUserToDB(it)
                }
            } else {
                loginView.onLoginUser()
            }
        } else {
            val taskException = task.exception
            Log.w(TAG, "signInWithEmail:failed", taskException)

            when (taskException) {
                is FirebaseAuthWeakPasswordException ->
                    loginView.onLoginFailed(R.string.activity_login_register_error_weak_pw)
                is FirebaseAuthInvalidCredentialsException ->
                    loginView.onLoginFailed(R.string.activity_login_register_error_invalid_email_format)
                is FirebaseException ->
                    loginView.onLoginFailed(R.string.activity_login_auth_failed)
                else ->
                    loginView.onLoginFailed(R.string.connection_problem_message)
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

    private fun addNewUserToDB(user: FirebaseUser) {
        if (user.uid.isBlank() || user.email.isNullOrBlank()) {
            loginView.onLoginFailed(R.string.activity_login_auth_failed)
            return
        }

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")

        user.email!!.let { email ->
            val userModel = User(email)
            reference.child(user.uid).setValue(userModel).addOnCompleteListener {
                loginView.onLoginUser()
            }
        }
    }
}