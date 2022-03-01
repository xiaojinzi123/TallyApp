package com.xiaojinzi.tally.datasource.service

import androidx.room.withTransaction
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.notSupportError
import com.xiaojinzi.support.ktx.AppScope
import com.xiaojinzi.support.ktx.ErrorIgnoreContext
import com.xiaojinzi.support.util.LogSupport
import com.xiaojinzi.tally.base.service.datasource.TallyAccountDTO
import com.xiaojinzi.tally.base.service.datasource.TallyAccountInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyAccountService
import com.xiaojinzi.tally.base.service.datasource.TallyAccountWithTypeDTO
import com.xiaojinzi.tally.base.support.TallyLogKeyword
import com.xiaojinzi.tally.base.support.tallyAccountService
import com.xiaojinzi.tally.base.support.tallyBillService
import com.xiaojinzi.tally.base.support.tallyService
import com.xiaojinzi.tally.datasource.data.toTallyAccountDO
import com.xiaojinzi.tally.datasource.data.toTallyAccountDTO
import com.xiaojinzi.tally.datasource.data.toTallyAccountWithTypeDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@DelicateCoroutinesApi
@ServiceAnno(TallyAccountService::class, autoInit = true)
class TallyAccountServiceImpl : TallyAccountService {

    override val defaultAccountObservable: Flow<TallyAccountDTO> = dbTally
        .tallyAccountDao()
        .subscribeDefault()
        .map { it.first().toTallyAccountDTO() }

    override val allAccount: Flow<List<TallyAccountDTO>> by lazy {
        dbTally.tallyAccountDao()
            .subscribeAll()
            .map { list ->
                list.map {
                    it.toTallyAccountDTO()
                }
            }
    }

    override val allAccountWithType: Flow<List<TallyAccountWithTypeDTO>> by lazy {
        dbTally.tallyAccountDao()
            .subscribeAllWithType()
            .map { list ->
                list.map {
                    it.toTallyAccountWithTypeDTO()
                }
            }
    }

    override suspend fun getAll(): List<TallyAccountDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyAccountDao()
                .getAll()
                .map {
                    it.toTallyAccountDTO()
                }
        }
    }

    override suspend fun getDefaultAccount(): TallyAccountDTO {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyAccountDao().getDefault().first().toTallyAccountDTO()
        }
    }


    override suspend fun getByUid(uid: String): TallyAccountDTO? {
        return withContext(context = Dispatchers.IO) {
            dbTally.tallyAccountDao()
                .getById(uid = uid)
                ?.toTallyAccountDTO()
        }
    }

    override suspend fun deleteByUid(uid: String) {
        val dbDao = dbTally.tallyAccountDao()
        val dto = dbDao.getById(uid = uid) ?: return
        if (dto.isDefault) {
            notSupportError("Default account can't be deleted")
        } else {
            dbDao.delete(target = dto)
        }
    }

    override suspend fun update(target: TallyAccountDTO) {
        val updateLines = dbTally
            .tallyAccountDao()
            .update(target = target.toTallyAccountDO())
        LogSupport.d(
            content = "账户更新成功的行数: $updateLines",
            keywords = arrayOf(TallyLogKeyword.TALLY_DATABASE, TallyLogKeyword.TALLY_ACCOUNT)
        )
    }

    override suspend fun insert(target: TallyAccountInsertDTO): TallyAccountDTO {
        val uid = target
            .toTallyAccountDO()
            .apply {
                dbTally
                    .tallyAccountDao()
                    .insert(
                        target = this
                    )
            }
            .uid
        return dbTally
            .tallyAccountDao()
            .getById(uid = uid)!!
            .toTallyAccountDTO()
    }

    override suspend fun insertList(targetList: List<TallyAccountInsertDTO>): List<TallyAccountDTO> {
        return withContext(context = Dispatchers.IO) {
            val dao = dbTally.tallyAccountDao()
            targetList
                .map { it.toTallyAccountDO() }
                .apply {
                    dao.insertList(targetList = this)
                }
                .map {
                    dao.getById(uid = it.uid)!!
                }
                .map { it.toTallyAccountDTO() }
        }
    }

    override suspend fun calculateCost(uid: String): Long {
        return tallyBillService.getAccountSpendingCost(accountId = uid) +
                tallyBillService.getAccountIncomeCost(accountId = uid)
    }


    override suspend fun setDefault(targetAccountId: String) {
        val dao = dbTally.tallyAccountDao()
        dbTally.withTransaction {
            dao.getById(uid = targetAccountId)?.let { targetAccount ->
                // 取消之前的默认账户
                dao
                    .getDefault()
                    .map {
                        it.copy(isDefault = false)
                    }
                    .forEach {
                        dao.update(target = it)
                    }
                // 更新为默认的
                dao.update(target = targetAccount.copy(isDefault = true))
            }
        }
    }

    override fun calculateBalanceAsync() {
        AppScope.launch(ErrorIgnoreContext) {
            getAll()
                .map { it.uid }
                .forEach { accountId ->
                    try {
                        val accountDTO = getByUid(uid = accountId) ?: return@launch
                        val cost = calculateCost(uid = accountDTO.uid)
                        LogSupport.d(
                            content = "账户:${accountDTO.uid} 的消费计算结果为：$cost",
                            TallyLogKeyword.TALLY_ACCOUNT
                        )
                        tallyAccountService.update(
                            target = accountDTO.copy(
                                balance = accountDTO.initialBalance + cost
                            )
                        )
                        LogSupport.d(
                            content = "账户:${accountId} 余额更新成功",
                            TallyLogKeyword.TALLY_ACCOUNT
                        )
                    } catch (_: Exception) {
                        LogSupport.d(
                            content = "账户:${accountId} 余额更新失败",
                            TallyLogKeyword.TALLY_ACCOUNT
                        )
                        // empty
                    }
                }

        }
    }

    init {
        tallyService.dataBaseChangedExceptAccountTableObservable
            .onEach {
                LogSupport.d(
                    content = "准备计算每个账户的余额",
                    TallyLogKeyword.TALLY_ACCOUNT, TallyLogKeyword.TALLY_DATABASE
                )
                calculateBalanceAsync()
            }
            .launchIn(scope = GlobalScope)

    }

}