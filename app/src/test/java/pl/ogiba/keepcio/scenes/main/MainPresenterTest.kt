package pl.ogiba.keepcio.scenes.main

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
class MainPresenterTest {

    @Mock
    private lateinit var mockedView: IMainView

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        presenter = MainPresenter()

        presenter.subscribe(mockedView)
    }

    @Test
    fun subscribe() {
        verify(mockedView).onSubscribe()
    }
}