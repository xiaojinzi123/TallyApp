package com.xiaojinzi.module.base.service

import android.content.Context
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.support.ktx.app

interface SPService {

    companion object {
        const val DEFAULT_FILE_NAME = "SP_DEFAULT"
    }

    fun contains(fileName: String = DEFAULT_FILE_NAME, key: String): Boolean

    fun getString(
        fileName: String = DEFAULT_FILE_NAME,
        key: String,
        defValue: String? = null
    ): String?

    fun putString(fileName: String = DEFAULT_FILE_NAME, key: String, value: String?)

    fun getBool(
        fileName: String = DEFAULT_FILE_NAME,
        key: String,
        defValue: Boolean? = null
    ): Boolean?

    fun putBool(fileName: String = DEFAULT_FILE_NAME, key: String, value: Boolean?)

    fun getInt(fileName: String = DEFAULT_FILE_NAME, key: String, defValue: Int? = null): Int?

    fun putInt(fileName: String = DEFAULT_FILE_NAME, key: String, value: Int?)

    fun getLong(fileName: String = DEFAULT_FILE_NAME, key: String, defValue: Long? = null): Long?

    fun putLong(fileName: String = DEFAULT_FILE_NAME, key: String, value: Long?)

}

@ServiceAnno(SPService::class)
class SPServiceImpl : SPService {

    override fun contains(fileName: String, key: String): Boolean {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        return sp.contains(key)
    }

    override fun getString(
        fileName: String,
        key: String,
        defValue: String?
    ): String? {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        return sp.getString(key, defValue)
    }

    override fun putString(
        fileName: String,
        key: String,
        value: String?,
    ) {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        val edit = sp.edit()
        edit.putString(key, value)
        edit.commit()
    }

    override fun getBool(
        fileName: String,
        key: String,
        defValue: Boolean?,
    ): Boolean? {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        return if (sp.contains(key)) {
            sp.getBoolean(key, false)
        } else {
            defValue
        }
    }

    override fun putBool(
        fileName: String,
        key: String,
        value: Boolean?,
    ) {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        val edit = sp.edit()
        if (value == null) {
            edit.remove(key)
        } else {
            edit.putBoolean(key, value)
        }
        edit.commit()
    }

    override fun getInt(
        fileName: String,
        key: String,
        defValue: Int?
    ): Int? {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        return if (sp.contains(key)) {
            sp.getInt(key, 0)
        } else {
            defValue
        }
    }

    override fun putInt(
        fileName: String,
        key: String,
        value: Int?,
    ) {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        val edit = sp.edit()
        if (value == null) {
            edit.remove(key)
        } else {
            edit.putInt(key, value)
        }
        edit.commit()
    }

    override fun getLong(
        fileName: String,
        key: String,
        defValue: Long?
    ): Long? {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        return if (sp.contains(key)) {
            sp.getLong(key, 0L)
        } else {
            defValue
        }
    }

    override fun putLong(
        fileName: String,
        key: String,
        value: Long?
    ) {
        val sp = app.getSharedPreferences(
            fileName,
            Context.MODE_PRIVATE
        )
        val edit = sp.edit();
        if (value == null) {
            edit.remove(key)
        } else {
            edit.putLong(key, value)
        }
        edit.commit()
    }

}