package com.xiaojinzi.tally.base.service.datasource

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.anno.NeedOptimize
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.tally.base.R
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

enum class TallyCategoryGroupTypeDTO(
    val dbValue: Int,
    @StringRes
    val stringRsd: Int,
    // 可用于排序
    val order: Int,
) {
    Other(
        dbValue = 3,
        stringRsd = R.string.res_str_other,
        order = 0,
    ),
    Income(
        dbValue = 1,
        stringRsd = R.string.res_str_income,
        order = 1,
    ),
    Spending(
        dbValue = 2,
        stringRsd = R.string.res_str_spending,
        order = 2,
    );

    companion object {
        fun fromDBValue(dbType: Int): TallyCategoryGroupTypeDTO {
            return when (dbType) {
                Income.dbValue -> Income
                Spending.dbValue -> Spending
                Other.dbValue -> Other
                else -> error("Not Support")
            }
        }
    }

}

@Keep
data class TallyCategoryGroupInsertDTO(
    val isBuiltIn: Boolean = false,
    val type: TallyCategoryGroupTypeDTO,
    val iconInnerIndex: Int,
    // 这两个字段其中一个必须有值
    val nameInnerIndex: Int? = null,
    val name: String? = null,
)

@Keep
data class TallyCategoryGroupDTO(
    val uid: String,
    val isBuiltIn: Boolean = false,
    val type: TallyCategoryGroupTypeDTO,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int?,
    val name: String?,
) {

    fun getStringItemVO(): StringItemDTO {
        return StringItemDTO(
            nameRsd = nameRsd,
            name = name,
        )
    }

}


@Keep
data class TallyCategoryInsertDTO(
    val groupId: String,
    val isBuiltIn: Boolean = false,
    @NeedOptimize
    val iconIndex: Int,
    @NeedOptimize
    val nameIndex: Int? = null,
    val name: String? = null,
)

@Keep
data class TallyCategoryWithGroupDTO(
    val category: TallyCategoryDTO,
    val group: TallyCategoryGroupDTO,
)

@Keep
@Parcelize
data class TallyCategoryDTO(
    val uid: String,
    // 所属的组
    val groupId: String,
    val isBuiltIn: Boolean = false,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int?,
    val name: String?,
) : Parcelable {

    fun getStringItemVO(): StringItemDTO {
        return StringItemDTO(
            nameRsd = nameRsd,
            name = name,
        )
    }

}

/**
 * 类别的实现
 */
interface TallyCategoryService {

    /**
     * 订阅类别组表的个数变化
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val categoryGroupCountObservable: Flow<Long>

    /**
     * 订阅类别表的个数变化
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val categoryCountObservable: Flow<Long>

    /**
     * 订阅所有类别组
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val categoryGroupListObservable: SharedStateFlow<List<TallyCategoryGroupDTO>>

    /**
     * 订阅所有类别
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val categoryListObservable: SharedStateFlow<List<TallyCategoryDTO>>

    /**
     * 订阅所有类别组和类别的组合数据
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val groupWithCategoryListObservable: Flow<List<Pair<TallyCategoryGroupDTO, List<TallyCategoryDTO>>>>

    /**
     * 插入一个类别组
     */
    suspend fun insertTallyCategoryGroup(target: TallyCategoryGroupInsertDTO): TallyCategoryGroupDTO

    /**
     * 更新一个类别组
     */
    suspend fun updateTallyCategoryGroup(target: TallyCategoryGroupDTO)

    /**
     * 更新一个类别
     */
    suspend fun updateTallyCategory(target: TallyCategoryDTO)

    /**
     * 插入一个类别
     */
    suspend fun insertTallyCategory(target: TallyCategoryInsertDTO): TallyCategoryDTO

    /**
     * 获取类别根据 id
     */
    suspend fun getTallyCategoryById(id: String): TallyCategoryDTO?

    /**
     * 获取记账的所有类别组的数据
     */
    suspend fun getAllTallyCategoryGroups(): List<TallyCategoryGroupDTO>

    /**
     * 根据 id 获取对象
     */
    suspend fun getTallyCategoryGroupById(id: String): TallyCategoryGroupDTO?

    /**
     * 获取记账的类别数据
     */
    suspend fun getTallyCategoriesByGroupId(groupId: String): List<TallyCategoryDTO>

    /**
     * 查询所有的类别
     */
    suspend fun getAllTallyCategories(): List<TallyCategoryDTO>

    /**
     * 根据 ID 获取
     */
    suspend fun getTallyCategoryDetailById(id: String): TallyCategoryWithGroupDTO?

    /**
     * 查询所有的类别, 带出所属的组
     */
    suspend fun getAllTallyCategoryDetails(): List<TallyCategoryWithGroupDTO>

    /**
     * 根据类别组 id 删除类别
     */
    suspend fun deleteCateGroupById(uid: String)

    /**
     * 根据类别 id 删除类别
     */
    suspend fun deleteCateById(uid: String)

    /**
     * 根据收入和支出类型订阅
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    fun subscribeCategoriesByType(type: TallyCategoryGroupTypeDTO): Flow<List<TallyCategoryDTO>>

}