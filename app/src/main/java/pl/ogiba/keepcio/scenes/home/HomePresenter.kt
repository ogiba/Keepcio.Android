package pl.ogiba.keepcio.scenes.home

import com.google.firebase.auth.FirebaseAuth

class HomePresenter : IHomePresenter {
    private lateinit var view: IHomeView

    override fun subscribe(view: IHomeView) {
        this.view = view

        view.onSubscribe()
    }

    override fun logoutUser() {
        FirebaseAuth.getInstance().signOut()

        view.onLogout()
    }
}
