package com.xiaojinzi.module.base.support

object Assert {

    fun <T> assertEquals(value1: T, value2: T, message: String = "") {
        if (value1 != value2) {
            val messageContent = if(message.isNullOrEmpty()) "" else "message is '$message'."
            notSupportError("value1 '$value1' is not equals to value2 '$value2'. $messageContent")
        }
    }

    fun <T> assertNotEquals(value1: T, value2: T, message: String = "") {
        if (value1 == value2) {
            val messageContent = if(message.isNullOrEmpty()) "" else "message is '$message'."
            notSupportError("value1 '$value1' is equals to value2 '$value2'. $messageContent")
        }
    }

    fun assertTrue(b: Boolean, message: String = ""): Boolean {
        val messageContent = if(message.isEmpty()) "" else "message is '$message'."
        if (!b) {
            notSupportError(messageContent)
        }
        return b
    }

    fun <T> assertNull(value: T?): T? {
        if (value != null) {
            notSupportError()
        }
        return null
    }

    fun <T> assertNotNull(value: T?): T {
        if (value == null) {
            notSupportError()
        }
        return value
    }

    fun assertNotEmptyString(value: String?): String {
        if (value.isNullOrEmpty()) {
            notSupportError()
        }
        return value
    }

}