package com.xiaojinzi.tally.bill.module.cate_create.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.xiaojinzi.module.base.view.compose.CommonEditText
import com.xiaojinzi.support.ktx.nothing
import com.xiaojinzi.tally.bill.R

@Composable
fun TestView() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .statusBarsPadding()
            .padding(horizontal = 0.dp, vertical = 20.dp)
            .nothing(),
    ) {
        val (
            nameName, nameValue,
            passName, passValue,
        ) = createRefs()
        val barrier = createEndBarrier(nameName, passName, margin = 0.dp)
        Text(
            modifier = Modifier
                .constrainAs(ref = nameName) {
                    this.start.linkTo(parent.start)
                    this.top.linkTo(parent.top)
                    centerVerticallyTo(nameValue)
                }
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .nothing(),
            text = stringResource(id = R.string.res_str_name),
            style = MaterialTheme.typography.subtitle1,
        )
        CommonEditText(
            modifier = Modifier
                // .width(0.dp)
                .constrainAs(ref = nameValue) {
                    this.start.linkTo(barrier)
                    this.end.linkTo(parent.end)
                    this.top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                // .width(width = Dimension.fillToConstraints)
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .nothing(),
            text = "123123123",
            onTextChanged = {}
        )
        /*Text(
            modifier = Modifier
                // .width(0.dp)
                .constrainAs(ref = nameValue) {
                    this.start.linkTo(barrier)
                    this.end.linkTo(parent.end)
                    this.top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                // .width(width = Dimension.fillToConstraints)
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .nothing(),
            text = "nameValue",
            style = MaterialTheme.typography.subtitle1,
        )*/
        Text(
            modifier = Modifier
                .constrainAs(ref = passName) {
                    this.top.linkTo(nameValue.bottom)
                    this.start.linkTo(parent.start)
                }
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .nothing(),
            text = stringResource(id = R.string.res_str_my_account),
            style = MaterialTheme.typography.subtitle1,
        )
        Text(
            modifier = Modifier
                // .width(0.dp)
                .constrainAs(ref = passValue) {
                    this.top.linkTo(nameValue.bottom)
                    this.start.linkTo(barrier)
                     this.end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(horizontal = 0.dp, vertical = 10.dp)
                .nothing(),
            text = "123123123",
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

@Preview
@Composable
private fun TestViewPreview() {
    TestView()
}