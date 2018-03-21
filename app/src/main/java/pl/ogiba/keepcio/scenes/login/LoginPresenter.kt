package pl.ogiba.keepcio.scenes.login

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes


/**
 * Created by robertogiba on 19.03.2018.
 */
class LoginPresenter : ILoginPresenter, FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
    private val TAG = LoginPresenter::class.simpleName

    private lateinit var loginView: ILoginView

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun subscribe(view: ILoginView) {
        this.loginView = view

        this.loginView.onSubscribe()
    }

    override fun loginUser(userName: String, password: String) {
        if (userName.isNotBlank() && password.isNotBlank()) {
            firebaseAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this)
        } else if (userName.isBlank()) {
            loginView.onValidationError(LoginErrorTypes.EMAIL, R.string.activity_login_login_error_label)
        } else if (password.isBlank()) {
            loginView.onValidationError(LoginErrorTypes.PASSWORD, R.string.activity_login_login_error_label)
        }
    }

    override fun onComplete(task: Task<AuthResult>) {
        val isSuccessful = task.isSuccessful
        Log.d(TAG, "signInWithEmail:onComplete:" + isSuccessful)

        if (isSuccessful) {
            val user = firebaseAuth.currentUser
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
}