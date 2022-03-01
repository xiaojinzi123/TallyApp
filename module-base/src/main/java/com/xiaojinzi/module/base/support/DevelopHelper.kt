package com.xiaojinzi.module.base.support

object DevelopHelper {

    private var _isDevelop: Boolean = false

    val isDevelop: Boolean
        get() = _isDevelop

    fun init(isDevelop: Boolean) {
        this._isDevelop = isDevelop
    }

}