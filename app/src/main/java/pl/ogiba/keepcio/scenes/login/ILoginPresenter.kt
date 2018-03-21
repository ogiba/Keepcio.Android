package pl.ogiba.keepcio.scenes.login

import pl.ogiba.keepcio.utils.IBasePresenter

/**
 * Created by robertogiba on 19.03.2018.
 */
interface ILoginPresenter : IBasePresenter<ILoginView> {
    fun loginUser(username: String, pw: String)

    fun registerUser(username: String, pw: String, repeatedPw: String)
}