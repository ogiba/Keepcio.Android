package pl.ogiba.keepcio.scenes.login

import android.app.Activity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates
import java.util.concurrent.Executor

/**
 * Created by robertogiba on 24.03.2018.
 */
@PrepareForTest(FirebaseAuth::class, FirebaseUser::class)
@RunWith(PowerMockRunner::class)
class LoginPresenterTest {

    @Mock
    private lateinit var mockedView: ILoginView

    private lateinit var mockedFirebaseAuth: FirebaseAuth

    private lateinit var presenter: LoginPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        mockedFirebaseAuth = mock(FirebaseAuth::class.java)

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
    fun changeState_set_register() {
        presenter.registerMode = LoginViewStates.LOGIN

        presenter.changeState()

        assertEquals(LoginViewStates.REGISTER, presenter.registerMode)
        verify(mockedView).onStateChange(com.nhaarman.mockito_kotlin.any())
    }

    @Test
    fun changeState_to_login() {
        presenter.registerMode = LoginViewStates.REGISTER

        presenter.changeState()

        assertEquals(LoginViewStates.LOGIN, presenter.registerMode)
        verify(mockedView).onStateChange(com.nhaarman.mockito_kotlin.any())
    }

    @Test
    fun registerUser_creation_called() {
        val mockedEmail = "test"
        val mockedPw = "test"
        val mockedRePw = "test"

        val mockedTask = mock(TestTask::class.java)

        Mockito.`when`(mockedFirebaseAuth.createUserWithEmailAndPassword(Matchers.anyString(),
                Matchers.anyString())).thenReturn(mockedTask)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)

        presenter.registerUser(mockedEmail, mockedPw, mockedRePw)

        verify(mockedFirebaseAuth).createUserWithEmailAndPassword(Matchers.anyString(), Matchers.anyString())
        verify(mockedTask).addOnCompleteListener(com.nhaarman.mockito_kotlin.any())
    }

    @Test
    fun registerUser_re_password_empty() {
        val mockedEmail = "test"
        val mockedPw = "test"
        val mockedRePw = ""

        presenter.registerUser(mockedEmail, mockedPw, mockedRePw)

        verify(mockedView).onValidationError(com.nhaarman.mockito_kotlin.eq(LoginErrorTypes.REPASSWORD),
                Matchers.anyInt())

    }

    @Test
    fun registerUser_re_password_not_matches() {
        val mockedEmail = "test"
        val mockedPw = "test"
        val mockedRePw = "test1"

        presenter.registerUser(mockedEmail, mockedPw, mockedRePw)

        verify(mockedView).onPasswordNotMatch()
    }

    @Test
    fun onComplete() {
    }

    @Test
    fun onAuthStateChanged() {
    }

    fun <T : Any> safeEq(value: T): T = eq(value) ?: value

    class TestTask : Task<AuthResult>() {
        override fun isComplete(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<AuthResult> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<AuthResult> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getResult(): AuthResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addOnSuccessListener(p0: Executor, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun addOnSuccessListener(p0: Activity, p1: OnSuccessListener<in AuthResult>): Task<AuthResult> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isSuccessful(): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getException(): java.lang.Exception? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}