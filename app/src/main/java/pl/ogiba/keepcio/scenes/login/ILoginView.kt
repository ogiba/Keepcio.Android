package pl.ogiba.keepcio.scenes.login

import android.support.annotation.StringRes
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.utils.IBaseView

/**
 * Created by robertogiba on 19.03.2018.
 */
interface ILoginView : IBaseView {
    fun onLoginUser()

    fun onLoginFailed(@StringRes stringId: Int)

    fun onValidationError(type: LoginErrorTypes, @StringRes stringId: Int)

    fun onRegistrationStarted()

    fun onPasswordNotMatch()
}