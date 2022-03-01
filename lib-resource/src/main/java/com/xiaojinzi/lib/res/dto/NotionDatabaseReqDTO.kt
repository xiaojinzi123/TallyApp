package com.xiaojinzi.lib.res.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

const val PAGE_SIZE = 20

@Keep
open class NotionDatabaseSortReqDTO(
    val property: String,
    val direction: String,
)

@Keep
class NotionDatabaseSortASCReqDTO(
    property: String,
) : NotionDatabaseSortReqDTO(property = property, direction = "ascending")

@Keep
class NotionDatabaseSortDESCReqDTO(
    property: String,
) : NotionDatabaseSortReqDTO(property = property, direction = "descending")

@Keep
abstract class NotionDatabaseFilterNumberReqDTO(
)

@Keep
data class NotionDatabaseReqDTO constructor(
    // 数据开始的指针
    @SerializedName("start_cursor")
    val startCursor: String?,
    @SerializedName("page_size")
    val pageSize: Int = PAGE_SIZE,
    val filter: NotionDatabaseFilterReq?,
    val sorts: List<NotionDatabaseSortReqDTO>,
)

enum class FilterCondition {
    And, Or
}

@Keep
open abstract class NotionDatabaseFilterReq

@Keep
data class NotionDatabaseFilterItemNumberEqualsReqDTO(
    val equals: Int,
)

@Keep
data class NotionDatabaseFilterItemCheckBoxEqualsReqDTO(
    val equals: Boolean,
)

@Keep
open class NotionDatabaseFilterItemReq(
    val property: String,
    val number: NotionDatabaseFilterItemNumberEqualsReqDTO? = null,
    val checkbox: NotionDatabaseFilterItemCheckBoxEqualsReqDTO? = null,
) : NotionDatabaseFilterReq()

@Keep
open class NotionDatabaseFilterGroupReq(
) : NotionDatabaseFilterReq()

@Keep
class NotionDatabaseFilterAndGroupReq(
    @SerializedName("and")
    val filterList: List<NotionDatabaseFilterReq>
) : NotionDatabaseFilterGroupReq()

@Keep
class NotionDatabaseFilterOrGroupReq(
    @SerializedName("or")
    val filterList: List<NotionDatabaseFilterReq>
) : NotionDatabaseFilterGroupReq()

/**
 * Notion 数据库的 Filter 构建器
 */
@Keep
class NotionDatabaseFilterReqBuilder(
    private val condition: FilterCondition
) {

    private val filters = mutableListOf<NotionDatabaseFilterReq>()

    fun addFilter(value: NotionDatabaseFilterReq): NotionDatabaseFilterReqBuilder {
        filters.add(value)
        return this
    }

    fun build(): NotionDatabaseFilterReq {
        return when (condition) {
            FilterCondition.And -> NotionDatabaseFilterAndGroupReq(
                filterList = filters
            )
            FilterCondition.Or -> NotionDatabaseFilterOrGroupReq(
                filterList = filters
            )
        }
    }

}

@Keep
class NotionDatabaseReqBuilder {
    var startCursor: String? = null
    var pageSize: Int = PAGE_SIZE
    private var filter: NotionDatabaseFilterReq? = null
    private val sorts = mutableListOf<NotionDatabaseSortReqDTO>()
    fun startCursor(value: String?): NotionDatabaseReqBuilder {
        this.startCursor = value
        return this
    }

    fun pageSize(value: Int): NotionDatabaseReqBuilder {
        this.pageSize = value
        return this
    }

    fun setFilter(value: NotionDatabaseFilterReq): NotionDatabaseReqBuilder {
        this.filter = value
        return this
    }

    fun addSort(value: NotionDatabaseSortReqDTO): NotionDatabaseReqBuilder {
        this.sorts.add(value)
        return this
    }

    fun build(): NotionDatabaseReqDTO {
        return NotionDatabaseReqDTO(
            startCursor = this.startCursor,
            pageSize = this.pageSize,
            filter = this.filter,
            sorts = this.sorts
        )
    }
}

fun newNotionDatabaseReqBuilder(): NotionDatabaseReqBuilder {
    return NotionDatabaseReqBuilder()
}

fun newNotionDatabaseFilterReqBuilder(condition: FilterCondition): NotionDatabaseFilterReqBuilder {
    return NotionDatabaseFilterReqBuilder(condition = condition)
}
