package pl.ogiba.keepcio.scenes.login

import android.app.Activity
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
import pl.ogiba.keepcio.TaskAdapter
import pl.ogiba.keepcio.models.User
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorTypes
import pl.ogiba.keepcio.scenes.login.utils.LoginViewStates
import java.util.concurrent.Executor

/**
 * Created by robertogiba on 24.03.2018.
 */
@PrepareForTest(FirebaseAuth::class, FirebaseUser::class, FirebaseDatabase::class, Log::class)
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
    fun onComplete_user_logged_in() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)
        val mockedTask = mock(TestTask::class.java)

        presenter.registerMode = LoginViewStates.LOGIN

        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        verify(mockedView).onLoginUser()
    }

    @Test
    fun onComplete_user_register_succeed_uid_blank() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)
        val mockedTask = mock(TestTask::class.java)

        presenter.registerMode = LoginViewStates.REGISTER

        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)
        `when`(mockedFirebaseUser.uid).thenReturn("")

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        verify(mockedView, never()).onLoginUser()
        verify(mockedView).onLoginFailed(Matchers.anyInt())
    }

    @Test
    fun onComplete_user_register_succeed_email_null() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)
        val mockedTask = mock(TestTask::class.java)

        presenter.registerMode = LoginViewStates.REGISTER

        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)
        `when`(mockedFirebaseUser.uid).thenReturn("mockedUid")
        `when`(mockedFirebaseUser.email).thenReturn(null)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        verify(mockedView, never()).onLoginUser()
        verify(mockedView).onLoginFailed(Matchers.anyInt())
    }

    @Test
    fun onComplete_user_register_succeed_email_empty() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)
        val mockedTask = mock(TestTask::class.java)

        presenter.registerMode = LoginViewStates.REGISTER

        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)
        `when`(mockedFirebaseUser.uid).thenReturn("mockedUid")
        `when`(mockedFirebaseUser.email).thenReturn("")

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        verify(mockedView, never()).onLoginUser()
        verify(mockedView).onLoginFailed(Matchers.anyInt())
    }

    @Test
    fun onComplete_user_register_succeed_added_to_db() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)
        val mockedTask = mock(TestTask::class.java)
        val mockedDatabase = mock(FirebaseDatabase::class.java)
        val mockedReference = mock(DatabaseReference::class.java)
        val mockedReferenceTask = mock(VoidTask::class.java)

        presenter.registerMode = LoginViewStates.REGISTER

        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)
        `when`(mockedFirebaseUser.uid).thenReturn("mockedUid")
        `when`(mockedFirebaseUser.email).thenReturn("mocked@mocked.mock")
        `when`(mockedDatabase.getReference(Matchers.eq("users"))).thenReturn(mockedReference)
        `when`(mockedReference.child(Matchers.anyString())).thenReturn(mockedReference)
        `when`(mockedReference.setValue(com.nhaarman.mockito_kotlin.any())).thenReturn(mockedReferenceTask)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)
        PowerMockito.mockStatic(FirebaseDatabase::class.java)
        PowerMockito.`when`<FirebaseDatabase>(FirebaseDatabase.getInstance()).thenReturn(mockedDatabase)

        presenter.onComplete(mockedTask)

//        verify(mockedView).onLoginUser()
        verify(mockedView, never()).onLoginFailed(Matchers.anyInt())
    }

    @Test
    fun onAuthStateChanged_user_signed_in() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)

        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)

        PowerMockito.mockStatic(Log::class.java)

        presenter.onAuthStateChanged(mockedFirebaseAuth)

        PowerMockito.verifyStatic()
        Log.d(Matchers.anyString(), Matchers.anyString())
    }

    @Test
    fun onAuthStateChanged_user_signed_out() {
        `when`(mockedFirebaseAuth.currentUser).thenReturn(null)

        PowerMockito.mockStatic(Log::class.java)

        presenter.onAuthStateChanged(mockedFirebaseAuth)

        PowerMockito.verifyStatic()
        Log.d(Matchers.anyString(), Matchers.anyString())
    }

    fun <T : Any> safeEq(value: T): T = eq(value) ?: value

    class TestTask : TaskAdapter<AuthResult>()

    class VoidTask : TaskAdapter<Void>()
}