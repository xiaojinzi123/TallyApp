package com.xiaojinzi.tally.home.module.main.data

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.annotation.StringRes
import com.xiaojinzi.tally.home.R

@Keep
data class TabVO(
    @StringRes
    val nameRsd: Int,
    @DrawableRes
    val iconRsd: Int,
    @DrawableRes
    val iconSelectedRsd: Int,
)

val tabList = listOf(
    TabVO(
        nameRsd = R.string.res_str_home,
        iconRsd = R.drawable.res_home1,
        iconSelectedRsd = R.drawable.res_home1_selected,
    ),
    TabVO(
        nameRsd = R.string.res_str_calendar,
        iconRsd = R.drawable.res_calendar1,
        iconSelectedRsd = R.drawable.res_calendar1_selected,
    ),
    TabVO(
        nameRsd = R.string.res_str_stat,
        iconRsd = R.drawable.res_daynight_statistical1,
        iconSelectedRsd = R.drawable.res_daynight_statistical1_selected,
    ),
    TabVO(
        nameRsd = R.string.res_str_my,
        iconRsd = R.drawable.res_daynight_my1,
        iconSelectedRsd = R.drawable.res_daynight_my1_selected,
    )
)