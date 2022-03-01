package com.xiaojinzi.tally.base.view

import androidx.annotation.ColorInt
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.service.datasource.TallyLabelDTO

fun TallyLabelDTO.toTallyLabelVO(): TallyLabelVO {
    return TallyLabelVO(
        name = this.name,
        colorLong = this.colorInt,
    )
}

@Keep
data class TallyLabelVO(
    @StringRes
    val nameRsd: Int? = null,
    val name: StringItemDTO,
    @ColorInt
    val colorLong: Int,
)

@Composable
fun TallyLabelList(labelList: List<TallyLabelVO>) {
    FlowRow(
        mainAxisSpacing = 4.dp,
        crossAxisSpacing = 4.dp,
        modifier = Modifier
            .padding(top = 4.dp)
            .nothing(),
    ) {
        labelList.forEachIndexed { _, item ->
            Text(
                modifier = Modifier
                    .background(
                        color = Color(color = item.colorLong),
                        shape = CircleShape,
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .nothing(),
                text = item.name.nameAdapter(),
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 8.sp
                )
            )
        }
    }
}