package francisco.simon.musicplayer.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import francisco.simon.musicplayer.data.MusifySession
import francisco.simon.musicplayer.data.network.Resource
import francisco.simon.musicplayer.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(private val homeRepository: HomeRepository, private  val musifySession: MusifySession) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchData()
    }
    fun getUserName(): String {
        return musifySession.getUserName()?: "Guest"
    }

    fun fetchData() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            val data = homeRepository.getHomeData()
            when (data) {
                is Resource.Success -> {
                    _state.value = HomeState.Success(data.data)
                }

                is Resource.Error -> {
                    _state.value = HomeState.Error(data.message)
                    _event.emit(HomeEvent.ShowErrorMessage(data.message))
                }
            }
        }
    }

    fun onRetryClicked() {
        fetchData()
    }
}