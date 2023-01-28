package com.xiaojinzi.tally.datasource.service

import androidx.room.InvalidationTracker
import androidx.room.withTransaction
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.lib.res.dto.AutoBillSourceAppType
import com.xiaojinzi.lib.res.dto.AutoBillSourceViewType
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.ResData
import com.xiaojinzi.module.base.support.dbCommonService
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.spService
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.base.support.*
import com.xiaojinzi.tally.datasource.R
import com.xiaojinzi.tally.datasource.data.categoryGroupInitRsdIndexList
import com.xiaojinzi.tally.datasource.data.testBillList
import com.xiaojinzi.tally.datasource.data.testLabelList
import com.xiaojinzi.tally.datasource.data.toTallCategoryDO
import com.xiaojinzi.tally.datasource.db.dbTally
import com.xiaojinzi.tally.datasource.db.tableNamesOfAccount
import com.xiaojinzi.tally.datasource.db.totalTableNames
import hu.akarnokd.kotlin.flow.toList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

@FlowPreview
@DelicateCoroutinesApi
@ServiceAnno(TallyService::class)
class TallyServiceImpl : TallyService {

    private val _dataBaseChangedObservable =
        MutableSharedStateFlow(initValue = Unit)

    private val _dataBaseChangedExceptAccountTableObservable =
        MutableSharedStateFlow(initValue = Unit)

    @FlowPreview
    override val dataBaseChangedObservable = _dataBaseChangedObservable

    @FlowPreview
    override val dataBaseChangedExceptAccountTableObservable =
        _dataBaseChangedExceptAccountTableObservable

    override suspend fun isInitTestData(): Boolean {
        return spService.getBool(key = "isInsertTestData") ?: false
    }

    override suspend fun initDataIfNoData() {

        LogSupport.d(
            content = "initDataIfNoData 初始化开始",
            keywords = arrayOf(TallyLogKeyword.DATE_INIT)
        )

        val isInitAccount = dbCommonService.getBoolean(key = DBCommonKeys.dataInitAccount) ?: false
        if (!isInitAccount) {
            // 插入几个内置的账户类型
            val accountTypeListDTO = tallyAccountTypeService.insertListAndReturn(
                targetList = listOf(
                    TallyAccountTypeInsertDTO(
                        nameRsd = R.string.res_str_normal_account
                    ),
                    /*TallyAccountTypeInsertDTO(
                        nameRsd = R.string.res_str_capital_account
                    ),
                    TallyAccountTypeInsertDTO(
                        nameRsd = R.string.res_str_credit_account
                    ),
                    TallyAccountTypeInsertDTO(
                        nameRsd = R.string.res_str_top_up_account
                    ),
                    TallyAccountTypeInsertDTO(
                        nameRsd = R.string.res_str_financial_account
                    ),
                    TallyAccountTypeInsertDTO(
                        nameRsd = R.string.res_str_asset_account
                    ),*/
                )
            )

            // 插入默认的账户
            tallyAccountService.insert(
                target = TallyAccountInsertDTO(
                    typeId = accountTypeListDTO.random().uid,
                    isDefault = true,
                    iconRsd = R.drawable.res_transfer1,
                    nameRsd = R.string.res_str_default_account,
                    initialBalance = 0,
                )
            )
        }
        dbCommonService.saveBoolean(key = DBCommonKeys.dataInitAccount, value = true)

        val isInitBook = dbCommonService.getBoolean(key = DBCommonKeys.dataInitBook) ?: false
        if (!isInitBook) {
            // 插入默认的账本和其他几个内置的账本
            tallyBookService.insertList(
                targetList = listOf(
                    TallyBookInsertDTO(
                        isDefault = true,
                        nameRsd = R.string.res_str_default_bill_book,
                    ),
                    TallyBookInsertDTO(
                        nameRsd = R.string.res_str_travel_book,
                    ),
                    TallyBookInsertDTO(
                        nameRsd = R.string.res_str_business_book,
                    ),
                    TallyBookInsertDTO(
                        nameRsd = R.string.res_str_baby_book,
                    ),
                )
            )
        }
        dbCommonService.saveBoolean(key = DBCommonKeys.dataInitBook, value = true)

        val isInitCategory =
            dbCommonService.getBoolean(key = DBCommonKeys.dataInitCategory) ?: false
        if (!isInitCategory) {
            // 插入内置的类别
            categoryGroupInitRsdIndexList
                .forEach { entity ->
                    val cateGroup = TallyCategoryGroupInsertDTO(
                        type = entity.type,
                        iconInnerIndex = entity.iconRsdIndex,
                        nameInnerIndex = entity.nameRsdIndex,
                        isBuiltIn = true,
                    )
                    val groupId = cateGroup
                        .toTallCategoryDO()
                        .copy(isBuiltIn = true)
                        .apply {
                            dbTally
                                .tallyCategoryGroupDao()
                                .insert(
                                    value = this
                                )
                        }
                        .uid
                    val categoryDOList = entity
                        .items
                        .map { item ->
                            TallyCategoryInsertDTO(
                                groupId = groupId,
                                iconIndex = item.iconIndex,
                                nameIndex = item.nameIndex,
                                isBuiltIn = true,
                            )
                        }
                        .map {
                            it
                                .toTallCategoryDO()
                                .copy(isBuiltIn = true)
                        }
                        .toList()
                    dbTally.tallyCategoryDao().insertList(categoryDOList)
                }
        }
        dbCommonService.saveBoolean(key = DBCommonKeys.dataInitCategory, value = true)

        // 自动记账的相关数据

        val alipaySourceApp = tallyBillAutoSourceAppService.getDetailByType(
            sourceType = AutoBillSourceAppType.Alipay
        )?.core ?: tallyBillAutoSourceAppService.insert(
            target = TallyBillAutoSourceAppInsertDTO(
                sourceAppType = AutoBillSourceAppType.Alipay,
                name = StringItemDTO(
                    nameRsd = R.string.res_str_alipay
                )
            ),
        )

        val weChatSourceApp = tallyBillAutoSourceAppService.getDetailByType(
            sourceType = AutoBillSourceAppType.WeChat
        )?.core ?: tallyBillAutoSourceAppService.insert(
            target = TallyBillAutoSourceAppInsertDTO(
                sourceAppType = AutoBillSourceAppType.WeChat,
                name = StringItemDTO(
                    nameRsd = R.string.res_str_wechat
                )
            ),
        )

        val ysfSourceApp = tallyBillAutoSourceAppService.getDetailByType(
            sourceType = AutoBillSourceAppType.YSF
        )?.core ?: tallyBillAutoSourceAppService.insert(
            target = TallyBillAutoSourceAppInsertDTO(
                sourceAppType = AutoBillSourceAppType.YSF,
                name = StringItemDTO(
                    nameRsd = R.string.res_str_ysf
                )
            ),
        )

        val needInsertList = listOf(
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = alipaySourceApp,
                sourceViewType = AutoBillSourceViewType.Alipay_BILL_Detail,
                name = "支付宝账单详情".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = alipaySourceApp,
                sourceViewType = AutoBillSourceViewType.Alipay_Pay_Success,
                name = "支付宝支付成功".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = alipaySourceApp,
                sourceViewType = AutoBillSourceViewType.Alipay_Transfer_Success,
                name = "支付宝转账成功".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = weChatSourceApp,
                sourceViewType = AutoBillSourceViewType.WeChat_BILL_Detail,
                name = "微信账单详情".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = weChatSourceApp,
                sourceViewType = AutoBillSourceViewType.WeChat_Pay_Success,
                name = "微信支付成功".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = ysfSourceApp,
                sourceViewType = AutoBillSourceViewType.YSF_BILL_Detail1,
                name = "云闪付-我的-账单详情".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = ysfSourceApp,
                sourceViewType = AutoBillSourceViewType.YSF_BILL_Detail2,
                name = "云闪付-消息-交易详情".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = ysfSourceApp,
                sourceViewType = AutoBillSourceViewType.YSF_BILL_Detail3,
                name = "云闪付-消息-账单详情".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = ysfSourceApp,
                sourceViewType = AutoBillSourceViewType.YSF_Pay_Success,
                name = "云闪付支付成功".toStringItemDTO(),
            ),
            TallyBillAutoSourceViewInsertDTO(
                sourceApp = ysfSourceApp,
                sourceViewType = AutoBillSourceViewType.YSF_Transfer_Success,
                name = "云闪付转账成功".toStringItemDTO(),
            ),
        ).filter { tallyBillAutoSourceViewService.getDetailByType(type = it.sourceViewType) == null }

        // 插入自动记账支持的页面类型的来源表
        tallyBillAutoSourceViewService.insertList(
            targetList = needInsertList
        )

        LogSupport.d(
            content = "initDataIfNoData 初始化完毕",
            keywords = arrayOf(TallyLogKeyword.DATE_INIT)
        )

    }

    override suspend fun flagInitData(b: Boolean) {
        dbCommonService.saveBoolean(key = "isInsertTallyData", value = b)
    }

    override suspend fun initTestDataIfNoData(isCreateBill: Boolean) {

        LogSupport.d(
            content = "initTestDataIfNoData 初始化开始",
            keywords = arrayOf(TallyLogKeyword.DATE_INIT)
        )

        val isInsertTestData = isInitTestData()
        if (isInsertTestData) {
            return
        }
        flagInitTestData(b = true)

        // 插入几个账本
        val tallyBookList = tallyBookService.insertList(
            targetList = (1..4).map {
                TallyBookInsertDTO(
                    name = "账本$it"
                )
            }
        )

        // 账户类型的集合对象
        val accountTypeListDTO = tallyAccountTypeService.getAll()

        // 插入几个测试账户
        val tallyAccountList = tallyAccountService.insertList(targetList = (1..4).map {
            TallyAccountInsertDTO(
                typeId = accountTypeListDTO.random().uid,
                iconRsd = ResData.resIconIndexList.random(),
                name = "测试账号$it",
                initialBalance = Random.nextLong(from = 10000, until = 100000),
            )
        })

        // 插入一些标签
        val labelList = tallyLabelService.getAll().run {
            if (this.isNullOrEmpty()) {
                tallyLabelService.insertList(targetList = testLabelList)
            } else {
                this
            }
        }

        // 插入测试的记录
        val categoryList = tallyCategoryService.getAllTallyCategories()

        val billList = if (isCreateBill) {
            testBillList
                .asFlow()
                .map {
                    // 是否是转账
                    val isTransfer = Random.nextBoolean() && Random.nextBoolean()
                    // 是否是报销单
                    val isReimbursement =
                        Random.nextBoolean() && Random.nextBoolean() && Random.nextBoolean()
                    if (isReimbursement) {
                        val isReimbursed = Random.nextBoolean()
                        TallyBillInsertDTO(
                            type = TallyBillTypeDTO.Normal,
                            time = it.createTime,
                            accountId = tallyAccountList.random().uid,
                            transferTargetAccountId = null,
                            bookId = tallyBookList.random().uid,
                            categoryId = categoryList.random().uid,
                            cost = it.cost,
                            note = it.note,
                            reimburseType = if (isReimbursed) ReimburseType.Reimbursed else ReimburseType.WaitReimburse,
                            isNotIncludedInIncomeAndExpenditure = false,
                        )
                    } else if (isTransfer) {
                        TallyBillInsertDTO(
                            type = TallyBillTypeDTO.Transfer,
                            time = it.createTime,
                            accountId = tallyAccountList.random().uid,
                            transferTargetAccountId = tallyAccountList.random().uid,
                            bookId = tallyBookList.random().uid,
                            categoryId = null,
                            cost = it.cost,
                            note = it.note,
                            isNotIncludedInIncomeAndExpenditure = false,
                        )
                    } else {
                        TallyBillInsertDTO(
                            type = TallyBillTypeDTO.Normal,
                            time = it.createTime,
                            accountId = tallyAccountList.random().uid,
                            transferTargetAccountId = null,
                            bookId = tallyBookList.random().uid,
                            categoryId = categoryList.random().uid,
                            cost = it.cost,
                            note = it.note,
                            isNotIncludedInIncomeAndExpenditure = Random.nextBoolean() && Random.nextBoolean(),
                        )
                    }
                }
                .toList()
                .map {
                    tallyBillService.insertBills(targetList = it)
                }
                .first()
        } else {
            emptyList()
        }

        billList
            .filter { it.type == TallyBillTypeDTO.Normal }
            .filter {
                it.reimburseType in listOf(
                    ReimburseType.WaitReimburse,
                    ReimburseType.Reimbursed
                )
            }
            .forEach { billObj ->
                val isReimbursed = Random.nextBoolean()
                if (isReimbursed) {
                    billObj.copy(
                        type = TallyBillTypeDTO.Reimbursement,
                        time = billObj.time + Random.nextLong(123456),
                        reimburseType = ReimburseType.NoReimburse,
                        reimburseBillId = billObj.uid,
                    )
                } else {
                    billObj.copy(
                        type = TallyBillTypeDTO.Reimbursement,
                        time = billObj.time + Random.nextLong(123456),
                        cost = billObj.cost / Random.nextInt(from = 2, until = 8),
                        reimburseType = ReimburseType.NoReimburse,
                        reimburseBillId = billObj.uid,
                    )
                }
            }

        // 随机插入一些账单的标签
        billList.forEach { billItem ->
            val isInsert1 = Random.nextBoolean()
            val isInsert2 = Random.nextBoolean()
            if (isInsert1 && isInsert2) {
                val labelCount = Random.nextInt(1, labelList.size + 1)
                repeat(times = labelCount) {
                    tallyBillLabelService.insertIfNotExist(
                        target = TallyBillLabelInsertDTO(
                            billId = billItem.uid,
                            labelId = labelList.random().uid,
                        )
                    )
                }
            }
        }

        billList.forEach { billItem ->
            val isInsert1 = Random.nextBoolean()
            val isInsert2 = Random.nextBoolean()
            if (isInsert1 && isInsert2) {
                val labelCount = Random.nextInt(1, 8)
                repeat(times = labelCount) {
                    tallyImageService.insert(
                        target = TallyImageInsertDTO(
                            key1 = billItem.uid,
                            url = TallyService.testImageList.random(),
                        )
                    )
                }
            }
        }

        // 设计插入一些图片

        LogSupport.d(
            content = "initTestDataIfNoData 初始化完毕",
            keywords = arrayOf(TallyLogKeyword.DATE_INIT)
        )

    }

    override suspend fun flagInitTestData(b: Boolean) {
        spService.putBool(key = "isInsertTestData", value = b)
    }

    override suspend fun withTransaction(block: suspend () -> Unit) {
        dbTally.withTransaction {
            withContext(context = Dispatchers.Main) {
                block.invoke()
            }
        }
    }

    init {

        LogSupport.d(content = "初始化", keywords = arrayOf(TallyLogKeyword.TALLY_SERVICE))

        // 监听数据库变化, 进行通知
        AppScope.launch(context = Dispatchers.IO) {
            dbTally.invalidationTracker.addObserver(object :
                InvalidationTracker.Observer(
                    totalTableNames
                        .toTypedArray()
                ) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    _dataBaseChangedObservable.value = Unit
                }
            })
            dbTally.invalidationTracker.addObserver(object :
                InvalidationTracker.Observer(
                    totalTableNames
                        .filter { it !in tableNamesOfAccount }
                        .toTypedArray()
                ) {
                override fun onInvalidated(tables: MutableSet<String>) {
                    _dataBaseChangedExceptAccountTableObservable.value = Unit
                }
            })
            LogSupport.d(
                content = "执行监听数据库变化的代码",
                keywords = arrayOf(TallyLogKeyword.TALLY_SERVICE, TallyLogKeyword.TALLY_DATABASE)
            )
        }

        // 数据库变化打印一个日志
        dataBaseChangedObservable
            .onEach {
                LogSupport.d(
                    content = "监听到数据库改变啦!!!!!!",
                    keywords = arrayOf(
                        TallyLogKeyword.TALLY_SERVICE,
                        TallyLogKeyword.TALLY_DATABASE
                    )
                )
            }
            .launchIn(scope = AppScope)

    }

}