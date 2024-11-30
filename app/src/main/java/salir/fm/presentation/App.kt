package salir.fm.presentation

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import salir.fm.di.appModule

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {

            androidLogger(level = Level.DEBUG)

            androidContext(this@App)

            modules(appModule)
        }
    }
}