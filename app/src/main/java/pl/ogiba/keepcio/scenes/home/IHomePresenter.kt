package pl.ogiba.keepcio.scenes.home

import pl.ogiba.keepcio.utils.IBasePresenter

interface IHomePresenter : IBasePresenter<IHomeView> {
    fun logoutUser()
}