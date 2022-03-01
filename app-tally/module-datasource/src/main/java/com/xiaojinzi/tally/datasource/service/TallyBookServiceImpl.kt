package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.datasource.TallyBookDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBookInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBookService
import com.xiaojinzi.tally.datasource.data.toTallyBookDO
import com.xiaojinzi.tally.datasource.data.toTallyBookDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@ServiceAnno(TallyBookService::class)
class TallyBookServiceImpl : TallyBookService {

    override val allBillBookListObservable: Flow<List<TallyBookDTO>>
        get() = dbTally.tallyBookDao().subscribeAll()
            .map { list ->
                list.map {
                    it.toTallyBookDTO()
                }
            }

    override val defaultBookObservable: Flow<TallyBookDTO>
        get() = dbTally.tallyBookDao().subscribeDefault()
            .map { it.toTallyBookDTO() }

    override suspend fun getAll(): List<TallyBookDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally
                .tallyBookDao()
                .getAll()
                .map { it.toTallyBookDTO() }
        }
    }

    override suspend fun getDefaultBook(): TallyBookDTO {
        return dbTally.tallyBookDao().getDefault()!!.toTallyBookDTO()
    }

    override suspend fun getBookById(uid: String): TallyBookDTO? {
        return dbTally.tallyBookDao().getByUid(uid = uid)?.toTallyBookDTO()
    }

    override suspend fun insertList(targetList: List<TallyBookInsertDTO>): List<TallyBookDTO> {
        return withContext(context = Dispatchers.IO) {
            val dbDao = dbTally.tallyBookDao()
            return@withContext targetList
                .map {
                    it.toTallyBookDO()
                }
                .apply {
                    dbDao.insertList(
                        targetList = this
                    )
                }
                .map {
                    dbDao.getByUid(uid = it.uid)!!.toTallyBookDTO()
                }
        }
    }

    override suspend fun update(target: TallyBookDTO) {
        val dbDao = dbTally.tallyBookDao()
        dbDao.update(target = target.toTallyBookDO())
    }

    override suspend fun deleteByUid(uid: String) {
        val dbDao = dbTally.tallyBookDao()
        dbDao.deleteByUid(uid = uid)
    }

}