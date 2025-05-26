package francisco.simon.musicplayer.data.repository

import francisco.simon.musicplayer.data.network.ApiService
import org.koin.core.annotation.Single

@Single
class StatusRepository(
    private val apiService: ApiService
) {

    suspend fun getStatus(): String {
        return apiService.getSomething().body()?.get("status") ?: "Failed"
    }

}