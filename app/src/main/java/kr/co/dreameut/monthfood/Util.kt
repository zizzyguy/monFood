package kr.co.dreameut.monthfood

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kr.co.dreameut.monthfood.inter.FcmGetSuccessListener
import java.text.SimpleDateFormat
import java.util.*


class Util {

    companion object{

        const val MAX = 9000000000000000000L

        fun check30(context: Context): Boolean {
            return SP.getData(context, SP.DATE_LONG, MAX)!! + 1000L * 60 * 50  < Date().time
        }

        fun getToken(listener: FcmGetSuccessListener){

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result
//                val msg = getString(R.string.msg_token_fmt, token)
                listener.onSuccess(token)
            })
        }

        @SuppressLint("HardwareIds")
        fun getUserUniqueKey(context: Context) : String {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id?.let {
                if(it == "00000000-0000-0000-0000-000000000000" || it.isEmpty()){
                    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                }else{
                    return it
                }
            }?:run{
                return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }
        }

        fun getVersionName(context: Context) : String {
            val pi = context.packageManager.getPackageInfo(context.packageName, 0)
            return pi.versionName
        }

        fun getVersionCode(context: Context) : Int{
            var versionCode :Int
            val pi = context.packageManager.getPackageInfo(context.packageName, 0)
            versionCode = pi.versionCode
            return versionCode
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeStamp(): String? {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
            return dateFormat.format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun getDate() : String?{
            val dateFormat = SimpleDateFormat("yyMMdd")
            return dateFormat.format(Date())
        }


        fun getDeviceName(): String? {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

        fun getPixel(context: Context, dp : Float) : Float{
            val dm = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm)
        }



        private fun capitalize(s: String?): String {
            if (s == null || s.length == 0) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first).toString() + s.substring(1)
            }
        }
    }
}