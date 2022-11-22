package com.thfw.ui.common;

import com.thfw.base.models.PickerData
import java.util.*

public interface ItemClickListener {
    fun onItemClick(selects: List<PickerData>, position: Int);
}