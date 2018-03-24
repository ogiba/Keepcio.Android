package pl.ogiba.keepcio.scenes.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.internal.matchers.Equals
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes

/**
 * Created by robertogiba on 24.03.2018.
 */
@PrepareForTest(FirebaseAuth::class, FirebaseUser::class)
@RunWith(PowerMockRunner::class)
class LoginPresenterTest {

    @Mock
    private lateinit var mockedView: ILoginView

    @Mock
    private lateinit var mockedFirebaseAuth: FirebaseAuth

    private lateinit var presenter: LoginPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth::class.java, "getInstance").thenReturn(mockedFirebaseAuth)

        presenter = LoginPresenter()

        presenter.subscribe(mockedView)
    }

    @Test
    fun subscribe() {
        verify(mockedView).onSubscribe()
    }

    @Test
    fun loginUser_all_empty() {
        val email = ""
        val password = ""

        presenter.loginUser(email, password)

        verify(mockedView).onValidationError(safeEq(LoginErrorTypes.EMAIL), anyInt())
    }

    @Test
    fun loginUser_email_filled_password_empty() {
        val email = "mockedEmail"
        val password = ""

        presenter.loginUser(email, password)

        verify(mockedView).onValidationError(safeEq(LoginErrorTypes.PASSWORD), anyInt())
    }

    @Test
    fun loginUser_username_firebase_auth_instnace_not_found() {
        val email = "mockedEmail"
        val password = "mockedPw"

//        presenter.firebaseAuth = mockedFirebaseAuth

        try {
            presenter.loginUser(email, password)
            fail()
        } catch (ex: Exception) {
            //Test passed
        }

        verify(mockedView, never()).onValidationError(safeEq(LoginErrorTypes.PASSWORD), anyInt())
    }

    @Test
    fun changeState() {
    }

    @Test
    fun registerUser() {
    }

    @Test
    fun onComplete() {
    }

    @Test
    fun onAuthStateChanged() {
    }

    fun <T : Any> safeEq(value: T): T = eq(value) ?: value
}