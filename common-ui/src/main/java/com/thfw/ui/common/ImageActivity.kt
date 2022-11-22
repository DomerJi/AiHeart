package com.thfw.ui.common

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dragclosehelper.library.DragCloseHelper
import com.thfw.base.models.ImageModel
import com.thfw.base.models.PickerData
import com.thfw.base.utils.EmptyUtil
import com.thfw.base.utils.LogUtil
import com.thfw.base.utils.ToastUtil
import com.thfw.ui.R
import com.thfw.ui.base.KtBaseActivity
import com.thfw.ui.dialog.TDialog
import com.thfw.ui.dialog.listener.OnBindViewListener
import com.thfw.ui.dialog.listener.OnViewClickListener
import java.io.Serializable

class ImageActivity : KtBaseActivity() {

    /**
     * 静态方法使用
     */
    companion object {

        @JvmField
        val KEY_VISIBLE_POSITION: String = "key.position"

        @JvmStatic
        fun startActivity(context: Context, image: ImageModel) {
            startActivity(context, listOf(image) as ArrayList<ImageModel>, 0)
        }

        @JvmStatic
        fun startActivity(context: Context, image: String) {
            startActivity(context, listOf(image), 0)
        }

        @JvmStatic
        fun startActivity(context: Context, images: List<String>, visiblePosition: Int) {
            var imageModes: ArrayList<ImageModel> = ArrayList()
            for (item in images) {
                imageModes.add(ImageModel(item))
            }
            startActivity(context, imageModes, visiblePosition)
        }

        @JvmStatic
        fun startActivity(context: Context, images: ArrayList<ImageModel>, visiblePosition: Int) {
            var intent: Intent = Intent(context, ImageActivity::class.java)
            intent.putExtra(KEY_DATA, images as Serializable)
            if (visiblePosition < 0 || visiblePosition >= images.size) {
                intent.putExtra(KEY_DATA, 0)
            } else {
                intent.putExtra(KEY_VISIBLE_POSITION, visiblePosition)
            }
            context.startActivity(intent)
            (context as Activity).overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }

    }

    lateinit var mVp: ViewPager2
    lateinit var mClImage: ConstraintLayout
    lateinit var dragCloseHelper: DragCloseHelper
    lateinit var tDialog: TDialog
    lateinit var mTvPage: TextView
    var scrollIng: Boolean = false
    var dragIng: Boolean = false
    var dragClose: Boolean = false
    lateinit var images: ArrayList<ImageModel>

    /**
     * @JvmField - 修饰静态变量
     * @JvmStatic - 修饰静态方法
     * @JvmField 和 @JvmStatic 只能写在 object 修饰的类或者 companion object 里，写法虽然有些别扭，但是效果是真的是按 static 来实现的
     */

    override fun getContentView(): Int {
        return R.layout.activity_image
    }

    override fun getStatusBarColor(): Int {
        return STATUSBAR_TRANSPARENT
    }

    override fun initView() {

        super.initView()
        dragCloseHelper = DragCloseHelper(mContext);
        dragCloseHelper.setMinScale(0.1f)
        mVp = findViewById<ViewPager2>(R.id.vp2_images) as ViewPager2
        findViewById<ImageView>(R.id.iv_close).setOnClickListener(View.OnClickListener { finish() })
        mTvPage = findViewById<TextView>(R.id.tv_page) as TextView;
        mClImage = findViewById<ConstraintLayout>(R.id.cl_image) as ConstraintLayout;
        dragCloseHelper.setDragCloseViews(mClImage, mVp)

    }

    override fun initData() {
        super.initData()

        images = intent.getSerializableExtra(KEY_DATA) as ArrayList<ImageModel>

        var visiblePosition = intent.getIntExtra(KEY_VISIBLE_POSITION, 0);
        LogUtil.d("initData+++++++++++++++++++++++++" + images.size);
        if (EmptyUtil.isEmpty(images)) {
            ToastUtil.show("参数不合法")
            finish()
            return
        }
        mTvPage.setText((visiblePosition + 1).toString() + "/" + images.size)
        var mVpImageAdapter = VpImageAdapter(images, dragCloseHelper)
        mVp.adapter = mVpImageAdapter;
        mVp.setCurrentItem(visiblePosition, false)
        mVp.registerOnPageChangeCallback(PageCallback())
        dragCloseHelper.setDragCloseListener(DragListener())
        mVpImageAdapter.longListener = View.OnLongClickListener {
            if (!dragIng) {
                showBottomDialog();
            }
            return@OnLongClickListener true
        };
    }

    fun showBottomDialog() {
        tDialog = TDialog.Builder(getSupportFragmentManager())
            .setLayoutRes(R.layout.dialog_select_cancel_layout)
            .setGravity(Gravity.BOTTOM)
            .setDialogAnimationRes(R.style.animate_dialog)
            .addOnClickListener(R.id.tv_cancel)
            .setScreenWidthAspect(this, 1f)
            // R.id.tv_title, R.id.tv_hint, R.id.tv_left, R.id.tv_right
            .setOnBindViewListener(OnBindViewListener {
                var mRvSelect: RecyclerView = it.getView(R.id.rv_select);
                mRvSelect.setLayoutManager(LinearLayoutManager(mRvSelect.getContext()))
                var items: List<PickerData> = listOf<PickerData>(PickerData("保存图片", 0))
                var dialogLikeAdapter: DialogSelectAdapter = DialogSelectAdapter(items);

                dialogLikeAdapter.onClickListener = object : ItemClickListener {
                    override fun onItemClick(selects: List<PickerData>, position: Int) {
                        saveImage29()
                        if (tDialog != null) {
                            tDialog.dismiss()
                        }
                    }
                };
                mRvSelect.setAdapter(dialogLikeAdapter);

            })
            .setOnViewClickListener(OnViewClickListener { hoder, view, dialog ->
                dialog.dismiss()
            }).create().show();


    }


    inner class DragListener : DragCloseHelper.DragCloseListener {


        override fun intercept(): Boolean {
            return scrollIng;
        }

        override fun dragStart() {
            dragIng = true
        }

        override fun dragging(percent: Float) {
            dragIng = true
        }

        override fun dragCancel() {
            dragIng = false
        }

        override fun dragClose(isShareElementMode: Boolean) {
            dragClose = true
            dragIng = false
        }

    }

    inner class PageCallback : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            scrollIng = state != 0
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            mTvPage.setText((position + 1).toString() + "/" + images.size)
        }

    }

    override fun finish() {
        super.finish()
        if (dragClose) {
            overridePendingTransition(R.anim.enter_fade_150, R.anim.exit_fade_150)
        } else {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        try {
            if (dragCloseHelper.handleEvent(event)) {
                return true;
            } else {
                return super.dispatchTouchEvent(event)
            }
        } catch (e: IllegalArgumentException) {
            return true
        }
    }

    /**
     * API29 中的最新保存图片到相册的方法
     */
    private fun saveImage29() {
        Glide.with(mContext).asBitmap().load(images[mVp.currentItem].picUrl).into(MyTarget())
    }

    inner class MyTarget : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            if(!isMeResumed){
                return
            }
            val insertUri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues()
            ) ?: kotlin.run {
                ToastUtil.show("保存失败！")
                return
            }
            // 使用use可以自动关闭流
            contentResolver.openOutputStream(insertUri).use {
                if (resource.compress(Bitmap.CompressFormat.JPEG, 90, it)) {
                    ToastUtil.show("保存成功！")
                } else {
                    ToastUtil.show("保存失败！")
                }
            }
        }

    }
}