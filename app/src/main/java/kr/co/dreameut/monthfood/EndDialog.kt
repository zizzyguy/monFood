package kr.co.dreameut.monthfood

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.co.dreameut.monthfood.databinding.DialogEndPopupBinding
import kr.co.dreameut.monthfood.databinding.ItemEndItemBinding

class EndDialog(private val baseActivity: BaseActivity, val mList: ArrayList<EndListInfo>?) : DialogFragment(){

    lateinit var binding : DialogEndPopupBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        try {
            binding = DialogEndPopupBinding.inflate(inflater,container, false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            SP.setData(baseActivity, SP.DATE_LONG, Util.MAX)
            binding.rvEnd.adapter = Adapter()
            binding.rvEnd.setHasFixedSize(true)
            val mGridLayoutManager = GridLayoutManager(baseActivity, 2)
            binding.rvEnd.layoutManager = mGridLayoutManager

            binding.tvOk.setOnClickListener{
                dismiss()
                baseActivity.finish()
            }
            binding.tvCancel.setOnClickListener{dismiss()}
        } catch (e: Exception) {
            Toast.makeText(baseActivity, e.toString(), Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
              val binding = ItemEndItemBinding.inflate(LayoutInflater.from(baseActivity),parent,false)
              return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(mList!![position])
        }

        override fun getItemCount(): Int {
            return mList!!.size
        }


        inner class ViewHolder(private val binding: ItemEndItemBinding) : RecyclerView.ViewHolder(binding.root){

            fun bind(item : EndListInfo){
                try {
                    binding.tvName.text = item.pName
                    binding.tvDiscount.text = item.pDiscountRate
                    binding.tvSalePrice.text = item.pSalePrice
                    binding.tvOriginalPrice.text = item.pOriginalPrice
                    binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    Glide.with(baseActivity).load(item.pGImg).into(binding.ivImg)
                    binding.itemView.setOnClickListener {
                        val intent = Intent(baseActivity, NewActivity::class.java)
                        intent.putExtra("tarUrl", item.url)
                        startActivity(intent)
                        dismiss()}
                } catch (e: Exception) {
                }
            }
        }
    }


}