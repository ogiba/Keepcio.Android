package pl.ogiba.keepcio.scenes.login

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Created by robertogiba on 24.03.2018.
 */
@PrepareForTest(FirebaseAuth::class, FirebaseUser::class)
@RunWith(PowerMockRunner::class)
class LoginPresenterTest {

    @Mock
    private var mockedView: ILoginView? = null

    @Mock
    private lateinit var mockedFirebaseAuth: FirebaseAuth

    private lateinit var presenter: LoginPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth::class.java, "getInstance").thenReturn(mockedFirebaseAuth)

        presenter = LoginPresenter()

        mockedView?.let {
            presenter.subscribe(it)
        }
    }

    @Test
    fun subscribe() {
        verify(mockedView)?.onSubscribe()
    }

    @Test
    fun loginUser() {
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

}