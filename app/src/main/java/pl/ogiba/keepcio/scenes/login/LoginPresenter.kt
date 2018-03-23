package pl.ogiba.keepcio.scenes.login

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.User
import pl.ogiba.keepcio.scenes.login.utils.FirebaseAuthErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates


/**
 * Created by robertogiba on 19.03.2018.
 */
class LoginPresenter : ILoginPresenter, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    private val TAG = LoginPresenter::class.simpleName

    private lateinit var loginView: ILoginView

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var registerMode = LoginViewStates.LOGIN

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
                    loginView.onValidationError(LoginErrorTypes.EMAIL, R.string.activity_login_login_error_label)
                }
                pw.isBlank() -> {
                    loginView.onValidationError(LoginErrorTypes.PASSWORD, R.string.activity_login_login_error_label)
                }
            }
        } else {
            when {
                username.isBlank() -> loginView.onValidationError(LoginErrorTypes.EMAIL,
                        R.string.activity_login_register_error_label)
                pw.isBlank() -> loginView.onValidationError(LoginErrorTypes.PASSWORD,
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
            loginView.onValidationError(LoginErrorTypes.REPASSWORD, R.string.activity_login_register_error_label);
            return
        }

        if (pw != repeatedPw) {
            loginView.onPasswordNotMatch()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(username, pw).addOnCompleteListener(this);
    }

    override fun onComplete(task: Task<AuthResult>) {
        val isSuccessful = task.isSuccessful
        Log.d(TAG, "signInWithEmail:onComplete:" + isSuccessful)

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
            val taskException = task.exception as? FirebaseAuthWeakPasswordException
            Log.w(TAG, "signInWithEmail:failed", taskException)

            taskException?.let {
                if (it.errorCode == FirebaseAuthErrorTypes.weakPassword) {
                    loginView.onLoginFailed(R.string.activity_login_register_error_weak_pw)
                } else {
                    loginView.onLoginFailed(R.string.connection_problem_message)
                }
            } ?: run {
                loginView.onLoginFailed(R.string.activity_login_auth_failed)
            }
        }

    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
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

        user.email?.let { email ->
            val userModel = User(email)
            reference.child(user.uid).setValue(userModel).addOnCompleteListener {
                loginView.onLoginUser()
            }
        } ?: kotlin.run {
            loginView.onLoginFailed(R.string.activity_login_auth_failed)
        }
    }
}