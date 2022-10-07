package kr.co.dreameut.monthfood

import android.content.Context
import org.json.JSONArray
import org.json.JSONException


class SP {

    companion object{
        private const val KEY = "setting"
        const val FCM_ID = "fcmid"
        const val M_KEY = "m_key"
        const val M_NUM = "m_num"
        const val HAS_LAYER = "has_layer"
        const val OPEN_RELOAD = "open_reload"
        const val CLOSE_RELOAD = "close_reload"
        const val BACK_JS = "back_js"
        const val BACK_STOP = "back_stop"

        fun setData(context: Context, key: String?, value: String?) {
            val prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getData(context: Context, key: String?, default_val: String?): String? {
            val prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
            return prefs.getString(key, default_val)
        }

        fun setStringArrayPref(context: Context, key: String, values: ArrayList<String>) {
            val prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
            val editor = prefs.edit()
            val a = JSONArray()
            for (i in 0 until values.size) {
                a.put(values[i])
            }
            if (!values.isEmpty()) {
                editor.putString(key, a.toString())
            } else {
                editor.putString(key, null)
            }
            editor.apply()
        }


        fun getStringArray(context: Context, key: String): ArrayList<String>? {
            val prefs = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
            val json = prefs.getString(key, null)
            val urls = ArrayList<String>()
            if (json != null) {
                try {
                    val a = JSONArray(json)
                    for (i in 0 until a.length()) {
                        val url = a.optString(i)
                        urls.add(url)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            return urls
        }


    }
}