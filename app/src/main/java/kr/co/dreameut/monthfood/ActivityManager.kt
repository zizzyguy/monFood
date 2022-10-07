package kr.co.dreameut.monthfood

import android.app.Activity
import android.util.Log

class ActivityManager {

    companion object{
        var actList = ArrayList<Activity>()

        fun allActivityFinishExceptMain() {
            for (act in actList) {
                if ((act as BaseActivity).className != "MainActivity")
                    act.finish()
            }
        }
    }
}