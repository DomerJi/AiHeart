package com.thfw.ui.common

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.dragclosehelper.library.DragCloseHelper
import com.github.chrisbanes.photoview.OnMatrixChangedListener
import com.github.chrisbanes.photoview.PhotoView
import com.thfw.base.ContextApp
import com.thfw.base.models.ImageModel
import com.thfw.base.models.PickerData
import com.thfw.base.utils.*
import com.thfw.ui.R
import com.thfw.ui.base.KtBaseActivity
import com.thfw.ui.dialog.LoadingDialog
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
    lateinit var mIvColse: ImageView
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
        mIvColse = findViewById<ImageView>(R.id.iv_close) as ImageView
        mIvColse.setOnClickListener(View.OnClickListener { finish() })
        mTvPage = findViewById<TextView>(R.id.tv_page) as TextView;
        mClImage = findViewById<ConstraintLayout>(R.id.cl_image) as ConstraintLayout;
        dragCloseHelper.setDragCloseViews(mClImage, mVp)

    }

    override fun initData() {
        super.initData()

        images = intent.getSerializableExtra(KEY_DATA) as ArrayList<ImageModel>

        var visiblePosition = intent.getIntExtra(KEY_VISIBLE_POSITION, 0);
        LogUtil.d(TAG, "initData images.size " + images.size);
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
                return@OnLongClickListener true
            } else {
                return@OnLongClickListener false
            }

        };
    }

    /**
     * 显示底部框框 保存图片和识别二维码
     */
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
                var items = mutableListOf<PickerData>(PickerData("保存图片", 0))
                var dialogLikeAdapter: DialogSelectAdapter = DialogSelectAdapter(items)
                mRvSelect.setAdapter(dialogLikeAdapter);

                var beginTime: Long = System.currentTimeMillis();
                Glide.with(mContext).asBitmap().load(images[mVp.currentItem].picUrl).into(
                    MyQRCodeTarget(images[mVp.currentItem].picUrl, object : QRCodeHasListener {
                        override fun has(qrCode: String, picUrl: String) {

                            var limit: Long = System.currentTimeMillis() - beginTime;
                            HandlerUtil.getMainHandler().postDelayed(Runnable {

                                if (!isMeResumed || !tDialog.isVisible) {
                                    return@Runnable
                                }

                                if (!StringUtil.contentEquals(images[mVp.currentItem].picUrl, picUrl)) {
                                    return@Runnable
                                }

                                items.add(PickerData("识别图中二维码", 1).put("key.qrcode", qrCode))
                                dialogLikeAdapter.notifyItemInserted(items.size - 1)
                            }, if (limit >= 500) 0 else (500 - limit));

                        }
                    })
                )
                dialogLikeAdapter.onClickListener = object : ItemClickListener {
                    override fun onItemClick(selects: List<PickerData>, position: Int) {
                        if (position == 0) {
                            saveImage29()
                        } else if (position == 1) {
                            var qrCode: String = selects[position].get("key.qrcode")
                            if (ContextApp.getWebViewListener() != null) {
                                ContextApp.getWebViewListener().jump(mContext, qrCode)
                            }
                        }
                        if (tDialog != null) {
                            tDialog.dismiss()
                        }
                    }
                };

            })
            .setOnViewClickListener(OnViewClickListener { hoder, view, dialog ->
                dialog.dismiss()
            }).create().show();


    }


    inner class DragListener : DragCloseHelper.DragCloseListener {


        override fun intercept(): Boolean {

            var mRv: RecyclerView? = mVp.getChildAt(0) as RecyclerView;
            if (mRv != null) {
                var itemView: View? = mRv.layoutManager?.findViewByPosition(mVp.currentItem);
                if (itemView != null && itemView.tag is VpImageAdapter.VpImageHolder) {
                    var scale: Float = (itemView.tag as VpImageAdapter.VpImageHolder).mPvImage.scale
                    LogUtil.d(TAG, "scale -> " + scale);
                    if (scale < 0.9f || scale > 1.1f) {
                        return true
                    }
                }
            }

            return scrollIng;
        }

        override fun dragStart() {
            dragIng = true
        }

        override fun dragging(percent: Float) {

            dragIng = true
            var newPercent: Float = (1f - (1f - percent) * 1.6).toFloat();
            if (newPercent < 0f) {
                newPercent = 0f
            }
            LogUtil.d(TAG, "percent -> $percent  newPercent -> $newPercent")
            mIvColse.alpha = newPercent
            mTvPage.alpha = newPercent
        }

        override fun dragCancel() {
            dragIng = false
            mIvColse.alpha = 1f;
            mTvPage.alpha = 1f;
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
            mVp.post(Runnable {

                var mRv: RecyclerView? = mVp.getChildAt(0) as RecyclerView;
                if (mRv != null) {
                    var itemView: View? = mRv.layoutManager?.findViewByPosition(mVp.currentItem);
                    if (itemView != null && itemView.tag is VpImageAdapter.VpImageHolder) {
                        setMatrix((itemView.tag as VpImageAdapter.VpImageHolder).mPvImage);
                    }
                }

            })
        }

    }

    fun setMatrix(mPvImage: PhotoView) {
        /**
         * viewpager左右滑动 + photoview缩放之间本身就存在着冲突，需要额外处理。
         * 此处使用自定义的vp，同时以com.github.chrisbanes.photoview.PhotoView为例，如果非此photoview，请自己实现对应的matrix方法
         * 思路：需要保证，1.图片缩小的情况下，vp没法左右滑动，2.图片正常的情况下，可以左右滑动，3.图片放大的情况下，在图片拖拽到边界的时候，可以左右滑动
         * 代码为了可读性，没有将ifelse进行合并处理
         */
        mPvImage.setOnMatrixChangeListener(OnMatrixChangedListener { rect: RectF ->
            //因为demo中此页面是全屏处理，所以需要获取整个屏幕的宽度（包括横屏的时候虚拟按键的宽度），如果非全屏，自己更改此处的宽度
            val screenW: Int = Util.getScreenWidth(mContext)

            /**
             * 因为发现加载完成之后，matrix会和正常情况有误差，所以需要获取误差值，此处取系统误差值getScaledTouchSlop
             * 但是getScaledTouchSlop默认是8*系统密度，所以可以根据自己需求去设定
             *
             * 同时scale也会存在一定的误差，正常情况下可能在0.99-1.01之间
             *
             * getParent().requestDisallowInterceptTouchEvent(true):父控件不会拦截事件
             * getParent().requestDisallowInterceptTouchEvent(false):父控件会根据自身的判断来决定是否拦截
             */
            Log.i(TAG, "rect -> " + rect.toString())
            Log.i(TAG, "width -> " + rect.width())
            val delta = ViewConfiguration.get(mContext).scaledTouchSlop / 2
            if (rect.width() < screenW) {
                //预览的时候，图片左右边界小于屏幕宽度 --> 此处存在三种情况，正常+缩小+放大（图片宽度还处于小于屏幕宽度的时候）
                if (mPvImage.getScale() < 0.9f || mPvImage.getScale() > 1.1f) {
                    //getscale会存在误差，如果是缩小或者放大状态，拦截vp的事件
                    mVp.setUserInputEnabled(false)
                } else {
                    //属于正常状态，可以左右滑动
                    mVp.setUserInputEnabled(true)
                }
            } else {
                //预览的时候，大于或者等于屏幕宽度 --> 对应的情况，正常 + 放大
                if (Math.abs(rect.left - 0) <= delta || Math.abs(rect.right - screenW) <= delta) {
                    //图片的左右边界，在误差范围内 --> 对应情况，正常 + 放大（图片拖拽到边界）
                    mVp.setUserInputEnabled(true)
                } else {
                    //放大，没拖拽到边界
                    mVp.setUserInputEnabled(false)
                }
            }
        })
    }

    override fun finish() {
        super.finish()
        Log.i(TAG, "dragClose -> $dragClose")
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
        LoadingDialog.show(this, "正在保存")
        Glide.with(mContext).asBitmap().load(images[mVp.currentItem].picUrl).into(MyTarget())
    }

    inner class MyTarget : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            LoadingDialog.hide()
            if (!isMeResumed) {
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

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            LoadingDialog.hide()
            ToastUtil.show("保存失败！")
        }

    }

    interface QRCodeHasListener {
        fun has(qrCode: String, picUrl: String)
    }

    inner class MyQRCodeTarget constructor(picUrl: String, listener: QRCodeHasListener) :
        SimpleTarget<Bitmap>() {
        var picUrl: String
        var listener: QRCodeHasListener

        init {
            this.picUrl = picUrl
            this.listener = listener
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            if (!isMeResumed) {
                return
            }
            Thread(Runnable {
                if (StringUtil.contentEquals(images[mVp.currentItem].picUrl, picUrl)) {
                    var qrCode = QRCodeDecoder.syncDecodeQRCode(resource)
                    if (!TextUtils.isEmpty(qrCode)) {
                        listener.has(qrCode, picUrl)
                    }
                }
            }).start()
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
        }

    }
}