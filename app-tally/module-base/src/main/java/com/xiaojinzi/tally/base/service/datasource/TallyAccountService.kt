package com.xiaojinzi.tally.base.service.datasource

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize

@Keep
data class TallyAccountTypeInsertDTO(
    val order: Int = 0,
    @StringRes
    val nameRsd: Int? = null,
    // 名称
    val name: String? = null,
)

@Keep
data class TallyAccountTypeDTO(
    val uid: String,
    // 用于排序
    val order: Int,
    @StringRes
    val nameRsd: Int? = null,
    // 名称
    val name: String? = null,
)

@Keep
data class TallyAccountInsertDTO(
    // 账户类型 ID
    val typeId: String,
    // 是否是默认的
    val isDefault: Boolean = false,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    // 名称
    val name: String? = null,
    // 初始余额
    @ValueLong
    val initialBalance: Long
)

@Keep
@Parcelize
data class TallyAccountDTO(
    val uid: String,
    // 账户类型 ID
    val typeId: String,
    // 是否是默认的
    val isDefault: Boolean,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    // 名称
    val name: String? = null,
    // 初始余额
    val initialBalance: Long,
    // 当前余额
    val balance: Long,
) : Parcelable {

    fun getStringItemVO(): StringItemDTO {
        return StringItemDTO(
            nameRsd = nameRsd,
            name = name,
        )
    }

    fun nameAdapter(): String {
        return nameRsd?.run {
            app.getString(this)
        } ?: name!!
    }

}

@Keep
data class TallyAccountWithTypeDTO(
    val account: TallyAccountDTO,
    val accountType: TallyAccountTypeDTO,
)

interface TallyAccountTypeService {

    /**
     * 订阅所有的账户类型对象
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val allAccountTypeObservableDTO: Flow<List<TallyAccountTypeDTO>>

    /**
     * 插入对象
     */
    suspend fun insert(target: TallyAccountTypeInsertDTO): String

    /**
     * 插入对象
     */
    suspend fun insertAndReturn(target: TallyAccountTypeInsertDTO): TallyAccountTypeDTO {
        return insert(target = target).run {
            getByUid(uid = this)!!
        }
    }

    /**
     * 插入多个对象
     */
    suspend fun insertList(targetList: List<TallyAccountTypeInsertDTO>): List<String>

    /**
     * 插入多个对象
     */
    suspend fun insertListAndReturn(targetList: List<TallyAccountTypeInsertDTO>): List<TallyAccountTypeDTO> {
        return withContext(context = Dispatchers.IO) {
            insertList(targetList = targetList)
                .map { getByUid(uid = it)!! }
        }
    }

    /**
     * 获取全部
     */
    suspend fun getAll(): List<TallyAccountTypeDTO>

    /**
     * 根据 id 获取
     */
    suspend fun getByUid(uid: String): TallyAccountTypeDTO?

    /**
     * 更新一个对象
     */
    suspend fun update(target: TallyAccountTypeDTO)

}

/**
 * 记账的账户相关的实现
 */
interface TallyAccountService {

    /**
     * 默认的账户
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val defaultAccountObservable: Flow<TallyAccountDTO>

    /**
     * 所有账户的数据 Observable
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val allAccount: Flow<List<TallyAccountDTO>>

    /**
     * 所有账户的数据 Observable
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val allAccountWithType: Flow<List<TallyAccountWithTypeDTO>>

    /**
     * 获取所有的账户信息
     */
    suspend fun getAll(): List<TallyAccountDTO>

    /**
     * 默认的账户
     */
    suspend fun getDefaultAccount(): TallyAccountDTO

    /**
     * 根据 ID 获取
     */
    suspend fun getByUid(uid: String): TallyAccountDTO?

    /**
     * 根据 uid 删除
     */
    suspend fun deleteByUid(uid: String)

    /**
     * 更新
     */
    suspend fun update(target: TallyAccountDTO)

    /**
     * 插入一个账户对象
     */
    suspend fun insert(target: TallyAccountInsertDTO): TallyAccountDTO

    /**
     * 插入多个对象
     */
    suspend fun insertList(targetList: List<TallyAccountInsertDTO>): List<TallyAccountDTO>

    /**
     * 计算某个账户的消费. 可能为正, 可能为负
     */
    suspend fun calculateCost(uid: String): Long

    /**
     * 设置为默认的账户
     */
    suspend fun setDefault(targetAccountId: String)

    /**
     * 异步计算账户余额
     */
    fun calculateBalanceAsync()


}