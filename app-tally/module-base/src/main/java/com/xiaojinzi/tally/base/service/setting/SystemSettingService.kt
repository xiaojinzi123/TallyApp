package com.xiaojinzi.tally.base.service.setting

import android.os.Build
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.bean.StringItemDTO
import com.xiaojinzi.module.base.bean.toStringItemDTO
import com.xiaojinzi.module.base.support.SpPersistenceConverter
import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.module.base.support.spConverterPersistenceNonNull
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.app
import com.xiaojinzi.tally.base.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

enum class LanguageSettingDTO(
    val value: Int,
    val locale: Locale,
    val nameItem: StringItemDTO,
) {

    FollowSystem(
        value = 0,
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            app.resources.configuration.locales[0]
        } else {
            app.resources.configuration.locale
        },
        nameItem = R.string.res_str_follow_system.toStringItemDTO(),
    ),
    Chinese(
        value = 1,
        locale = Locale.CHINESE,
        nameItem = R.string.res_str_chinese.toStringItemDTO(),
    ),
    English(
        value = 2,
        locale = Locale.ENGLISH,
        nameItem = R.string.res_str_english.toStringItemDTO(),
    ),
    ;

    companion object {

        val spPersistenceConverter = object : SpPersistenceConverter<LanguageSettingDTO, Int> {

            override fun converte(value: LanguageSettingDTO): Int {
                return value.value
            }

            override fun parse(value: Int): LanguageSettingDTO {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    app.resources.configuration.locales[0]
                }
                return when (value) {
                    FollowSystem.value -> FollowSystem
                    Chinese.value -> Chinese
                    English.value -> English
                    else -> notSupportError()
                }
            }

        }

    }

}

interface SystemSettingService {

    /**
     * App 语言设置
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = true)
    val languageObservableDTO: MutableSharedStateFlow<LanguageSettingDTO>

}

@ServiceAnno(SystemSettingService::class)
class SystemSettingServiceImpl : SystemSettingService {

    override val languageObservableDTO =
        MutableSharedStateFlow(initValue = LanguageSettingDTO.FollowSystem)
            .spConverterPersistenceNonNull(
                key = "languageObservableDTO",
                def = LanguageSettingDTO.FollowSystem,
                spPersistenceConverter = LanguageSettingDTO.spPersistenceConverter,
            )

}