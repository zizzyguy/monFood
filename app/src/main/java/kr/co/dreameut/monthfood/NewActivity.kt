package kr.co.dreameut.monthfood

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kr.co.dreameut.monthfood.databinding.ActivityMainBinding
import kr.co.dreameut.monthfood.databinding.ActivityNewBinding

class NewActivity : BaseActivity(){

    private lateinit var binding: ActivityNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webSetting(binding.webView)
        intent.getStringExtra("tarUrl")?.let {
            binding.webView.loadUrl(it)
        }
    }

    override fun loadUrl(url : String) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        val yn = SP.getData(this, SP.BACK_STOP, "N")
        if (yn == "Y") {
            val method = SP.getData(this, SP.BACK_JS, "")
            if (method!!.isNotEmpty()) {
                binding.webView.loadUrl("javascript:$method();")
                SP.setData(this, SP.BACK_JS, "")
            }
        } else {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                super.onBackPressed()
            }
        }
    }
}