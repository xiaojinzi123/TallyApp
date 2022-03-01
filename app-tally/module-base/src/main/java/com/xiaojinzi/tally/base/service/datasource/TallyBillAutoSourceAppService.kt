package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.lib.res.dto.AutoBillSourceAppType

@Keep
data class TallyBillAutoSourceAppInsertDTO(
    val sourceAppType: AutoBillSourceAppType,
    val name: StringItemDTO,
)

@Keep
data class TallyBillAutoSourceAppDTO(
    val uid: String,
    val sourceAppType: AutoBillSourceAppType,
    val name: StringItemDTO,
    val accountId: String?,
)

@Keep
data class TallyBillAutoSourceAppDetailDTO(
    val core: TallyBillAutoSourceAppDTO,
    val account: TallyAccountDTO?,
)

/**
 * 自动记账的支持的那些界面的 Service
 */
interface TallyBillAutoSourceAppService {

    /**
     * 插入一个对象
     */
    suspend fun insert(target: TallyBillAutoSourceAppInsertDTO): TallyBillAutoSourceAppDTO

    /**
     * 插入一个对象
     */
    suspend fun insertAndReturnDetail(target: TallyBillAutoSourceAppInsertDTO): TallyBillAutoSourceAppDetailDTO

    /**
     * 插入一组数据
     */
    suspend fun insertList(targetList: List<TallyBillAutoSourceAppInsertDTO>): List<TallyBillAutoSourceAppDTO>

    /**
     * 更新一个对象
     */
    suspend fun update(target: TallyBillAutoSourceAppDTO)

    /**
     * 根据 ID 获取
     */
    suspend fun getById(id: String): TallyBillAutoSourceAppDTO?

    /**
     * 根据 [AutoBillSourceAppType] 获取
     */
    suspend fun getDetailByType(sourceType: AutoBillSourceAppType): TallyBillAutoSourceAppDetailDTO?

    /**
     * 获取所有
     */
    suspend fun getAll(): List<TallyBillAutoSourceAppDTO>

    /**
     * 获取所有
     */
    suspend fun getAllDetail(): List<TallyBillAutoSourceAppDetailDTO>

}