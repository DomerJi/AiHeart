package com.thfw.base.models

import com.thfw.base.base.IModel

/**
 * Author:pengs
 * Date: 2022/11/22 10:38
 * Describe:Todo
 */
class ImageModel constructor(picUrl: String) : IModel {

    var name: String = ""
    var picUrl: String = ""
        get() = field;
    var des: String = ""

    init {
        this.picUrl = picUrl
    }

}