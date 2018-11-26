package pl.ogiba.keepcio.scenes.login

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.After
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
import pl.ogiba.keepcio.R
import pl.ogiba.keepcio.TaskAdapter
import pl.ogiba.keepcio.scenes.login.utils.LoginErrorType
import pl.ogiba.keepcio.scenes.login.utils.LoginViewState

/**
 * Created by robertogiba on 24.03.2018.
 */
@PrepareForTest(FirebaseAuth::class, FirebaseUser::class, FirebaseDatabase::class,
        Log::class, FirebaseAuthWeakPasswordException::class)
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

    @After
    fun tearDown() {
        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth::class.java, "getInstance").thenReturn(null)
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

        verify(mockedView).onValidationError(safeEq(LoginErrorType.EMAIL), anyInt())
    }

    @Test
    fun loginUser_email_filled_password_empty() {
        val email = "mockedEmail"
        val password = ""

        presenter.loginUser(email, password)

        verify(mockedView).onValidationError(safeEq(LoginErrorType.PASSWORD), anyInt())
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

        verify(mockedView, never()).onValidationError(safeEq(LoginErrorType.PASSWORD), anyInt())
    }

    @Test
    fun changeState_set_register() {
        presenter.registerMode = LoginViewState.LOGIN

        presenter.changeState()

        assertEquals(LoginViewState.REGISTER, presenter.registerMode)
        verify(mockedView).onStateChange(safeEq(LoginViewState.REGISTER))
    }

    @Test
    fun changeState_to_login() {
        presenter.registerMode = LoginViewState.REGISTER

        presenter.changeState()

        assertEquals(LoginViewState.LOGIN, presenter.registerMode)
        verify(mockedView).onStateChange(safeEq(LoginViewState.LOGIN))
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
        verify(mockedTask).addOnCompleteListener(any())
    }

    @Test
    fun registerUser_re_password_empty() {
        val mockedEmail = "test"
        val mockedPw = "test"
        val mockedRePw = ""

        presenter.registerUser(mockedEmail, mockedPw, mockedRePw)

        verify(mockedView).onValidationError(safeEq(LoginErrorType.REPASSWORD),
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

        presenter.registerMode = LoginViewState.LOGIN

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

        presenter.registerMode = LoginViewState.REGISTER

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

        presenter.registerMode = LoginViewState.REGISTER

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

        presenter.registerMode = LoginViewState.REGISTER

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

        presenter.registerMode = LoginViewState.REGISTER

        `when`(mockedTask.isSuccessful).thenReturn(true)
        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)
        `when`(mockedFirebaseUser.uid).thenReturn("mockedUid")
        `when`(mockedFirebaseUser.email).thenReturn("mocked@mocked.mock")
        `when`(mockedDatabase.getReference(Matchers.eq("users"))).thenReturn(mockedReference)
        `when`(mockedReference.child(Matchers.anyString())).thenReturn(mockedReference)
        `when`(mockedReference.setValue(any())).thenReturn(mockedReferenceTask)

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
    fun onComplete_user_login_failed_not_defined() {
        val mockedTask = mock(TestTask::class.java)
        val mockedException = mock(Exception::class.java)

        `when`(mockedTask.exception).thenReturn(mockedException)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        PowerMockito.verifyStatic(Log::class.java)
        Log.w(Matchers.anyString(), Matchers.anyString(), Matchers.any(Throwable::class.java))

        verify(mockedView).onLoginFailed(Matchers.eq(R.string.connection_problem_message))
    }

    @Test
    fun onComplete_user_login_failed() {
        val mockedTask = mock(TestTask::class.java)
        val mockedException = mock(FirebaseException::class.java)

        `when`(mockedTask.exception).thenReturn(mockedException)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        PowerMockito.verifyStatic(Log::class.java)
        Log.w(Matchers.anyString(), Matchers.anyString(), Matchers.any(Throwable::class.java))

        verify(mockedView).onLoginFailed(Matchers.eq(R.string.activity_login_auth_failed))
    }

    @Test
    fun onComplete_user_login_failed_badly_formatted_email() {
        val mockedTask = mock(TestTask::class.java)
        val mockedException = mock(FirebaseAuthInvalidCredentialsException::class.java)

        `when`(mockedTask.exception).thenReturn(mockedException)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        PowerMockito.verifyStatic(Log::class.java)
        Log.w(Matchers.anyString(), Matchers.anyString(), Matchers.any(Throwable::class.java))

        verify(mockedView).onLoginFailed(Matchers.eq(R.string.activity_login_register_error_invalid_email_format))
    }

    @Test
    fun onComplete_user_login_failed_weak_pw() {
        val mockedTask = mock(TestTask::class.java)
        val mockedException = PowerMockito.mock(FirebaseAuthWeakPasswordException::class.java)

        `when`(mockedTask.exception).thenReturn(mockedException)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth)
        PowerMockito.mockStatic(Log::class.java)

        presenter.onComplete(mockedTask)

        PowerMockito.verifyStatic(Log::class.java)
        Log.w(Matchers.anyString(), Matchers.anyString(), Matchers.any(Throwable::class.java))

        verify(mockedView).onLoginFailed(Matchers.eq(R.string.activity_login_register_error_weak_pw))
    }

    @Test
    fun onAuthStateChanged_user_signed_in() {
        val mockedFirebaseUser = mock(FirebaseUser::class.java)

        `when`(mockedFirebaseAuth.currentUser).thenReturn(mockedFirebaseUser)

        PowerMockito.mockStatic(Log::class.java)

        presenter.onAuthStateChanged(mockedFirebaseAuth)

        PowerMockito.verifyStatic(Log::class.java)
        Log.d(Matchers.anyString(), Matchers.anyString())
    }

    @Test
    fun onAuthStateChanged_user_signed_out() {
        `when`(mockedFirebaseAuth.currentUser).thenReturn(null)

        PowerMockito.mockStatic(Log::class.java)

        presenter.onAuthStateChanged(mockedFirebaseAuth)

        PowerMockito.verifyStatic(Log::class.java)
        Log.d(Matchers.anyString(), Matchers.anyString())
    }

    fun <T : Any> safeEq(value: T): T = eq(value) ?: value

    class TestTask : TaskAdapter<AuthResult>()

    class VoidTask : TaskAdapter<Void>()

    class TestCompleteListener : OnCompleteListener<AuthResult> {
        override fun onComplete(p0: Task<AuthResult>) {

        }
    }
}