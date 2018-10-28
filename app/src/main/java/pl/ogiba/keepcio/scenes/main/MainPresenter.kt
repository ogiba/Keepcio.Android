package pl.ogiba.keepcio.scenes.main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by robertogiba on 18.03.2018.
 */
class MainPresenter : IMainPresenter {
    private lateinit var mainView: IMainView

    override fun subscribe(view: IMainView) {
        this.mainView = view

        mainView.onSubscribe()
    }

    override fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

//        mainView.onLogout()
    }
}