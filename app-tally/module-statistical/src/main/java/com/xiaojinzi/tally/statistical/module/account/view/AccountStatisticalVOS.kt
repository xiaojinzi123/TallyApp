package com.xiaojinzi.tally.statistical.module.account.view

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

enum class AccountStatisticalOverviewType{
    Home, My
}

@Keep
data class AccountStatisticalOverviewVO(
    // 总资产, 正常应该是 > 0 的
    val totalAsset: Long,
    // 债务资产 正常应该是 > 0 的 表示欠了多少
    val debtAsset: Long,
    // 净资产
    val netAsset: Long = totalAsset - debtAsset,
)

@Keep
data class AccountStatisticalItemVO(
    val accountId: String,
    val isDefault: Boolean,
    @DrawableRes
    val iconRsd: Int,
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
    // 余额
    val balance: Long
) {

    @Composable
    fun getNameAdapter(): String {
        return name ?: stringResource(id = nameRsd!!)
    }

}

@Keep
data class AccountStatisticalGroupVO(
    @StringRes
    val nameRsd: Int? = null,
    val name: String? = null,
    // 余额
    val balance: Long,
    val items: List<AccountStatisticalItemVO> = emptyList(),
) {

    @Composable
    fun getNameAdapter(): String {
        return name ?: stringResource(id = nameRsd!!)
    }

}