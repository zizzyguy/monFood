package kr.co.dreameut.monthfood

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kakao.ad.tracker.KakaoAdTracker
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
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivitiySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!KakaoAdTracker.isInitialized) {
            KakaoAdTracker.init(applicationContext, getString(R.string.kakao_ad_track_id))
        }

        if (SP.getData(this, SP.PERMISSION_OK, "N") == "N") {
            showDialogPermission()
        } else {
            goMainActivity()
        }
    }

    private fun showDialogPermission() {
        val dialog = FdialogPermission(this)
        dialog.show(supportFragmentManager, "enddialog")
    }

    fun goMainActivity(){
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
                        val intent = Intent(this@SplashActivity, MainActivity::class.java )
                        startActivity(intent)
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