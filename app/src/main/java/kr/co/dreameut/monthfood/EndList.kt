package kr.co.dreameut.monthfood

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EndList {

    @SerializedName("end_list")
    @Expose
    var endList: ArrayList<EndListInfo>? = null
}