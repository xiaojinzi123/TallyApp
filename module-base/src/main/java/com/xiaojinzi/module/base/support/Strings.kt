package com.xiaojinzi.module.base.support

/**
 * 获取后缀, xxx/xxx.jpg 获取 jpg
 */
fun String.getFileSuffix(): String? {
    val tempIndex = this.lastIndexOf('.')
    return if (tempIndex > -1 && tempIndex < this.lastIndex) {
        this.substring(tempIndex + 1)
    } else {
        null
    }
}