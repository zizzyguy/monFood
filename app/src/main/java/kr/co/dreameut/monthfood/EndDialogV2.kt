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
import android.widget.*
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.google.android.material.tabs.TabLayoutMediator
import kr.co.dreameut.monthfood.databinding.DialogEndPopupBinding
import kr.co.dreameut.monthfood.databinding.DialogEndPopupV2Binding
import kr.co.dreameut.monthfood.databinding.ItemEndItemBinding
import kr.co.dreameut.monthfood.databinding.ItemEndItemV2Binding

class EndDialogV2(private val baseActivity: BaseActivity, val mList: ArrayList<EndListInfo>?) : DialogFragment(){

    lateinit var binding : DialogEndPopupV2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        try {
            binding = DialogEndPopupV2Binding.inflate(inflater,container, false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.vpEnd.adapter = Adapter()
            binding.vpEnd.setCurrentItem(mList!!.size*5, false)
//            binding.dotsIndicator.attachTo(binding.vpEnd)
            for(i : Int  in 0 until mList!!.size){
                val imageView = ImageView(baseActivity)
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                layoutParams.setMargins(14,0,14,0)
                imageView.layoutParams = layoutParams
                if(i==0){
                    imageView.setImageResource(R.drawable.active)
                }else{
                    imageView.setImageResource(R.drawable.deact)
                }
                binding.indicator.addView(imageView)
            }
            binding.vpEnd.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.indicator.removeAllViews()
                    for(i : Int in 0 until mList!!.size){
                        val imageView = ImageView(baseActivity)
                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                        layoutParams.setMargins(14,0,14,0)
                        imageView.layoutParams = layoutParams
                        if(position%mList.size==i){
                            imageView.setImageResource(R.drawable.active)
                        }else{
                            imageView.setImageResource(R.drawable.deact)
                        }
                        binding.indicator.addView(imageView)
                    }
                }
            })
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
              val binding = ItemEndItemV2Binding.inflate(LayoutInflater.from(baseActivity),parent,false)
              return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(mList!![position%mList.size])
        }

        override fun getItemCount(): Int {
            return Int.MAX_VALUE
        }

        inner class ViewHolder(private val binding: ItemEndItemV2Binding) : RecyclerView.ViewHolder(binding.root){

            fun bind(item : EndListInfo){
                try {
                    binding.tvName.text = item.pName
//                    binding.tvDiscount.text = item.pDiscountRate
                    binding.tvSalePrice.text = item.pSalePrice
                    binding.tvOriginalPrice.text = item.pOriginalPrice
                    binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    val round = Util.getPixel(baseActivity, 10f)
                    Glide.with(baseActivity).load(item.pGImg).transform(CenterCrop(), GranularRoundedCorners(round,round,0f,0f)).into(binding.ivImg)
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