package com.xiaojinzi.tally.bill.module.search.view

import androidx.annotation.Keep
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBookDTO
import com.xiaojinzi.tally.base.service.datasource.TallyCategoryGroupDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelDTO

@Keep
data class BillSearchLabelVO(
    val core: TallyLabelDTO,
    val isSelected: Boolean,
)

@Keep
data class BillSearchBookVO(
    val core: TallyBookDTO,
    val isSelected: Boolean,
)

@Keep
data class BillSearchAccountVO(
    val core: TallyAccountDTO,
    val isSelected: Boolean,
)

@Keep
data class BillSearchCategoryGroupVO(
    val core: TallyCategoryGroupDTO,
    val isSelected: Boolean,
)