package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.Assert
import com.xiaojinzi.tally.base.service.datasource.TallyAccountTypeDTO
import com.xiaojinzi.tally.base.service.datasource.TallyAccountTypeInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyAccountTypeService
import com.xiaojinzi.tally.datasource.data.toTallyAccountTypeDO
import com.xiaojinzi.tally.datasource.data.toTallyAccountTypeDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ServiceAnno(TallyAccountTypeService::class)
class TallyAccountTypeServiceImpl: TallyAccountTypeService {

    override val allAccountTypeObservableDTO: Flow<List<TallyAccountTypeDTO>> =
        dbTally
            .tallyAccountTypeDao()
            .subscribeAll()
            .map { list ->
                list.map { it.toTallyAccountTypeDTO() }
            }

    override suspend fun insert(target: TallyAccountTypeInsertDTO): String {
        return withContext(context = Dispatchers.IO) {
            target
                .toTallyAccountTypeDO()
                .apply {
                    dbTally
                        .tallyAccountTypeDao()
                        .insert(target = this)
                }
                .uid
        }
    }

    override suspend fun insertList(targetList: List<TallyAccountTypeInsertDTO>): List<String> {
        return withContext(context = Dispatchers.IO) {
            targetList
                .map { it.toTallyAccountTypeDO() }
                .apply {
                    dbTally
                        .tallyAccountTypeDao()
                        .insertList(targetList = this)
                }
                .map { it.uid }
        }
    }

    override suspend fun getAll(): List<TallyAccountTypeDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally
                .tallyAccountTypeDao()
                .getAll()
                .map { it.toTallyAccountTypeDTO() }
        }
    }

    override suspend fun getByUid(uid: String): TallyAccountTypeDTO? {
        return dbTally
            .tallyAccountTypeDao()
            .getByUid(uid = uid)
            ?.toTallyAccountTypeDTO()
    }

    override suspend fun update(target: TallyAccountTypeDTO) {
        val updateLines = dbTally
            .tallyAccountTypeDao()
            .update(target = target.toTallyAccountTypeDO())
        Assert.assertEquals(value1 = updateLines, value2 = 1)
    }

}