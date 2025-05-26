package francisco.simon.musicplayer

import android.app.Application
import francisco.simon.musicplayer.di.NetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

class MusicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MusicApp)
            modules(defaultModule, NetworkModule().module)
        }
    }
}