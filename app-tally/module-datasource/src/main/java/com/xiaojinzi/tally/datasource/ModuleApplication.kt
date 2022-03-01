package com.xiaojinzi.tally.datasource

import android.app.Application
import com.xiaojinzi.component.anno.ModuleAppAnno
import com.xiaojinzi.component.application.IApplicationLifecycle
import com.xiaojinzi.component.application.IModuleNotifyChanged
import com.xiaojinzi.module.base.support.ResData
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.module.base.support.DevelopHelper
import kotlinx.coroutines.DelicateCoroutinesApi

@ModuleAppAnno
class ModuleApplication : IApplicationLifecycle, IModuleNotifyChanged {

    override fun onCreate(app: Application) {
        // Debug 的时候检查, release 就正常了
        if (DevelopHelper.isDevelop) {
            ResData.resIconIndexList.groupBy { it }
                .forEach { item ->
                    Assert.assertEquals(
                        value1 = item.value.size,
                        1,
                        message = "index is ${ResData.getIconRsdIndex(rsdId = item.key)}"
                    )
                }
            ResData
                .resIconCategoryList
                .forEachIndexed { index1, groupItem ->
                    groupItem.second.forEachIndexed { index2, item ->
                        Assert.assertNotEquals(
                            value1 = ResData.resIconIndexList.indexOf(element = item),
                            -1,
                            message = "index1 = $index1, index2 = $index2"
                        )
                    }
                }
            ResData.resStringIndexList.groupBy { it }
                .forEach { item ->
                    Assert.assertEquals(
                        value1 = item.value.size,
                        1,
                        message = "index is ${ResData.getNameRsdIndex(rsdId = item.key)}"
                    )
                }
        }
    }

    override fun onDestroy() {
    }

    @DelicateCoroutinesApi
    override fun onModuleChanged(app: Application) {
        // empty
    }


}