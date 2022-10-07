package kr.co.dreameut.alltrot.network

import android.util.Log
import kr.co.dreameut.monthfood.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ApiModule {
    private var apiService: APIService? = null
    fun getApiService(isNew: Boolean): APIService? {
        apiService = provideRetrofit(isNew)
        return apiService
    }

    private fun provideRetrofit(isNew: Boolean): APIService? {
        val client = OkHttpClient.Builder()
            .connectTimeout(1000, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("os", "android")
                    .addHeader("key", Const.SECRET_KEY)
                    .addHeader("m-key", SP.getData(APP.INSTANCE, SP.M_KEY, ""))
                    .addHeader("m-num", SP.getData(APP.INSTANCE, SP.M_NUM, ""))
                    .addHeader("version-name", Util.getVersionName(APP.INSTANCE))
                    .addHeader("version-code", Util.getVersionCode(APP.INSTANCE).toString())
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        if(isNew){
            apiService = Retrofit.Builder()
                .baseUrl(Protocol.SERVER_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(APIService::class.java)
        }else{
            apiService = Retrofit.Builder()
                .baseUrl(Protocol.SERVER_BASE)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build()
                .create(APIService::class.java)
        }
        return apiService
    }
}