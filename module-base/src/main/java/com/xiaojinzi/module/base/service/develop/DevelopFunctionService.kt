package com.xiaojinzi.module.base.service.develop

import android.content.Context
import androidx.annotation.Keep
import com.xiaojinzi.component.impl.service.ServiceManager
import kotlinx.coroutines.flow.Flow

@Keep
data class DevelopPageConfig(
    val pageName: String,
    val groups: List<DevelopGroupConfig> = emptyList(),
)

@Keep
data class DevelopGroupConfig(
    val groupName: String,
    val actionName: String = "",
    val items: List<DevelopItemConfig> = emptyList(),
    val action: () -> Unit = {},
)

/**
 * 开发者功能的 Item
 */
@Keep
abstract class DevelopItemConfig(
    open val content: String,
)

@Keep
data class DevelopItemCheckBoxConfig(
    override val content: String,
    // 开发者的 key, 用于订阅数据
    val developKey: String,
) : DevelopItemConfig(content = content)

@Keep
data class DevelopItemInfoViewConfig(
    override val content: String,
    // 监听的流
    val observable: Flow<String>? = null,
) : DevelopItemConfig(content = content)

@Keep
data class DevelopItemActionConfig(
    override val content: String,
    val action: (context: Context) -> Unit,
) : DevelopItemConfig(content = content)


/**
 * 每个项目自己会实现的
 */
interface DevelopFunctionService {

    companion object {

        const val ImplName_Tally = "app-tally"
        const val ImplName_System = "module-system"
        val implNames = listOf(ImplName_Tally, ImplName_System)

        /**
         * 获取所有开发者功能的实现
         */
        fun getFunctionList(): List<DevelopFunctionService> {
            return implNames
                .mapNotNull { name ->
                    ServiceManager.get(DevelopFunctionService::class.java, name)
                }
                .sortedByDescending { it.order }
        }

    }

    val order: Int
        get() = 0

    /**
     * 开发者界面首页的配置
     */
    val developMainGroup: List<DevelopGroupConfig>

}