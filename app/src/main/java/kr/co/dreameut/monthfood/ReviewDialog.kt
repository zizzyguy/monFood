package kr.co.dreameut.monthfood

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kr.co.dreameut.monthfood.databinding.DialogReviewBinding

class ReviewDialog : DialogFragment() {


    lateinit var binding : DialogReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogReviewBinding.inflate(inflater, container,false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.ivReview.setOnClickListener { goGooglePlay() }
        binding.tvOk.setOnClickListener { goGooglePlay() }
        return binding.root
    }

    private fun goGooglePlay(){
        try {
            activity?.let { SP.setData(it,SP.REVIEW, "Y") }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=kr.co.dreameut.monthfood"))
            startActivity(intent)
            dismiss()
        } catch (e: Exception) {
            Toast.makeText(activity, "구글 플레이를 열수가 없습니다.",Toast.LENGTH_SHORT).show()
        }
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