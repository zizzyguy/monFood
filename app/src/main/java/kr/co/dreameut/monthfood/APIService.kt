package kr.co.dreameut.monthfood
import retrofit2.Call
import retrofit2.http.*


/**
 * Class: APIService
 * Created by tae kwon on 2019-05-10
 *
 * Description:
 */


interface APIService {

    @GET("app/splash")
    fun splash(): Call<String>

    @GET("app/endlist")
    fun endlist() : Call<EndData>

}