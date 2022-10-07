package kr.co.dreameut.monthfood

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kr.co.dreameut.alltrot.network.ApiModule
import kr.co.dreameut.monthfood.databinding.ActivitiySplashBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    var targetUrl: String = ""

    private lateinit var binding: ActivitiySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivitiySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ApiModule.getApiService(false)?.splash()?.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val jo = JSONObject(response.body())
                val url = jo.optString("splash_img", "")
                val move = jo.optString("splash_move")
                runOnUiThread(Runnable {
                    Glide.with(this@SplashActivity).load(url).into(binding.ivSplash)
                })
                GlobalScope.launch(Dispatchers.Default) {
                    launch(Dispatchers.IO) {
                        delay(1500)
                        finish()
                        if(move == "N"){
                            overridePendingTransition(0,0)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                GlobalScope.launch(Dispatchers.Default) {
                    launch(Dispatchers.IO) {
                        delay(1500)
                        finish()
                        overridePendingTransition(0,0)
                    }
                }
            }
        })
    }
}