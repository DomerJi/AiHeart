package com.thfw.ui.common

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dragclosehelper.library.DragCloseHelper
import com.github.chrisbanes.photoview.PhotoView
import com.thfw.base.models.ImageModel
import com.thfw.ui.R


/**
 * Author:pengs
 * Date: 2022/11/22 11:41
 * Describe:Todo
 */
class VpImageAdapter constructor(images: ArrayList<ImageModel>, dragCloseHelper: DragCloseHelper) :
    RecyclerView.Adapter<VpImageAdapter.VpImageHolder>() {
    lateinit var mContext: Context
    lateinit var images: ArrayList<ImageModel>
    var dragCloseHelper: DragCloseHelper

    init {
        this.images = images
        this.dragCloseHelper = dragCloseHelper
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mContext = recyclerView.context;
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VpImageHolder {
        var itemView: View =
            LayoutInflater.from(mContext).inflate(R.layout.layout_image_imp, parent, false)
        return VpImageHolder(itemView);
    }

    override fun onBindViewHolder(holder: VpImageHolder, position: Int) {
        Glide.with(mContext).load(images[position].picUrl)
            .transition(DrawableTransitionOptions.withCrossFade()).into(holder.mPvImage);
    }

    override fun getItemCount(): Int {
        if (images == null) {
            return 0
        }
        return images.size
    }

    lateinit var longListener: View.OnLongClickListener


    inner class VpImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPvImage: PhotoView;

        init {
            mPvImage = itemView.findViewById<PhotoView>(R.id.pv_image);
            itemView.tag = this
            dragCloseHelper.setShareElementMode(false);
            mPvImage.setOnClickListener(View.OnClickListener { (mPvImage.context as Activity).finish() })
            mPvImage.setOnLongClickListener(View.OnLongClickListener {
                if (longListener != null) {
                    longListener.onLongClick(it)
                    return@OnLongClickListener true
                }
                return@OnLongClickListener false
            })
        }
    }

}


