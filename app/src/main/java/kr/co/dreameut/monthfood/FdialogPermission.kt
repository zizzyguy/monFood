package kr.co.dreameut.monthfood

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.UserManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kr.co.dreameut.monthfood.databinding.DialogEndPopupBinding
import kr.co.dreameut.monthfood.databinding.DialogEndPopupV2Binding
import kr.co.dreameut.monthfood.databinding.DialogPermissionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FdialogPermission(private val mActvitiy: SplashActivity) : DialogFragment(){

    lateinit var binding : DialogPermissionBinding
    val TAG_DIALOG = "DIALOG_PERMISSON"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DialogPermissionBinding.inflate(inflater,container, false)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false

        binding.btnPermissionOk.setOnClickListener {
            SP.setData(mActvitiy, SP.PERMISSION_OK, "Y")
            mActvitiy.goMainActivity()
            dismiss()
        }
        return binding.root
    }


    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager.beginTransaction()
            ft.add(this, tag).addToBackStack(null)
            ft.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {
        }
    }
}