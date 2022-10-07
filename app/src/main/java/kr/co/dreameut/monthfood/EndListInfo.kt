package kr.co.dreameut.monthfood

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EndListInfo {
    @SerializedName("p_num")
    @Expose
    var pNum: String? = null

    @SerializedName("p_name")
    @Expose
    var pName: String? = null

    @SerializedName("p_original_price")
    @Expose
    var pOriginalPrice: String? = null

    @SerializedName("p_sale_price")
    @Expose
    var pSalePrice: String? = null

    @SerializedName("p_discount_rate")
    @Expose
    var pDiscountRate: String? = null

    @SerializedName("p_g_img")
    @Expose
    var pGImg: String? = null

    @SerializedName("arrival_source")
    @Expose
    var arrivalSource: String? = null

    @SerializedName("etc_data")
    @Expose
    var etcData: String? = null

    @SerializedName("url")
    @Expose
    val url : String? = null
}