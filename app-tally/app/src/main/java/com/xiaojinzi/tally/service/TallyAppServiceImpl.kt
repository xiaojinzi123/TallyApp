package com.xiaojinzi.tally.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.service.ImageUploadServerType
import com.xiaojinzi.module.base.support.flow.NormalMutableSharedFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.tally.BuildConfig
import com.xiaojinzi.tally.base.service.AppChannelDTO
import com.xiaojinzi.tally.base.service.TallyAppService
import com.xiaojinzi.tally.base.service.datasource.TallyBudgetInsertDTO
import com.xiaojinzi.tally.base.support.tallyAppService
import com.xiaojinzi.tally.base.support.tallyBudgetService
import com.xiaojinzi.tally.base.support.tallyService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@ServiceAnno(TallyAppService::class)
class TallyAppServiceImpl : TallyAppService {

    override val isInsertTestData: Boolean
        get() = BuildConfig.insertTestData

    override val imageType: ImageUploadServerType
        get() = ImageUploadServerType.fromValue(value = BuildConfig.imageType)

    override val autoBillEnable: Boolean
        get() = BuildConfig.autoBill

    override val buglyEnable: Boolean
        get() = BuildConfig.bugly

    override val buglyAppkey: String
        get() = BuildConfig.buglyAppkey

    override val appChannel = AppChannelDTO.parseChannel(value = BuildConfig.appChannel)

    override val appTaskInitPublishObservableDTO = NormalMutableSharedFlow<Unit>()

    override val appTaskInitBehaviorObservableDTO =
        appTaskInitPublishObservableDTO.sharedStateIn(scope = AppScope)

    override fun startInitTask() {
        //  开始走异步任务
        AppScope.launch(Dispatchers.Main) {

            // 在一个事务中处理
            tallyService.withTransaction {
                tallyService.initDataIfNoData()
            }
            tallyService.withTransaction {
                if (tallyAppService.isInsertTestData) {
                    tallyService.initTestDataIfNoData(isCreateBill = true)
                }
            }

            // 表示已经初始化完毕了
            appTaskInitPublishObservableDTO.tryEmit(value = Unit)

            // 接下去是业务的一些初始化. 不是必要的

            // 如果当月没有设置预算, 但是设置了默认预算, 那就插入一条当月预算
            tallyBudgetService.monthlyDefaultBudgetObservableDTO.first()?.let { defaultBudget ->
                val currentTime = System.currentTimeMillis()
                val currentMonthBudget = tallyBudgetService.getByMonthTime(currentTime)
                if (currentMonthBudget == null) {
                    tallyBudgetService.insert(
                        target = TallyBudgetInsertDTO(
                            monthTime = currentTime,
                            value = defaultBudget,
                        )
                    )
                }
            }

        }
    }

}