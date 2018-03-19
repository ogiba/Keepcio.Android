package pl.ogiba.keepcio.utils

/**
 * Created by robertogiba on 19.03.2018.
 */
interface IBasePresenter<in T: IBaseView> {
    fun subscribe(view: T)
}