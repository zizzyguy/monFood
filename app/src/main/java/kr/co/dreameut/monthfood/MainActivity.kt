package kr.co.dreameut.monthfood

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.coroutines.*
import kr.co.dreameut.alltrot.network.ApiModule
import kr.co.dreameut.monthfood.SP.Companion.getData
import kr.co.dreameut.monthfood.databinding.ActivityMainBinding
import kr.co.dreameut.monthfood.inter.FcmGetSuccessListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class MainActivity : BaseActivity(){

    private lateinit var binding: ActivityMainBinding
    var targetUrl : String = ""
    private lateinit var endData : EndData

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "알림에 동의하셨습니다.",Toast.LENGTH_SHORT)
        } else {
            Toast.makeText(this,"알림에 동의하지 않으셨습니다. 설정에서 알림을 다시 받으실수 있습니다.",Toast.LENGTH_SHORT)
        }
        init()
    }

    override fun loadUrl(url : String) {
        binding.webView.loadUrl(url)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initChannel()
        binding.webView.clearHistory()
        getEndData()
        webSetting(binding.webView)
        adkNotificationPermission()
        init()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }



    private fun initChannel(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if ( notificationManager.getNotificationChannel("monthfood_channel") == null) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "monthfood_channel",
                "월간푸드",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "화면표시로 알림"
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun init(){
        GlobalScope.launch(Dispatchers.Default) { launch(Dispatchers.IO) {
            FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener {
                if(it==null){
                    intent.getStringExtra("tarUrl")?.let { it ->
                        //푸쉬로 왔을때
                        targetUrl = it
                        binding.webView.clearHistory()
                    } ?: run {
                        //normal
                        targetUrl = Const.URL
                        val intent = Intent(this@MainActivity,  SplashActivity::class.java)
                        startActivity(intent)
                    }
                }else{
                    //다이내믹 링크로 왔을때
                    it.link.toString().let { link ->
                        targetUrl = link
                        binding.webView.clearHistory()
                    }
                }
                loginTask()
            }
        } }
    }

    private fun adkNotificationPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                init()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getEndData(){
        ApiModule.getApiService(true)?.endlist()?.enqueue(object : Callback<EndData> {
            override fun onResponse(call: Call<EndData>, response: Response<EndData>) {
                if(response.isSuccessful){
                    endData = response.body()!!
                }
            }

            override fun onFailure(call: Call<EndData>, t: Throwable) {
            }


        })
    }


    private fun loginTask(){
        GlobalScope.launch(Dispatchers.Default){
            launch (Dispatchers.IO) {
                Util.getToken(object : FcmGetSuccessListener {
                    override fun onSuccess(token: String) {
                        SP.setData(this@MainActivity, SP.FCM_ID, token)
                    }

                })
            }.join()

            val job = async(Dispatchers.IO) {
                val kuId = "${System.currentTimeMillis()}|||${Util.getVersionName(this@MainActivity)}|${Util.getVersionCode(this@MainActivity)}|${Build.MODEL}|" +
                        "${Build.VERSION.RELEASE}||||||${SP.getData(this@MainActivity, SP.FCM_ID,"")}||${Util.getUserUniqueKey(this@MainActivity)}||||||||||||||||||||"
//                String(KISA_SEED_CBC.SEED_CBC_Encrypt(
//                    "mysdis2002_key".toByteArray(StandardCharsets.UTF_8),
//                    "mysdis2002_iv".toByteArray(StandardCharsets.UTF_8),
//                    "0".toByteArray(StandardCharsets.UTF_8),
//                    0,
//                    "0".toByteArray(StandardCharsets.UTF_8).size))
//                SeedCipher.encryptString(kuId)
                Base64.encodeToString(kuId.toByteArray(), Base64.DEFAULT)
//                AES256Cipher.AES_Encode(kuId)
            }
            webLogin(job.await())
        }
    }

    private fun webLogin(kuId: String) {
        binding.webView.post {
            targetUrl.let {
                binding.webView.loadUrl(Const.URL+"app?kuId=$kuId&backUrl=${URLEncoder.encode(it,"utf-8")}")
            }
        }
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
                val dialog = EndDialogV2(this, endData.data?.endList)
                dialog.show(supportFragmentManager, "enddialog")
            }
        }
    }
}