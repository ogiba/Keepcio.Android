package pl.ogiba.keepcio.scenes.home

import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@PrepareForTest(FirebaseAuth::class)
@RunWith(PowerMockRunner::class)
class HomePresenterTest {

    @Mock
    private lateinit var mockedView: IHomeView

    @Mock
    private lateinit var mockedFirebaseAuth: FirebaseAuth

    private lateinit var presenter: HomePresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        PowerMockito.mockStatic(FirebaseAuth::class.java)
        PowerMockito.`when`<FirebaseAuth>(FirebaseAuth::class.java, "getInstance").thenReturn(mockedFirebaseAuth)

        presenter = HomePresenter()

        presenter.subscribe(mockedView)
    }

    @Test
    fun subscribe() {
        mockedView.onSubscribe()
    }

    @Test
    fun logoutUser() {
        presenter.logoutUser()

        verify(mockedView).onLogout()
    }
}