package com.xiaojinzi.module.base.domain

import com.xiaojinzi.module.base.support.flow.MutableSharedStateFlow
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.annotation.HotObservable
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface CostUseCase : BaseUseCase {

    /**
     * 耗费的钱的字符串
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR)
    val costStrObservableDTO: SharedStateFlow<CostState>

    /**
     * 费用的格式是否正确
     */
    @HotObservable(HotObservable.Pattern.BEHAVIOR, isShared = false)
    val costIsCorrectFormatObservableDTO: Flow<Boolean>

    /**
     * 计算一个结果, 根据给的算式
     */
    suspend fun calculateResult(target: String): Float

    /**
     * cost 字符串的累加
     */
    fun costAppend(
        target: String,
        isRemoveDefaultInput: Boolean = false,
        isReset: Boolean = false,
    )

    fun appendNumber(value: Int)
    fun appendPoint()
    fun appendAddSymbol()
    fun appendMinusSymbol()

    /**
     * cost 字符串删除最后一个字符功能
     */
    fun costDeleteLast()

    /**
     * 反转 cost 的正负值
     */
    fun toggleCostValue()

}

class CostUseCaseImpl : BaseUseCaseImpl(), CostUseCase {

    /**
     * 耗费的钱的字符串
     */
    override val costStrObservableDTO =
        MutableSharedStateFlow<CostState>(initValue = CostEmptyState())

    override val costIsCorrectFormatObservableDTO = costStrObservableDTO
        .map {
            it.isCorrectFormat()
            // mathPattern.matcher(it).matches()
        }

    override suspend fun calculateResult(target: String): Float {
        return withContext(context = Dispatchers.IO) {
            if (target.isEmpty()) {
                return@withContext 0f
            }
            var index = target.indexOf('+')
            if (index != -1) {
                return@withContext calculateResult(
                    target = target.substring(startIndex = 0, endIndex = index)
                ) + calculateResult(
                    target = target.substring(startIndex = index + 1)
                )
            }
            index = target.lastIndexOf('-')
            if (index != -1) {
                return@withContext calculateResult(
                    target = target.substring(startIndex = 0, endIndex = index)
                ) - calculateResult(
                    target = target.substring(startIndex = index + 1)
                )
            }
            return@withContext target.toFloat()
        }
    }

    override fun costAppend(target: String, isRemoveDefaultInput: Boolean, isReset: Boolean) {
        var temp = if (isReset) {
            CostEmptyState()
        } else {
            costStrObservableDTO.value
        }
        target.forEach {
            temp = when (it) {
                '+' -> temp.appendAddFlag()
                '-' -> temp.appendMinusFlag()
                '.' -> temp.appendPointFlag()
                in ('0'..'9') -> temp.appendNumber(value = it.code - 48)
                else -> notSupportError()
            }
        }
        costStrObservableDTO.value = if (isRemoveDefaultInput) {
            CostInitState(
                initState = temp,
            )
        } else {
            temp
        }
    }

    override fun appendNumber(value: Int) {
        costStrObservableDTO.value = costStrObservableDTO.value.appendNumber(value = value)
    }

    override fun appendPoint() {
        costStrObservableDTO.value = costStrObservableDTO.value.appendPointFlag()
    }

    override fun appendAddSymbol() {
        costStrObservableDTO.value = costStrObservableDTO.value.appendAddFlag()
    }

    override fun appendMinusSymbol() {
        costStrObservableDTO.value = costStrObservableDTO.value.appendMinusFlag()
    }

    override fun costDeleteLast() {
        costStrObservableDTO.value = costStrObservableDTO.value.delete()
        /*val currValue = costStrObservableDTO.value
        if (currValue.isNotEmpty()) {
            costStrObservableDTO.value =
                currValue.substring(startIndex = 0, endIndex = currValue.lastIndex)
        }*/
    }

    override fun toggleCostValue() {
        scope.launch(ErrorIgnoreContext) {
            val costToggledStr =
                (-calculateResult(target = costStrObservableDTO.value.strValue)).toString()
            costStrObservableDTO.value = CostEmptyState()
            costAppend(target = costToggledStr)
        }
    }

}