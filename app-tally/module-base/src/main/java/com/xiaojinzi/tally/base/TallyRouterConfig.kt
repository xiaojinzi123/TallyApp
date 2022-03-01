package com.xiaojinzi.tally.base

import android.provider.Settings

// ------------------------------------ 公共的 ------------------------------------

object TallyRouterConfig {

    // ------------------------------------ 记账项目相关的 ------------------------------------
    private const val TALLY_HOST_DEVELOP = "develop"
    const val TALLY_DEVELOP_DATA_STATISTICAL = "$TALLY_HOST_DEVELOP/dataStatistical"

    private const val TALLY_HOST_LOADING = "loading"
    const val TALLY_LOADING_MAIN = "$TALLY_HOST_LOADING/main"

    private const val TALLY_HOST_HOME = "home"
    const val TALLY_HOME_MAIN = "$TALLY_HOST_HOME/main"
    const val TALLY_CATEGORY = "$TALLY_HOST_HOME/category"
    const val TALLY_CATEGORY_CREATE = "$TALLY_HOST_HOME/categoryCreate"
    const val TALLY_CATEGORY_GROUP_CREATE = "$TALLY_HOST_HOME/categoryGroupCreate"

    private const val TALLY_HOST_ACCOUNT = "account"
    const val TALLY_ACCOUNT_MAIN = "$TALLY_HOST_ACCOUNT/main"
    const val TALLY_ACCOUNT_CREATE = "$TALLY_HOST_ACCOUNT/create"
    const val TALLY_ACCOUNT_DETAIL = "$TALLY_HOST_ACCOUNT/detail"
    const val TALLY_ACCOUNT_SELECT = "$TALLY_HOST_ACCOUNT/accountSelect"

    const val TALLY_LABEL_LIST = "$TALLY_HOST_HOME/labelList"
    const val TALLY_LABEL_CREATE = "$TALLY_HOST_HOME/labelCreate"
    const val TALLY_NOTE = "$TALLY_HOST_HOME/note"

    private const val TALLY_HOST_MY = "my"
    const val TALLY_MY = "$TALLY_HOST_MY/main"
    const val TALLY_SETTING = "$TALLY_HOST_MY/setting"
    const val TALLY_ABOUT = "$TALLY_HOST_MY/about"

    private const val TALLY_HOST_STATISTICAL = "statistical"

    private const val TALLY_HOST_BILL = "bill"
    const val TALLY_BILL_BUDGET = "$TALLY_HOST_BILL/budget"
    const val TALLY_BILL_BUDGET_SET = "$TALLY_HOST_BILL/budgetSet"
    const val TALLY_BILL_AUTO = "$TALLY_HOST_BILL/autoBill"
    const val TALLY_BILL_AUTO_DEFAULT_CATEGORY = "$TALLY_HOST_BILL/autoBillDefaultCategory"
    const val TALLY_BILL_AUTO_DEFAULT_ACCOUNT = "$TALLY_HOST_BILL/autoBillDefaultAccount"
    const val TALLY_BILL_AUTO_CREATE = "$TALLY_HOST_BILL/autoBillCreate"
    const val TALLY_BILL_MONTHLY = "$TALLY_HOST_BILL/monthlyBill"
    const val TALLY_BILL_LABEL_LIST = "$TALLY_HOST_BILL/labelBillList"
    const val TALLY_BILL_CATEGORY_GROUP = "$TALLY_HOST_BILL/categoryGroupBill"
    const val TALLY_BILL_BOOK = "$TALLY_HOST_BILL/book"
    const val TALLY_BILL_BOOK_CREATE = "$TALLY_HOST_BILL/bookCreate"
    const val TALLY_BILL_BOOK_DETAIL = "$TALLY_HOST_BILL/bookDetail"
    const val TALLY_BILL_BOOK_SELECT = "$TALLY_HOST_BILL/bookSelect"
    const val TALLY_BILL_CREATE = "$TALLY_HOST_BILL/billCreate"
    const val TALLY_BILL_SEARCH = "$TALLY_HOST_BILL/billSearch"
    const val TALLY_BILL_DETAIL = "$TALLY_HOST_BILL/billDetail"
    const val TALLY_SUB_REIMBURSEMENT_BILL_LIST = "$TALLY_HOST_BILL/subReimbursementBillList"
    const val TALLY_CYCLE_BILL = "$TALLY_HOST_BILL/cycleBill"
    const val TALLY_CYCLE_BILL_CREATE = "$TALLY_HOST_BILL/cycleBillCreate"
    const val TALLY_CYCLE_BILL_TASK = "$TALLY_HOST_BILL/cycleBillTask"

    private const val TALLY_HOST_APP = "app"
    const val TALLY_APP_EVALUATION = "$TALLY_HOST_APP/evaluation"

}
