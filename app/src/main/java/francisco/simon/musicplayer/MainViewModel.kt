package francisco.simon.musicplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import francisco.simon.musicplayer.data.MusifySession
import francisco.simon.musicplayer.data.repository.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(
    private val repository: StatusRepository,val musifySession: MusifySession
) : ViewModel() {
    private val _state  = MutableStateFlow("")
    val state = _state.asStateFlow()

    init {
        getStatus()
    }
    fun isUserLoggedIn(): Boolean {
        return musifySession.getToken() != null
    }
    private fun getStatus() {
        viewModelScope.launch {
            val result = repository.getStatus()
            _state.value = result
        }
    }
}