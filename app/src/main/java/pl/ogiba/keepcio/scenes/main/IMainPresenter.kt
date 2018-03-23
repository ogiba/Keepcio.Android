package pl.ogiba.keepcio.scenes.main

import pl.ogiba.keepcio.utils.IBasePresenter

/**
 * Created by robertogiba on 18.03.2018.
 */
interface IMainPresenter : IBasePresenter<IMainView> {
    fun logoutUser()
}