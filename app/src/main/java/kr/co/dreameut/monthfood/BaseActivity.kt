package kr.co.dreameut.monthfood

import android.Manifest
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.play.core.review.ReviewManagerFactory
import java.io.File
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    val TAG = "zest"
    val RC_CALL = 645
    val RC_STORAGE = 6878
    private lateinit var mWebView: WebView
    var className = ""
    var mWebViewImageUpload: ValueCallback<Array<Uri>>? = null
    var mCameraPhotoPath: String? = null

    var arUrl: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                try {
                    if(result.data?.getStringExtra("url") !=null){
                        result.data!!.getStringExtra("url")?.let { loadUrl(it) }
                    }
                } catch (e: Exception) {
                }
            })

    var arImageChooser: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (result.resultCode == RESULT_OK) {
                    val intent = result.data

                    if (intent == null) { //바로 사진을 찍어서 올리는 경우
                        val results = arrayOf(Uri.parse(mCameraPhotoPath))
                        mWebViewImageUpload!!.onReceiveValue(results!!)
                    } else { //사진 앱을 통해 사진을 가져온 경우
                        val results = intent!!.data!!
                        mWebViewImageUpload!!.onReceiveValue(arrayOf(results!!))
                    }
                } else { //취소 한 경우 초기화
                    mWebViewImageUpload!!.onReceiveValue(null)
                    mWebViewImageUpload = null
                }
            }
        )

    abstract fun loadUrl(url : String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityManager.actList.add(this)
        className = componentName.shortClassName.replace(".", "")
    }

    override fun onResume() {
        super.onResume()
        val openReload = SP.getData(this, SP.OPEN_RELOAD, "")
        if (openReload == "Y") {
            SP.setData(this, SP.OPEN_RELOAD, "")
            mWebView.reload()
        }

        val closeReload = SP.getData(this, SP.CLOSE_RELOAD, "");
        if (closeReload!!.isNotEmpty()) {
            SP.setData(this, SP.CLOSE_RELOAD, "")
            mWebView.loadUrl("javascript:webview_reload(${closeReload})")
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show()
        }
    }

    open fun getUserAgent(webSettings: WebSettings): String {
        return "${webSettings.userAgentString} monFoodApp/android monFoodAppVersion/${
            Util.getVersionName(
                this
            )
        } monFoodAppVersionCode/${Util.getVersionCode(this)}"
    }

    @SuppressLint("SetJavaScriptEnabled")
    open fun webSetting(webView: WebView) {
        mWebView = webView
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val webSetting = webView.settings
        webSetting.cacheMode = WebSettings.LOAD_DEFAULT
        webSetting.javaScriptEnabled = true
        webSetting.databaseEnabled = true
        webSetting.domStorageEnabled = true
        webSetting.defaultTextEncodingName = "UTF-8"
        webSetting.displayZoomControls = false
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.builtInZoomControls = false
        webSetting.allowContentAccess = true
        webSetting.allowFileAccess = true
        webSetting.userAgentString = getUserAgent(webSetting)
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidApp")
        webView.webViewClient = WebClient()
        webView.webChromeClient = WebChromeClient()
        webSetting.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
    }

    inner class WebChromeClient : android.webkit.WebChromeClient() {
        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?,
        ): Boolean {
            AlertDialog.Builder(this@BaseActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which -> result!!.confirm() }
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        override fun onJsPrompt(
            view: WebView?,
            url: String?,
            message: String?,
            defaultValue: String?,
            result: JsPromptResult?,
        ): Boolean {
            AlertDialog.Builder(this@BaseActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which -> result!!.confirm() }
                .setNegativeButton(android.R.string.cancel) { dialog, which -> result!!.cancel() }
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        override fun onJsConfirm(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?,
        ): Boolean {
            AlertDialog.Builder(this@BaseActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which -> result!!.confirm() }
                .setNegativeButton(android.R.string.cancel) { dialog, which -> result!!.cancel() }
                .setCancelable(false)
                .create()
                .show()
            return true
        }

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?,
        ): Boolean {

            // 웹뷰 만들기
            var childWebView = WebView(view!!.context)

            // 부모 웹뷰와 동일하게 웹뷰 설정
            childWebView.run {
                settings.run {
                    javaScriptEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                    setSupportMultipleWindows(true)
                }
                layoutParams = view.layoutParams
                webViewClient = view.webViewClient
                webChromeClient = view.webChromeClient
            }

            // 화면에 추가하기
            mWebView.addView(childWebView)
            // TODO: 화면 추가 이외에 onBackPressed() 와 같이
            //       사용자의 내비게이션 액션 처리를 위해
            //       별도 웹뷰 관리를 권장함
            //   ex) childWebViewList.add(childWebView)

            // 웹뷰 간 연동
            val transport = resultMsg?.obj as WebView.WebViewTransport
            transport.webView = childWebView
            resultMsg?.sendToTarget()
            return true
        }


        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?,
        ): Boolean {
            val permission01 = ContextCompat.checkSelfPermission(this@BaseActivity, CAMERA)
            val permission02 =
                ContextCompat.checkSelfPermission(this@BaseActivity, READ_EXTERNAL_STORAGE)
            if (permission01 == PackageManager.PERMISSION_DENIED || permission02 == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this@BaseActivity,
                    arrayOf(Manifest.permission.CAMERA, READ_EXTERNAL_STORAGE),
                    RC_STORAGE
                )
            } else {
                try {
                    mWebViewImageUpload = filePathCallback!!
                    var takePictureIntent: Intent?
                    takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (takePictureIntent.resolveActivity(packageManager) != null) {
                        var photoFile: File?

                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath)

                        if (photoFile != null) {
                            mCameraPhotoPath = "file:${photoFile.absolutePath}"
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile))
                        } else takePictureIntent = null
                    }
                    val contentSelectionIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    contentSelectionIntent.type = "image/*"

                    var intentArray: Array<Intent?>

                    if (takePictureIntent != null) intentArray = arrayOf(takePictureIntent)
                    else intentArray = takePictureIntent?.get(0)!!

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "사용할 앱을 선택해주세요.")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    arImageChooser.launch(chooserIntent)
                } catch (e: Exception) {
                }
                return true
            }
            return true
        }

        private fun createImageFile(): File? {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "img_" + timeStamp + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        override fun onCloseWindow(window: WebView?) {
            super.onCloseWindow(window)
            mWebView.removeView(window)
        }
    }

    inner class WebClient : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?,
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?,
        ): Boolean {
            val url = request?.url.toString()
            if (request?.url?.scheme == "intent") {
                try {
                    // Intent 생성
                    val intent = Intent.parseUri(request.url.toString(), Intent.URI_INTENT_SCHEME)

                    // 실행 가능한 앱이 있으면 앱 실행
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                        Log.d(TAG, "ACTIVITY: ${intent.`package`}")
                        return true
                    } else {
                        val packageName = intent.getPackage()
                        if (packageName != null) {
                            startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$packageName")))
                            return true
                        }
                    }

                    // Fallback URL이 있으면 현재 웹뷰에 로딩
                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                    if (fallbackUrl != null) {
                        view?.loadUrl(fallbackUrl)
                        Log.d(TAG, "FALLBACK: $fallbackUrl")
                        return true
                    }

                    Log.e(TAG, "Could not parse anythings")

                } catch (e: URISyntaxException) {
                    Log.e(TAG, "Invalid intent request", e)
                }
                return true
            }

            if (request?.url?.scheme == "https") {
                view?.loadUrl(request?.url.toString())
                return true
            }

            if (request?.url?.scheme == "tel") {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(request?.url.toString()))
                startActivity(intent)
                return true
            }

            if (request?.url?.scheme == "mailto") {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(request?.url.toString()))
                startActivity(intent)
                return true
            }


            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:") && !url.startsWith("intent")) {
                //외부 앱에 대한 URL scheme 대응
                var intent: Intent? = null
                return try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME) //IntentURI처리
                    val uri = Uri.parse(intent.dataString)
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                    true
                } catch (ex: URISyntaxException) {
                    false
                } catch (e: ActivityNotFoundException) {
                    if (intent == null) return false
                    //handleNotFoundPaymentScheme()에서 처리되지 않은 것 중, url로부터 package정보를 추출할 수 있는 경우 market이동 처리
                    val packageName = intent.getPackage()
                    if (packageName != null) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                        return true
                    }
                    false
                }
            }

            return false
        }

    }

    inner class WebAppInterface(private val mContext: Context) {

        private val handler = Handler(Looper.myLooper()!!)

        @JavascriptInterface
        fun hideKeyboard() {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(mWebView.windowToken, 0)
        }

        @JavascriptInterface
        fun showKeyboard() {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        @JavascriptInterface
        fun isNewWin(): String {
            return if (this@BaseActivity.className == "NewActivity") "Y" else "N"
        }

        @JavascriptInterface
        fun canOnBackPressed(yn: String) {
            SP.setData(this@BaseActivity, SP.BACK_STOP, yn)
        }

        @JavascriptInterface
        fun jsOnBackPressed(method: String) {
            try {
                SP.setData(this@BaseActivity, SP.BACK_JS, method)
                this@BaseActivity.onBackPressed()
            } catch (e: Exception) {

            }
        }

        @JavascriptInterface
        fun clearCache() {
            handler.post {
                mWebView.clearHistory()
                mWebView.clearCache(true)
                mWebView.clearFormData()

                val files = cacheDir.listFiles()
                if (!files.isNullOrEmpty()) {
                    for (file in files) {
                        file.delete()
                    }
                }
            }
        }

        @JavascriptInterface
        fun winClose(openReload: String) {
            handler.post {
                if (openReload == "Y") SP.setData(this@BaseActivity, SP.OPEN_RELOAD, openReload)
                finish()
            }
        }

        @JavascriptInterface
        fun closeReload(closeReload: String) {
            handler.post {
                if (closeReload.isNotEmpty()) SP.setData(this@BaseActivity,
                    SP.CLOSE_RELOAD,
                    closeReload)
                finish()
            }
        }

        @JavascriptInterface
        fun reload() {
            handler.post { mWebView.reload() }
        }

        @JavascriptInterface
        fun toast(str: String) {
            handler.post { Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show() }
        }

        @JavascriptInterface
        fun saveData(key: String, value: String) {
            SP.setData(this@BaseActivity, key, value)
        }

        @JavascriptInterface
        fun getData(key: String): String {
            return SP.getData(this@BaseActivity, key, "")!!
        }

        @SuppressLint("HardwareIds")
        @JavascriptInterface
        fun getUserUniqueKey(): String {
            AdvertisingIdClient.getAdvertisingIdInfo(mContext).id?.let {
                if (it == "00000000-0000-0000-0000-000000000000" || it.isEmpty()) {
                    return Settings.Secure.getString(
                        mContext.contentResolver,
                        Settings.Secure.ANDROID_ID
                    )
                } else {
                    return it
                }
            } ?: run {
                return Settings.Secure.getString(
                    mContext.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            }
        }

        @JavascriptInterface
        fun goNextWebView(url: String) {
            handler.post {
                val intent = Intent(mContext, NewActivity::class.java)
                intent.putExtra("tarUrl", url)
                arUrl.launch(intent)
            }
        }

        @JavascriptInterface
        fun finishForUrl(url: String) {
            handler.post {
                goHome()
                if(this@BaseActivity.className == "NewActivity"){
                    val intent = Intent()
                    intent.putExtra("url",url)
                    setResult(1000, intent)
                    finish()
                }else{
                    mWebView.loadUrl(url)
                }
//                val intent = Intent(this@BaseActivity, MainActivity::class.java)
//                intent.putExtra("url", url)
//                intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
//                startActivity(intent)
//                finish()
            }
        }

        @JavascriptInterface
        fun goBrowser(url: String) {
            handler.post {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }


        @JavascriptInterface
        fun tel(tel: String) {
            val permission = ContextCompat.checkSelfPermission(this@BaseActivity, CALL_PHONE)
            if (permission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    this@BaseActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    RC_CALL
                )
            } else {
                handler.post {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$tel"))
                    startActivity(intent)
                }
            }
        }

        @JavascriptInterface
        fun finish() {
            handler.post {
                this@BaseActivity.finish()
            }
        }

        @JavascriptInterface
        fun goBack() {
            handler.post {
                if (mWebView.canGoBack()) {
                    mWebView.goBack()
                } else {
                    finish()
                }
            }
        }

        @JavascriptInterface
        fun goHome() {
            handler.post {
                ActivityManager.allActivityFinishExceptMain()
            }
        }


        @JavascriptInterface
        fun logout() {
            SP.setData(this@BaseActivity, SP.M_KEY, "")
            SP.setData(this@BaseActivity, SP.M_NUM, "")
            handler.post {
                val intent = Intent(this@BaseActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)
            }
        }

        /**
         * 리뷰API
         */
        @JavascriptInterface
        fun requestReview() {
//            val manager = FakeReviewManager(this@BaseActivity)
            val manager = ReviewManagerFactory.create(this@BaseActivity)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener {
                if (it.isSuccessful) {
                    val reviewInfo = it.result
                    val flow = manager.launchReviewFlow(this@BaseActivity, reviewInfo)
                    flow.addOnCompleteListener {
                    }
                }
            }
        }

        @JavascriptInterface
        fun openerReload() {
            SP.setData(this@BaseActivity, SP.OPEN_RELOAD, "Y")
        }
    }
}