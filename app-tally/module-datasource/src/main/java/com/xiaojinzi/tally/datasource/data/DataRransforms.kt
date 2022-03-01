package com.xiaojinzi.tally.datasource.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.toArgb
import com.xiaojinzi.lib.res.dto.AutoBillSourceAppType
import com.xiaojinzi.lib.res.dto.AutoBillSourceViewType
import com.xiaojinzi.module.base.support.ResData
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.tallyImageService
import com.xiaojinzi.tally.datasource.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

/**
 * 两者一定有一个不是为空的
 */
interface IDatabaseName {

    /**
     * 名称的下标, 是 [ResData.resStringIndexList] 的下标
     */
    val nameInnerIndex: Int?

    /**
     * 名称
     */
    val name: String?

    fun toStringItem(): StringItemDTO {
        return StringItemDTO(
            nameRsd = nameInnerIndex?.let { ResData.resStringIndexList[it] },
            name = name,
        )
    }

}

private fun @receiver:StringRes Int.toNameInnerIndex(): Int {
    return ResData.getNameRsdIndex(rsdId = this)
}

private fun @receiver:DrawableRes Int.toIconInnerIndex(): Int {
    return ResData.getIconRsdIndex(rsdId = this)
}

// ===============================================================================

fun TallyBudgetInsertDTO.toTallyBudgetDO(): TallyBudgetDO {
    return TallyBudgetDO(
        createTime = System.currentTimeMillis(),
        value = this.value,
        month = TallyBudgetService.MONTH_TIME_FORMAT.format(Date(this.monthTime)),
    )
}

fun TallyBudgetDO.toTallyBudgetDTO(): TallyBudgetDTO {
    return TallyBudgetDTO(
        uid = this.uid,
        createTime = this.createTime,
        monthTime = TallyBudgetService.MONTH_TIME_FORMAT.parse(this.month).time,
        value = this.value,
    )
}

fun TallyBudgetDTO.toTallyBudgetDO(): TallyBudgetDO {
    return TallyBudgetDO(
        uid = this.uid,
        createTime = this.createTime,
        value = this.value,
        month = TallyBudgetService.MONTH_TIME_FORMAT.format(Date(this.monthTime))
    )
}

fun TallyImageInsertDTO.toTallyImageDO(): TallyImageDO {
    return TallyImageDO(
        url = this.url,
        key1 = this.key1,
    )
}

fun TallyImageDO.toTallyImageDTO(): TallyImageDTO {
    return TallyImageDTO(
        uid = this.uid,
        url = this.url,
        key1 = this.key1,
    )
}

fun TallyBillAutoSourceAppInsertDTO.toTallyBillAutoSourceAppDO(): TallyBillAutoSourceAppDO {
    return TallyBillAutoSourceAppDO(
        sourceAppType = this.sourceAppType.dbValue,
        nameInnerIndex = this.name.nameRsd?.toNameInnerIndex(),
        name = this.name.name,
    )
}

fun TallyBillAutoSourceAppDO.toTallyBillAutoSourceAppDTO(): TallyBillAutoSourceAppDTO {
    return TallyBillAutoSourceAppDTO(
        uid = this.uid,
        sourceAppType = AutoBillSourceAppType.fromValue(this.sourceAppType),
        name = StringItemDTO(
            nameRsd = if (nameInnerIndex == null) null else ResData.resStringIndexList[nameInnerIndex],
            name = this.name,
        ),
        accountId = this.accountId
    )
}

fun TallyBillAutoSourceAppDetailDO.toTallyBillAutoSourceAppDetailDTO(): TallyBillAutoSourceAppDetailDTO {
    return TallyBillAutoSourceAppDetailDTO(
        core = this.core.toTallyBillAutoSourceAppDTO(),
        account = this.account?.toTallyAccountDTO(),
    )
}

fun TallyBillAutoSourceAppDTO.toTallyBillAutoSourceAppDO(): TallyBillAutoSourceAppDO {
    return TallyBillAutoSourceAppDO(
        uid = this.uid,
        sourceAppType = this.sourceAppType.dbValue,
        nameInnerIndex = this.name.nameRsd?.toNameInnerIndex(),
        name = this.name.name,
        accountId = this.accountId,
    )
}

fun TallyBillAutoSourceViewInsertDTO.toTallyBillAutoSourceViewDO(): TallyBillAutoSourceViewDO {
    return TallyBillAutoSourceViewDO(
        sourceAppId = this.sourceApp.uid,
        sourceViewType = this.sourceViewType.dbValue,
        nameInnerIndex = this.name.nameRsd?.toNameInnerIndex(),
        name = this.name.name,
        accountId = this.accountId,
        categoryId = this.categoryId
    )
}

fun TallyBillAutoSourceViewDO.toTallyBillAutoSourceViewDTO(): TallyBillAutoSourceViewDTO {
    return TallyBillAutoSourceViewDTO(
        uid = this.uid,
        sourceAppId = this.sourceAppId,
        sourceViewType = AutoBillSourceViewType.fromValue(this.sourceViewType),
        name = StringItemDTO(
            nameRsd = if (nameInnerIndex == null) null else ResData.resStringIndexList[nameInnerIndex],
            name = this.name,
        ),
        accountId = this.accountId,
        categoryId = this.categoryId
    )
}

fun TallyBillAutoSourceViewDTO.toTallyBillAutoSourceViewDO(): TallyBillAutoSourceViewDO {
    return TallyBillAutoSourceViewDO(
        uid = this.uid,
        sourceAppId = this.sourceAppId,
        sourceViewType = this.sourceViewType.dbValue,
        nameInnerIndex = this.name.nameRsd?.toNameInnerIndex(),
        name = this.name.name,
        accountId = this.accountId,
        categoryId = this.categoryId
    )
}

fun TallyBillAutoSourceViewDetailDO.toTallyBillAutoSourceViewDTO(): TallyBillAutoSourceViewDetailDTO {
    return TallyBillAutoSourceViewDetailDTO(
        core = this.core.toTallyBillAutoSourceViewDTO(),
        sourceApp = this.sourceApp.toTallyBillAutoSourceAppDTO(),
        category = this.cate?.toTallyCategoryDTO(),
    )
}

fun TallyBookInsertDTO.toTallyBookDO(): TallyBookDO {
    return TallyBookDO(
        isDefault = this.isDefault,
        createTime = System.currentTimeMillis(),
        nameInnerIndex = this.nameRsd?.run { ResData.getNameRsdIndex(rsdId = this) },
        name = this.name,
    )
}

fun TallyBookDO.toTallyBookDTO(): TallyBookDTO {
    return TallyBookDTO(
        uid = this.uid,
        createTime = this.createTime,
        isDefault = this.isDefault,
        nameRsd = if (nameInnerIndex == null) null else ResData.resStringIndexList[nameInnerIndex],
        name = this.name,
    )
}

fun TallyBookDTO.toTallyBookDO(): TallyBookDO {
    return TallyBookDO(
        uid = this.uid,
        createTime = this.createTime,
        isDefault = this.isDefault,
        nameInnerIndex = this.nameRsd?.toNameInnerIndex(),
        name = this.name,
    )
}

fun TallyCategoryGroupTypeDTO.toDBIntType(): Int {
    return this.dbValue
}

fun TallyCategoryGroupDTO.toTallyCategoryGroupDO(): TallyCategoryGroupDO {
    return TallyCategoryGroupDO(
        uid = this.uid,
        isBuiltIn = this.isBuiltIn,
        type = this.type.dbValue,
        iconInnerIndex = ResData.getIconRsdIndex(rsdId = this.iconRsd),
        nameInnerIndex = this.nameRsd?.run { ResData.getNameRsdIndex(rsdId = this) },
        name = name,
    )
}

fun TallyCategoryGroupDO.toTallyCategoryGroupDTO(): TallyCategoryGroupDTO {
    return TallyCategoryGroupDTO(
        uid = uid!!,
        isBuiltIn = isBuiltIn,
        type = TallyCategoryGroupTypeDTO.fromDBValue(dbType = this.type),
        iconRsd = ResData.resIconIndexList[iconInnerIndex],
        nameRsd = if (nameInnerIndex == null) null else ResData.resStringIndexList[nameInnerIndex],
        name = name,
    )
}

fun TallyCategoryGroupInsertDTO.toTallCategoryDO(): TallyCategoryGroupDO {
    return TallyCategoryGroupDO(
        // 默认都是 false
        isBuiltIn = false,
        type = type.toDBIntType(),
        iconInnerIndex = this.iconInnerIndex,
        nameInnerIndex = this.nameInnerIndex,
        name = this.name,
    )
}

fun TallyCategoryDO.toTallyCategoryDTO(): TallyCategoryDTO {
    return TallyCategoryDTO(
        uid = uid,
        groupId = groupId,
        isBuiltIn = isBuiltIn,
        iconRsd = ResData.resIconIndexList[this.iconInnerIndex],
        nameRsd = if (nameInnerIndex == null) null else ResData.resStringIndexList[nameInnerIndex],
        name = name,
    )
}

fun TallyCategoryDTO.toTallyCategoryDO(): TallyCategoryDO {
    return TallyCategoryDO(
        uid = this.uid,
        groupId = this.groupId,
        isBuiltIn = this.isBuiltIn,
        iconInnerIndex = this.iconRsd.toIconInnerIndex(),
        nameInnerIndex = this.nameRsd?.toNameInnerIndex(),
        name = name,
    )
}

fun TallyCategoryInsertDTO.toTallCategoryDO(): TallyCategoryDO {
    return TallyCategoryDO(
        groupId = this.groupId,
        // 默认都是 false
        isBuiltIn = false,
        iconInnerIndex = this.iconIndex,
        nameInnerIndex = this.nameIndex,
        name = this.name,
    )
}

fun TallyBillInsertDTO.toTallBillDO(): TallyBillDO {
    return TallyBillDO(
        isDeleted = false,
        usage = this.usage.dbValue,
        type = this.type.dbValue,
        time = this.time,
        accountId = this.accountId,
        transferTargetAccountId = this.transferTargetAccountId,
        bookId = this.bookId,
        categoryId = this.categoryId,
        cost = this.cost,
        costAdjust = this.cost,
        note = this.note,
        reimburseType = this.reimburseType.dbValue,
        reimburseBillId = this.reimburseBillId,
        isNotIncludedInIncomeAndExpenditure = this.isNotIncludedInIncomeAndExpenditure,
    )
}

fun TallyBillDO.toTallyBillDTO(): TallyBillDTO {
    return TallyBillDTO(
        uid = this.uid,
        isDeleted = this.isDeleted,
        usage = TallyBillUsageDTO.fromValue(dbValue = this.usage),
        type = TallyBillTypeDTO.fromValue(dbValue = this.type),
        time = this.time,
        accountId = this.accountId,
        transferTargetAccountId = this.transferTargetAccountId,
        bookId = this.bookId,
        categoryId = this.categoryId,
        cost = this.cost,
        costAdjust = this.costAdjust,
        note = this.note,
        reimburseType = ReimburseType.fromValue(dbValue = this.reimburseType),
        reimburseBillId = this.reimburseBillId,
        isNotIncludedInIncomeAndExpenditure = this.isNotIncludedInIncomeAndExpenditure,
    )
}

fun TallyBillDTO.toTallyBillDO(): TallyBillDO {
    return TallyBillDO(
        uid = this.uid,
        usage = this.usage.dbValue,
        type = this.type.dbValue,
        time = this.time,
        accountId = this.accountId,
        transferTargetAccountId = this.transferTargetAccountId,
        bookId = this.bookId,
        categoryId = this.categoryId,
        cost = this.cost,
        costAdjust = this.costAdjust,
        note = this.note,
        reimburseType = this.reimburseType.dbValue,
        reimburseBillId = this.reimburseBillId,
        isNotIncludedInIncomeAndExpenditure = this.isNotIncludedInIncomeAndExpenditure,
    )
}

fun TallyLabelInsertDTO.toTallyLabelDO(): TallyLabelDO {
    return TallyLabelDO(
        nameInnerIndex = this.name.nameRsd?.toNameInnerIndex(),
        name = this.name.name,
        colorInnerIndex = this.colorInnerIndex,
    )
}

fun TallyLabelDO.toTallyLabelDTO(): TallyLabelDTO {
    return TallyLabelDTO(
        uid = this.uid,
        createTime = this.createTime,
        colorInt = TallyLabelService.labelInnerColorList[this.colorInnerIndex].toArgb(),
        name = this.toStringItem(),
    )
}

fun TallyLabelDTO.toTallyLabelDO(): TallyLabelDO {
    return TallyLabelDO(
        uid = this.uid,
        createTime = this.createTime,
        colorInnerIndex = TallyLabelService.labelInnerColorList.indexOfFirst { it.toArgb() == this.colorInt }
            .apply {
                if (this == -1) {
                    notSupportError()
                }
            },
        nameInnerIndex = this.name.nameRsd?.toNameInnerIndex(),
        name = this.name.name,
    )
}

fun TallyAccountTypeInsertDTO.toTallyAccountTypeDO(): TallyAccountTypeDO {
    return TallyAccountTypeDO(
        order = this.order,
        nameInnerIndex = this.nameRsd?.run {
            ResData.getNameRsdIndex(rsdId = this)
        },
        name = this.name,
    )
}

fun TallyAccountTypeDTO.toTallyAccountTypeDO(): TallyAccountTypeDO {
    return TallyAccountTypeDO(
        uid = this.uid,
        order = this.order,
        nameInnerIndex = this.nameRsd?.run {
            ResData.getNameRsdIndex(rsdId = this)
        },
        name = this.name,
    )
}

fun TallyAccountTypeDO.toTallyAccountTypeDTO(): TallyAccountTypeDTO {
    return TallyAccountTypeDTO(
        uid = this.uid,
        order = this.order,
        nameRsd = if (this.nameInnerIndex == null) null else ResData.resStringIndexList[this.nameInnerIndex],
        name = this.name,
    )
}

fun TallyAccountDO.toTallyAccountDTO(): TallyAccountDTO {
    return TallyAccountDTO(
        uid = this.uid,
        typeId = this.typeId,
        isDefault = this.isDefault,
        iconRsd = ResData.resIconIndexList[this.iconInnerIndex],
        nameRsd = if (this.nameInnerIndex == null) null else ResData.resStringIndexList[this.nameInnerIndex],
        name = this.name,
        initialBalance = this.initialBalance,
        balance = this.balance,
    )
}

fun TallyAccountWithTypeDO.toTallyAccountWithTypeDTO(): TallyAccountWithTypeDTO {
    return TallyAccountWithTypeDTO(
        account = this.account.toTallyAccountDTO(),
        accountType = this.accountType.toTallyAccountTypeDTO(),
    )
}

fun TallyAccountInsertDTO.toTallyAccountDO(): TallyAccountDO {
    return TallyAccountDO(
        typeId = this.typeId,
        isDefault = this.isDefault,
        iconInnerIndex = ResData.getIconRsdIndex(rsdId = this.iconRsd),
        nameInnerIndex = this.nameRsd?.run {
            ResData.getNameRsdIndex(rsdId = this)
        },
        name = this.name,
        initialBalance = this.initialBalance,
        balance = initialBalance,
    )
}

fun TallyAccountDTO.toTallyAccountDO(): TallyAccountDO {
    return TallyAccountDO(
        uid = this.uid,
        typeId = this.typeId,
        isDefault = this.isDefault,
        iconInnerIndex = ResData.getIconRsdIndex(rsdId = this.iconRsd),
        nameInnerIndex = this.nameRsd?.run {
            ResData.getNameRsdIndex(rsdId = this)
        },
        name = this.name,
        initialBalance = this.initialBalance,
        balance = balance,
    )
}

fun TallyBillLabelInsertDTO.toTallyBillLabelDO(): TallyBillLabelDO {
    return TallyBillLabelDO(
        billId = this.billId,
        labelId = this.labelId,
    )
}

fun TallyBillLabelDO.toTallyBillLabelDTO(): TallyBillLabelDTO {
    return TallyBillLabelDTO(
        uid = this.uid,
        billId = this.billId,
        labelId = this.labelId,
    )
}

fun TallyCategoryWithGroupDO.toTallyCategoryWithGroupDTO(): TallyCategoryWithGroupDTO {
    return TallyCategoryWithGroupDTO(
        category = this.category.toTallyCategoryDTO(),
        group = this.group.toTallyCategoryGroupDTO(),
    )
}

suspend fun TallyBillDetailDO.toTallyBillDetailDTO(): TallyBillDetailDTO {
    val target = this
    return withContext(Dispatchers.IO) {
        return@withContext TallyBillDetailDTO(
            bill = target.bill.toTallyBillDTO(),
            book = target.book.toTallyBookDTO(),
            account = target.account.toTallyAccountDTO(),
            transferTargetAccount = target.transferTargetAccount?.toTallyAccountDTO(),
            categoryWithGroup = target.categoryWithGroup?.toTallyCategoryWithGroupDTO(),
            labelList = target.labelList.map { it.toTallyLabelDTO() },
            imageUrlList = tallyImageService.getListByKey1(key1 = target.bill.uid).map { it.url }
        )
    }
}
