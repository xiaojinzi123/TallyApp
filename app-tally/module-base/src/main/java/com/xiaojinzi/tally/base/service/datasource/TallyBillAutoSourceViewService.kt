package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.Keep
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.lib.res.dto.AutoBillSourceViewType

@Keep
data class TallyBillAutoSourceViewInsertDTO(
    val sourceApp: TallyBillAutoSourceAppDTO,
    val sourceViewType: AutoBillSourceViewType,
    val name: StringItemDTO,
    val accountId: String? = null,
    val categoryId: String? = null,
)

@Keep
data class TallyBillAutoSourceViewDTO(
    val uid: String,
    val sourceAppId: String,
    val sourceViewType: AutoBillSourceViewType,
    val name: StringItemDTO,
    val accountId: String?,
    val categoryId: String?,
)

@Keep
data class TallyBillAutoSourceViewDetailDTO(
    val core: TallyBillAutoSourceViewDTO,
    val sourceApp: TallyBillAutoSourceAppDTO,
    val category: TallyCategoryDTO?,
)

/**
 * 自动记账的支持的那些界面的 Service
 */
interface TallyBillAutoSourceViewService {

    /**
     * 插入一个对象
     */
    suspend fun insert(target: TallyBillAutoSourceViewInsertDTO): TallyBillAutoSourceViewDTO

    /**
     * 插入一组数据
     */
    suspend fun insertList(targetList: List<TallyBillAutoSourceViewInsertDTO>): List<TallyBillAutoSourceViewDTO>

    /**
     * 更新一个对象
     */
    suspend fun update(target: TallyBillAutoSourceViewDTO)

    /**
     * 根据 ID 获取
     */
    suspend fun getById(id: String): TallyBillAutoSourceViewDTO?

    /**
     * 根据类型获取
     */
    suspend fun getDetailByType(type: AutoBillSourceViewType): TallyBillAutoSourceViewDetailDTO?

    /**
     * 根据 Id 获取
     */
    suspend fun getDetailById(id: String): TallyBillAutoSourceViewDetailDTO?

    /**
     * 获取所有
     */
    suspend fun getAllDetail(): List<TallyBillAutoSourceViewDetailDTO>

}