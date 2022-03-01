package com.xiaojinzi.tally.base.service.datasource

import androidx.annotation.*
import androidx.compose.ui.graphics.Color
import com.xiaojinzi.module.base.bean.StringItemDTO
import kotlinx.coroutines.flow.Flow

@Keep
data class TallyLabelInsertDTO(
    val colorInnerIndex: Int,
    val name: StringItemDTO,
)

@Keep
data class TallyLabelDTO(
    val uid: String,
    val createTime: Long,
    @ColorInt
    val colorInt: Int,
    val name: StringItemDTO,
)

interface TallyLabelService {

    companion object {

        /**
         * 颜色列表
         */
        val labelInnerColorList = listOf(
            Color(color = 0xFFFFCDD2),
            Color(color = 0xFFF8BBD0),
            Color(color = 0xFFE1BEE7),
            Color(color = 0xFFD1C4E9),
            Color(color = 0xFFC5CAE9),
            Color(color = 0xFFBBDEFB),
            Color(color = 0xFFB3E5FC),
            Color(color = 0xFFB2EBF2),
            Color(color = 0xFFB2DFDB),
            Color(color = 0xFFC8E6C9),
            Color(color = 0xFFDCEDC8),
            Color(color = 0xFFF0F4C3),
            Color(color = 0xFFFFF9C4),
            Color(color = 0xFFFFECB3),
            Color(color = 0xFFFFE0B2),
            Color(color = 0xFFFFCCBC),
        )
    }

    /**
     * 获取所有标签
     */
    suspend fun getAll(): List<TallyLabelDTO>

    /**
     * 根据 Id 获取
     */
    suspend fun getById(id: String): TallyLabelDTO?

    suspend fun getByIds(ids: List<String>): List<TallyLabelDTO>

    suspend fun insert(target: TallyLabelInsertDTO): TallyLabelDTO

    suspend fun insertList(targetList: List<TallyLabelInsertDTO>): List<TallyLabelDTO>

    /**
     * 根据 Id 删除
     */
    suspend fun deleteByIds(ids: List<String>)

    /**
     * 更新
     */
    suspend fun update(target: TallyLabelDTO)

    /**
     * 订阅所有
     */
    fun subscribeAll(): Flow<List<TallyLabelDTO>>

}