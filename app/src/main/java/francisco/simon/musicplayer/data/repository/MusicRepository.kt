package francisco.simon.musicplayer.data.repository

import francisco.simon.musicplayer.data.model.Song
import francisco.simon.musicplayer.data.network.ApiService
import francisco.simon.musicplayer.data.network.Resource
import org.koin.core.annotation.Single

@Single
class MusicRepository(
    private val apiService: ApiService
) {

    suspend fun getSongById(id: String): Resource<Song> {
        return try {
            val response = apiService.getSongById(id)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch song data")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }
}