package pl.ogiba.keepcio.scenes.login

import com.google.firebase.auth.FirebaseAuth

/**
 * Created by robertogiba on 19.03.2018.
 */
class LoginPresenter : ILoginPresenter {
    private lateinit var loginView: ILoginView

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun subscribe(view: ILoginView) {
        this.loginView = view

        this.loginView.onSubscribe()
    }
}