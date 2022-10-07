package kr.co.dreameut.monthfood

import android.app.Application
import android.webkit.WebView
import com.google.firebase.FirebaseApp

class APP : Application() {

    companion object{
        lateinit var  INSTANCE : APP
    }

    init{
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        WebView.setWebContentsDebuggingEnabled(true);
    }
}