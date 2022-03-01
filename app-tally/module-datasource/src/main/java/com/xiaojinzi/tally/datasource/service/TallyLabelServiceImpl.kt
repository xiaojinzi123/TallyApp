package com.xiaojinzi.tally.datasource.service

import androidx.room.withTransaction
import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.datasource.TallyLabelDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyLabelService
import com.xiaojinzi.tally.base.support.tallyBillLabelService
import com.xiaojinzi.tally.base.view.toTallyLabelVO
import com.xiaojinzi.tally.datasource.data.toTallyLabelDO
import com.xiaojinzi.tally.datasource.data.toTallyLabelDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ServiceAnno(TallyLabelService::class)
class TallyLabelServiceImpl : TallyLabelService {

    override suspend fun getAll(): List<TallyLabelDTO> {
        return withContext(Dispatchers.IO) {
            dbTally.tallyLabelDao().getAll().map { it.toTallyLabelDTO() }
        }
    }

    override suspend fun getById(id: String): TallyLabelDTO? {
        return dbTally.tallyLabelDao()
            .getById(uid = id)
            ?.toTallyLabelDTO()
    }

    override suspend fun getByIds(ids: List<String>): List<TallyLabelDTO> {
        return withContext(Dispatchers.IO) {
            ids.map {
                dbTally
                    .tallyLabelDao()
                    .getById(uid = it)!!
                    .toTallyLabelDTO()
            }
        }
    }

    override suspend fun insert(target: TallyLabelInsertDTO): TallyLabelDTO {
        return insertList(targetList = listOf(target)).first()
    }

    override suspend fun insertList(targetList: List<TallyLabelInsertDTO>): List<TallyLabelDTO> {
        return withContext(context = Dispatchers.IO) {
            targetList
                .map { it.toTallyLabelDO() }
                .apply {
                    dbTally
                        .tallyLabelDao()
                        .insertList(
                            target = this
                        )
                }
                .map {
                    dbTally.tallyLabelDao().getById(uid = it.uid)!!.toTallyLabelDTO()
                }
        }
    }

    override suspend fun deleteByIds(ids: List<String>) {
        if (ids.isEmpty()) {
            return
        }
        dbTally.withTransaction {
            withContext(Dispatchers.IO) {
                // 先删除账单和标签的关联表
                val labelDOList = ids
                    .mapNotNull {
                        dbTally.tallyLabelDao().getById(uid = it)
                    }
                tallyBillLabelService.deleteByLabelIds(ids = labelDOList.map { it.uid!! })
                dbTally.tallyLabelDao().deleteList(target = labelDOList)
            }
        }
    }

    override suspend fun update(target: TallyLabelDTO) {
        dbTally.tallyLabelDao()
            .update(target = target.toTallyLabelDO())
    }

    override fun subscribeAll(): Flow<List<TallyLabelDTO>> {
        return dbTally.tallyLabelDao()
            .subscribeAll()
            .map { list ->
                list.map { it.toTallyLabelDTO() }
            }
    }

}