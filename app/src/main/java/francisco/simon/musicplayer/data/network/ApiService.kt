package francisco.simon.musicplayer.data.network

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/status")
    suspend fun getSomething(): Response<Map<String,String>>
}