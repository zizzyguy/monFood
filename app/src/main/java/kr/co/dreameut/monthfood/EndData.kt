package kr.co.dreameut.monthfood

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EndData {
    @SerializedName("data")
    @Expose
    var data: EndList? = null
}