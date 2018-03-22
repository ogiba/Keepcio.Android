package pl.ogiba.keepcio.scenes.login

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.models.User
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes


/**
 * Created by robertogiba on 19.03.2018.
 */
class LoginPresenter : ILoginPresenter, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    private val TAG = LoginPresenter::class.simpleName

    private lateinit var loginView: ILoginView

    private val firebaseAuth = FirebaseAuth.getInstance()
    private var registerMode = false

    override fun subscribe(view: ILoginView) {
        this.loginView = view

        this.loginView.onSubscribe()
    }

    override fun loginUser(username: String, pw: String) {
        if (username.isNotBlank() && pw.isNotBlank() && !registerMode) {
            firebaseAuth.signInWithEmailAndPassword(username, pw).addOnCompleteListener(this)
        } else if (!registerMode) {
            if (username.isBlank()) {
                loginView.onValidationError(LoginErrorTypes.EMAIL, R.string.activity_login_login_error_label)
            } else if (pw.isBlank()) {
                loginView.onValidationError(LoginErrorTypes.PASSWORD, R.string.activity_login_login_error_label)
            }
        } else if (registerMode) {
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

            if (registerMode) {
                //TODO: Check whether user is not null
                addNewUserToDB(user!!)
            }

            loginView.onLoginUser()
        } else {
            val taskException = task.exception
            Log.w(TAG, "signInWithEmail:failed", taskException)

            if (taskException != null) {
                loginView.onLoginFailed(R.string.connection_problem_message)
            } else {
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
            return
        }

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("users")
        val userModel = User(user.email!!)
        reference.child(user.uid).setValue(userModel)
    }
}