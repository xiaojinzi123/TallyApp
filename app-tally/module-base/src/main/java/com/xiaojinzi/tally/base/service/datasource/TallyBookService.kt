package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.annotation.HotObservable
import kotlinx.coroutines.flow.Flow

@Keep
data class TallyBookInsertDTO(
    val isDefault: Boolean = false,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
)

@Keep
data class TallyBookDTO(
    val uid: String,
    val createTime: Long,
    val isDefault: Boolean,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null
) {
    val nameItem: StringItemDTO = StringItemDTO(
        nameRsd = nameRsd,
        name = name,
    )
}

/**
 * 账本的功能
 */
interface TallyBookService {

    /**
     * 所有的账本
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val allBillBookListObservable: Flow<List<TallyBookDTO>>

    /**
     * 默认的账本
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val defaultBookObservable: Flow<TallyBookDTO>

    /**
     * 获取所有的数据
     */
    suspend fun getAll(): List<TallyBookDTO>

    /**
     * 默认的账本
     */
    suspend fun getDefaultBook(): TallyBookDTO

    /**
     * 根据 uid 获取目标
     */
    suspend fun getBookById(uid: String): TallyBookDTO?

    /**
     * 插入一个数据
     */
    suspend fun insert(target: TallyBookInsertDTO): TallyBookDTO {
        return insertList(
            targetList = listOf(target)
        ).first()
    }

    /**
     * 插入多个数据
     */
    suspend fun insertList(targetList: List<TallyBookInsertDTO>): List<TallyBookDTO>

    /**
     * 更新
     */
    suspend fun update(target: TallyBookDTO)

    /**
     * 根据 Id 删除
     */
    suspend fun deleteByUid(uid: String)

}