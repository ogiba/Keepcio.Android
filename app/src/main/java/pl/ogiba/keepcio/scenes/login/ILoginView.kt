package pl.ogiba.keepcio.scenes.login

import androidx.annotation.StringRes
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorType
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates
import pl.ogiba.keepcio.utils.IBaseView

/**
 * Created by robertogiba on 19.03.2018.
 */
interface ILoginView : IBaseView {
    fun onLoginUser()

    fun onLoginFailed(@StringRes stringId: Int)

    fun onValidationError(type: LoginErrorType, @StringRes stringId: Int)

    fun onStateChange(state: LoginViewStates)

    fun onRegistrationStarted()

    fun onPasswordNotMatch()
}