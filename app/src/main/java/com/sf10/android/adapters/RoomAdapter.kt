package com.sf10.android.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sf10.android.R
import com.sf10.android.databinding.ItemPublicPlayerBinding
import com.sf10.android.models.PublicPlayer
import com.sf10.android.models.Visibility
import de.hdodenhof.circleimageview.CircleImageView


open class RoomAdapter(
    private val context: Context,
    private var list: ArrayList<PublicPlayer>,
    private val isCreator: Boolean,
    private val myUid: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPublicPlayerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyViewHolder(binding)
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            holder.bind(model)
            if(isCreator && model.uid != myUid){
                holder.itemView.findViewById<Button>(R.id.btn_kick).visibility = View.VISIBLE
                holder.itemView.findViewById<Button>(R.id.btn_kick).setOnClickListener {
                    onClickListener?.onClickKick(position, model)
                }
            }
        }
    }

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(private val binding: ItemPublicPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: PublicPlayer) {
            binding.tvName.text = model.username
            Glide.with(binding.root).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivPlaceImage)
        }
    }

    interface OnClickListener {
        fun onClickKick(position: Int, model: PublicPlayer)
    }
}