package pl.ogiba.keepcio.scenes.login

/**
 * Created by robertogiba on 19.03.2018.
 */
class LoginPresenter : ILoginPresenter {
    private lateinit var loginView: ILoginView

    override fun subscribe(view: ILoginView) {
        this.loginView = view

        this.loginView.onSubscribe()
    }
}