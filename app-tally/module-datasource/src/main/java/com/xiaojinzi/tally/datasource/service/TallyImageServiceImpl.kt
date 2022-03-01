package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.datasource.TallyImageDTO
import com.xiaojinzi.tally.base.service.datasource.TallyImageInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyImageService
import com.xiaojinzi.tally.datasource.data.toTallyImageDO
import com.xiaojinzi.tally.datasource.data.toTallyImageDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ServiceAnno(TallyImageService::class)
class TallyImageServiceImpl : TallyImageService {

    override suspend fun insert(target: TallyImageInsertDTO): TallyImageDTO {
        val dao = dbTally.tallyImageDao()
        val targetDO = target.toTallyImageDO()
        dao.insert(target = targetDO)
        return dao.getById(uid = targetDO.uid)!!.toTallyImageDTO()
    }

    override suspend fun insertList(targetList: List<TallyImageInsertDTO>): List<TallyImageDTO> {
        val dao = dbTally.tallyImageDao()
        return targetList
            .map {
                it.toTallyImageDO()
            }
            .apply {
                dao.insertList(targetList = this)
            }
            .map { dao.getById(uid = it.uid)!!.toTallyImageDTO() }
    }

    override suspend fun deleteByKey1(key1: String) {
        dbTally
            .tallyImageDao()
            .deleteByKey1(key1 = key1)
    }

    override suspend fun getListByKey1(key1: String): List<TallyImageDTO> {
        return withContext(context = Dispatchers.IO) {
            dbTally
                .tallyImageDao()
                .getByKey1(key1 = key1)
                .map { it.toTallyImageDTO() }
        }
    }

}