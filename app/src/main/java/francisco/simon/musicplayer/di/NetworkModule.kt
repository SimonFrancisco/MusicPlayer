package francisco.simon.musicplayer.di

import francisco.simon.musicplayer.data.MusifySession
import francisco.simon.musicplayer.data.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    @Single
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Single
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, musifySession: MusifySession): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                if (musifySession.getToken() != null) {
                    request.addHeader("Authorization", "Bearer ${musifySession.getToken()}")
                }
                chain.proceed(request.build())
            }
            .build()
    }

    @Single
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val gson = com.google.gson.GsonBuilder()
            .setLenient()
            .create()

        return Retrofit.Builder()
            .baseUrl("http://localhost:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    }

    @Single
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}