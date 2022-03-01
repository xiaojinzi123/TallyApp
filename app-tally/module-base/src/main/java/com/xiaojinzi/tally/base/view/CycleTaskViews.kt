package com.xiaojinzi.tally.base.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.base.R
import com.xiaojinzi.tally.base.service.datasource.TallyBillDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillTypeDTO
import com.xiaojinzi.tally.base.support.tallyCostAdapter
import com.xiaojinzi.tally.base.support.tallyNumberFormat1

@Composable
fun CycleTaskItem(
    modifier: Modifier = Modifier,
    billDetail: TallyBillDetailDTO,
) {

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 12.dp)
                .nothing(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val targetName: StringItemDTO = when (billDetail.bill.type) {
                TallyBillTypeDTO.Transfer -> R.string.res_str_transfer.toStringItemDTO()
                TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement -> billDetail.categoryWithGroup?.category?.getStringItemVO()
                    ?: "---".toStringItemDTO()
            }
            val targetIcon = when (billDetail.bill.type) {
                TallyBillTypeDTO.Transfer -> R.drawable.res_transfer2
                TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement -> billDetail.categoryWithGroup?.category?.iconRsd
                    ?: R.drawable.res_question_mark1
            }
            Image(
                modifier = Modifier
                    .size(size = 24.dp)
                    .nothing(),
                painter = painterResource(id = targetIcon),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(width = 16.dp))

            Text(
                text = targetName.nameAdapter(),
                style = MaterialTheme.typography.body2,
            )

            Spacer(modifier = Modifier.weight(weight = 1f, fill = true))

            Column(
                horizontalAlignment = Alignment.End,
            ) {

                Text(
                    text = "${
                        if (billDetail.bill.costAdjust > 0) {
                            "+"
                        } else {
                            ""
                        }
                    }${
                        billDetail.bill.costAdjust.tallyCostAdapter().tallyNumberFormat1()
                    }",
                    style = MaterialTheme.typography.body2.copy(
                        color = Color.Red,
                    ),
                )

                Spacer(modifier = Modifier.height(height = 4.dp))

                val accountInfo = when (billDetail.bill.type) {
                    TallyBillTypeDTO.Transfer -> {
                        "${
                            billDetail.account.getStringItemVO().nameAdapter()
                        } → ${
                            billDetail.transferTargetAccount?.getStringItemVO()
                                ?.nameAdapter() ?: "?"
                        }"
                    }
                    TallyBillTypeDTO.Normal, TallyBillTypeDTO.Reimbursement -> {
                        billDetail.account.getStringItemVO().nameAdapter()
                    }
                }

                Text(
                    text = "[${billDetail.book.nameItem.nameAdapter()}] $accountInfo",
                    style = MaterialTheme.typography.body2,
                )
            }

        }
    }

}
