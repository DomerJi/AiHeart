package com.thfw.ui.common

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.thfw.base.models.PickerData
import com.thfw.base.utils.LogUtil
import com.thfw.base.utils.SharePreferenceUtil
import com.thfw.ui.R
import com.thfw.ui.base.KtBaseActivity
import com.thfw.ui.dialog.OptionDialog
import com.thfw.ui.widget.TitleView

class LhXkSettingActivity : KtBaseActivity() {

    companion object {
        @JvmField
        var ORIGIN_VALUE = "lhxk.picker.value"

        @JvmField
        val KEY_FACE_FOCUS = "lhxk.face.focus" // 焦点跟随 开关

        @JvmField
        val KEY_FACE_FOCUS_DEFALUT = 1

        @JvmField
        val KEY_FACE_FOCUS_M = "lhxk.face.focus.m" // 焦点跟随 距离（1米 1.5米 2米 2.5米 3米(默认) 4米 4.5米 5米 视野内的所有人）

        @JvmField
        val KEY_FACE_FOCUS_M_DEFALUT = 3f

        @JvmField
        val KEY_FACE_FOCUS_NOSEE_TIME = "lhxk.face.focus.time" // 别看我 后 保持时长（10秒 20秒 30秒  45秒 1分钟(默认) 2分钟 3分钟 5分钟 10分钟）

        @JvmField
        val KEY_FACE_FOCUS_NOSEE_TIME_DEFALUT = 60

        @JvmField
        val KEY_FACE_FOCUS_NOSEE_MODEL = "lhxk.face.focus.model" // 别看我 记录模式（单人(默认)/多人）

        @JvmField
        val KEY_FACE_FOCUS_NOSEE_MODEL_DEFALUT = 0

        @JvmField
        val KEY_VOICE = "lhxk.voice" // 语音识别 开关

        @JvmField
        val KEY_VOICE_DEFALUT = 1

        @JvmField
        val KEY_VOICE_TEXT = "lhxk.voice.text" // 文字上屏 开关

        @JvmField
        val KEY_VOICE_TEXT_DEFALUT = 1

        @JvmField
        val KEY_VOICE_TEXT_GRAVITY = "lhxk.voice.text.gravity" // 文字上屏位置（顶部 中间(默认) 底部）

        @JvmField
        val KEY_VOICE_TEXT_GRAVITY_DEFALUT = Gravity.CENTER

        @JvmStatic
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, LhXkSettingActivity::class.java))
        }

        /**
         * 恢复默认
         */
        @JvmStatic
        fun resetData() {
            SharePreferenceUtil.setInt(KEY_FACE_FOCUS, KEY_FACE_FOCUS_DEFALUT)
            SharePreferenceUtil.setFloat(KEY_FACE_FOCUS_M, KEY_FACE_FOCUS_M_DEFALUT)
            SharePreferenceUtil.setInt(KEY_FACE_FOCUS_NOSEE_TIME, KEY_FACE_FOCUS_NOSEE_TIME_DEFALUT)
            SharePreferenceUtil.setInt(KEY_FACE_FOCUS_NOSEE_MODEL, KEY_FACE_FOCUS_NOSEE_MODEL_DEFALUT)
            SharePreferenceUtil.setInt(KEY_VOICE, KEY_VOICE_DEFALUT)
            SharePreferenceUtil.setInt(KEY_VOICE_TEXT, KEY_VOICE_TEXT_DEFALUT)
            SharePreferenceUtil.setInt(KEY_VOICE_TEXT_GRAVITY, KEY_VOICE_TEXT_GRAVITY_DEFALUT)
        }
    }

    private var mLlFaceFocus: LinearLayout? = null
    private var mSwitchFaceFocus: Switch? = null
    private var mLlFocusChild: LinearLayout? = null
    private var mLlFocusM: LinearLayout? = null
    private var mTvFocusMValue: TextView? = null
    private var mLlFocusNoSeeModel: LinearLayout? = null
    private var mTvFocusNoSeeModel: TextView? = null
    private var mLlFocusNoSee: LinearLayout? = null
    private var mTvFocusNoSeeValue: TextView? = null
    private var mLlVoice: LinearLayout? = null
    private var mSwitchVoice: Switch? = null
    private var mLlVoiceChild: LinearLayout? = null
    private var mLlVoiceText: LinearLayout? = null
    private var mSwitchVoiceText: Switch? = null
    private var mLlVoiceTextGravity: LinearLayout? = null
    private var mTvVoiceTextGravity: TextView? = null

    override fun getContentView(): Int {
        return R.layout.activity_lhxk_setting
    }

    override fun initView() {
        super.initView()

        var mTitleView = findViewById<TitleView>(R.id.titleView) as TitleView
        mTitleView.setRightOnClickListener({ v ->
            LhXkSettingActivity.resetData()
            initData()
        })
        // ================ 焦点跟随开关 ============
        mLlFaceFocus = findViewById<LinearLayout>(R.id.ll_face_focus) as LinearLayout
        mLlFocusChild = findViewById<LinearLayout>(R.id.ll_focus_child) as LinearLayout
        mSwitchFaceFocus = findViewById<View>(R.id.switch_face_focus) as Switch
        mLlFocusM = findViewById<LinearLayout>(R.id.ll_focus_m) as LinearLayout
        mTvFocusMValue = findViewById<TextView>(R.id.tv_focus_m_value) as TextView
        mLlFocusNoSeeModel = findViewById<LinearLayout>(R.id.ll_focus_no_see_model) as LinearLayout
        mTvFocusNoSeeModel = findViewById<TextView>(R.id.tv_focus_no_see_model) as TextView
        mLlFocusNoSee = findViewById<LinearLayout>(R.id.ll_focus_no_see) as LinearLayout
        mTvFocusNoSeeValue = findViewById<TextView>(R.id.tv_focus_no_see_value) as TextView
        // ============== 语音识别 ===================
        mLlVoice = findViewById<LinearLayout>(R.id.ll_voice) as LinearLayout
        mSwitchVoice = findViewById<Switch>(R.id.switch_voice) as Switch
        mLlVoiceChild = findViewById<LinearLayout>(R.id.ll_voice_child) as LinearLayout
        mLlVoiceText = findViewById<LinearLayout>(R.id.ll_voice_text) as LinearLayout
        mSwitchVoiceText = findViewById<Switch>(R.id.switch_voice_text) as Switch
        mLlVoiceTextGravity = findViewById<LinearLayout>(R.id.ll_voice_text_gravity) as LinearLayout
        mTvVoiceTextGravity = findViewById<TextView>(R.id.tv_voice_text_gravity) as TextView

    }

    override fun initData() {
        super.initData()
        mSwitchFaceFocus!!.setChecked(SharePreferenceUtil.getInt(KEY_FACE_FOCUS, KEY_FACE_FOCUS_DEFALUT) == 1)

        mLlFocusChild!!.visibility = if (mSwitchFaceFocus!!.isChecked) View.VISIBLE else View.GONE
        mSwitchFaceFocus!!.setOnCheckedChangeListener({ buttonView, isChecked ->
            SharePreferenceUtil.setInt(
                KEY_FACE_FOCUS,
                if (isChecked) 1 else 0
            )
            LhXkSet.refreshLhXkSet();
            mLlFocusChild!!.visibility = if (isChecked) View.VISIBLE else View.GONE
        })
        var focusMList = listOf<PickerData>(
            PickerData("1米").put(ORIGIN_VALUE, 1f),
            PickerData("1.5米").put(ORIGIN_VALUE, 1.5f),
            PickerData("2米").put(ORIGIN_VALUE, 2f),
            PickerData("2.5米").put(ORIGIN_VALUE, 2.5f),
            PickerData("3米(默认)").put(ORIGIN_VALUE, 3f),
            PickerData("3.5米").put(ORIGIN_VALUE, 3.5f),
            PickerData("4米").put(ORIGIN_VALUE, 4f),
            PickerData("4.5米").put(ORIGIN_VALUE, 4.5f),
            PickerData("5米").put(ORIGIN_VALUE, 5f),
            PickerData("视野内的所有人").put(ORIGIN_VALUE, -1f)
        )

        var focusMListValue: Float = SharePreferenceUtil.getFloat(KEY_FACE_FOCUS_M, KEY_FACE_FOCUS_M_DEFALUT)
        var showPositionFocusM = 0;
        for ((index, item) in focusMList.withIndex()) {
            if (item.get<Float>(ORIGIN_VALUE) == focusMListValue) {
                mTvFocusMValue!!.text = item.pickerViewText
                showPositionFocusM = index;
                break
            }
        }

        // 焦点跟随 距离（1米 1.5米 2米 2.5米 3米(默认) 3.5米 4米 4.5米 5米 视野内的所有人）
        mLlFocusM!!.setOnClickListener({ view ->
            var mClRoot: RelativeLayout = findViewById<RelativeLayout>(R.id.cl_root) as RelativeLayout
            OptionDialog.create(
                mContext, mClRoot,
                OnOptionsSelectListener() { option1, option2, option3, mView ->
                    showPositionFocusM = option1;
                    mTvFocusMValue!!.text = focusMList.get(option1).pickerViewText
                    var value: Float = focusMList.get(option1).get(ORIGIN_VALUE)
                    LogUtil.i(TAG, "value - > " + value)
                    SharePreferenceUtil.setFloat(KEY_FACE_FOCUS_M, value);
                    LhXkSet.refreshLhXkSet();
                }, "视野距离", focusMList, showPositionFocusM
            )
        })
        var focusModel = listOf<PickerData>(
            PickerData("单人(默认)").put(ORIGIN_VALUE, 0),
            PickerData("多人").put(ORIGIN_VALUE, 1)
        )
        var focusModelValue: Int = SharePreferenceUtil.getInt(KEY_FACE_FOCUS_NOSEE_MODEL, KEY_FACE_FOCUS_NOSEE_MODEL_DEFALUT)
        var showPositionFocusModel = 0;
        for ((index, item) in focusModel.withIndex()) {
            if (item.get<Int>(ORIGIN_VALUE) == focusModelValue) {
                mTvFocusNoSeeModel!!.text = item.pickerViewText
                showPositionFocusModel = index
                break
            }
        }
        // 不看我 （单人（默认）多人）
        mLlFocusNoSeeModel!!.setOnClickListener({ view ->
            var mClRoot: RelativeLayout = findViewById<RelativeLayout>(R.id.cl_root) as RelativeLayout
            OptionDialog.create(
                mContext, mClRoot,
                OnOptionsSelectListener() { option1, option2, option3, mView ->
                    showPositionFocusModel = option1;
                    mTvFocusNoSeeModel!!.text = focusModel.get(option1).pickerViewText
                    var value: Int = focusModel.get(option1).get(ORIGIN_VALUE)
                    LogUtil.i(TAG, "value - > " + value)
                    SharePreferenceUtil.setInt(KEY_FACE_FOCUS_NOSEE_MODEL, value)
                    LhXkSet.refreshLhXkSet()
                }, "别看我了【记忆人数】", focusModel, showPositionFocusModel
            )
        })

        var focusTime = listOf<PickerData>(
            PickerData("10秒").put(ORIGIN_VALUE, 10),
            PickerData("20秒").put(ORIGIN_VALUE, 20),
            PickerData("30秒").put(ORIGIN_VALUE, 30),
            PickerData("45秒").put(ORIGIN_VALUE, 45),
            PickerData("1分钟(默认)").put(ORIGIN_VALUE, 60),
            PickerData("2分钟").put(ORIGIN_VALUE, 2 * 60),
            PickerData("3分钟").put(ORIGIN_VALUE, 3 * 60),
            PickerData("5分钟").put(ORIGIN_VALUE, 5 * 60),
            PickerData("10分钟").put(ORIGIN_VALUE, 10 * 60)
        )
        var focusTimeValue: Int = SharePreferenceUtil.getInt(KEY_FACE_FOCUS_NOSEE_TIME, KEY_FACE_FOCUS_NOSEE_TIME_DEFALUT)
        var showPositionfocusTime = 0;
        for ((index, item) in focusTime.withIndex()) {
            if (item.get<Int>(ORIGIN_VALUE) == focusTimeValue) {
                mTvFocusNoSeeValue!!.text = item.pickerViewText
                showPositionfocusTime = index;
                break
            }
        }
        // 不看我 生效时长（10秒 20秒 30秒  45秒 1分钟(默认) 2分钟 3分钟 5分钟 10分钟）
        mLlFocusNoSee!!.setOnClickListener({ view ->
            var mClRoot: RelativeLayout = findViewById<RelativeLayout>(R.id.cl_root) as RelativeLayout
            OptionDialog.create(
                mContext, mClRoot,
                OnOptionsSelectListener() { option1, option2, option3, mView ->
                    showPositionfocusTime = option1
                    mTvFocusNoSeeValue!!.text = focusTime.get(option1).pickerViewText
                    var value: Int = focusTime.get(option1).get(ORIGIN_VALUE)
                    LogUtil.i(TAG, "value - > " + value)
                    SharePreferenceUtil.setInt(KEY_FACE_FOCUS_NOSEE_TIME, value)
                    LhXkSet.refreshLhXkSet()
                }, "别看我了【持续时长】", focusTime, showPositionfocusTime
            )
        })

        mSwitchVoice!!.setChecked(SharePreferenceUtil.getInt(KEY_VOICE, KEY_VOICE_DEFALUT) == 1);
        // 语音识别开关
        mLlVoiceChild!!.visibility = if (mSwitchVoice!!.isChecked) View.VISIBLE else View.GONE
        mSwitchVoice!!.setOnCheckedChangeListener({ buttonView, isChecked ->
            SharePreferenceUtil.setInt(
                KEY_VOICE,
                if (isChecked) 1 else 0
            )
            LhXkSet.refreshLhXkSet()
            mLlVoiceChild!!.visibility = if (isChecked) View.VISIBLE else View.GONE
        })

        mSwitchVoiceText!!.setChecked(SharePreferenceUtil.getInt(KEY_VOICE_TEXT, KEY_VOICE_TEXT_DEFALUT) == 1);
        mSwitchVoiceText!!.setOnCheckedChangeListener({ buttonView, isChecked ->
            SharePreferenceUtil.setInt(
                KEY_VOICE_TEXT,
                if (isChecked) 1 else 0
            )
            LhXkSet.refreshLhXkSet()
        })

        var voiceTextGravity = listOf<PickerData>(
            PickerData("顶部").put(ORIGIN_VALUE, Gravity.TOP),
            PickerData("中部(默认)").put(ORIGIN_VALUE, Gravity.CENTER),
            PickerData("底部").put(ORIGIN_VALUE, Gravity.BOTTOM),
        )
        var voiceGravity: Int = SharePreferenceUtil.getInt(KEY_VOICE_TEXT_GRAVITY, KEY_VOICE_TEXT_GRAVITY_DEFALUT)
        var showPositionGravity = 0;
        for ((index, item) in voiceTextGravity.withIndex()) {
            if (item.get<Int>(ORIGIN_VALUE) == voiceGravity) {
                mTvVoiceTextGravity!!.text = item.pickerViewText
                showPositionGravity = index
                break
            }
        }
        // 文字上屏 位置（顶部 中部（默认） 底部）
        mLlVoiceTextGravity!!.setOnClickListener({ view ->
            var mClRoot: RelativeLayout = findViewById<RelativeLayout>(R.id.cl_root) as RelativeLayout
            OptionDialog.create(
                mContext, mClRoot,
                OnOptionsSelectListener() { option1, option2, option3, mView ->
                    showPositionGravity = option1
                    mTvVoiceTextGravity!!.text = voiceTextGravity.get(option1).pickerViewText
                    var value: Int = voiceTextGravity.get(option1).get(ORIGIN_VALUE)
                    LogUtil.i(TAG, "value - > " + value)
                    SharePreferenceUtil.setInt(KEY_VOICE_TEXT_GRAVITY, value)
                    LhXkSet.refreshLhXkSet()
                }, "文字上屏【位置】", voiceTextGravity, showPositionGravity
            )
        })
    }
}