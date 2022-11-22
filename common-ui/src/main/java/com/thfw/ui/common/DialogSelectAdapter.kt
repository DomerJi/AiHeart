package com.thfw.ui.common

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thfw.base.models.PickerData
import com.thfw.ui.R


/**
 * Author:pengs
 * Date: 2022/11/22 11:41
 * Describe:Todo
 */
class DialogSelectAdapter constructor(selects: List<PickerData>) :
    RecyclerView.Adapter<DialogSelectAdapter.SelectHolder>() {

    lateinit var mContext: Context
    var selects: List<PickerData>

    init {
        this.selects = selects
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context;
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectHolder {
        var itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_select_text_imp, parent, false)
        return SelectHolder(itemView);
    }

    override fun onBindViewHolder(holder: SelectHolder, position: Int) {
        holder.mTvTitle.setText(selects[position].pickerViewText)
    }

    override fun getItemCount(): Int {
        if (selects == null) {
            return 0
        }
        return selects.size
    }

    lateinit var onClickListener: ItemClickListener



    inner class SelectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTvTitle: TextView;

        init {
            mTvTitle = itemView.findViewById<TextView>(R.id.tv_title);

            itemView.setOnClickListener(View.OnClickListener {
                if (onClickListener != null) {
                    onClickListener.onItemClick(selects, bindingAdapterPosition)
                }
            })

        }
    }

}


