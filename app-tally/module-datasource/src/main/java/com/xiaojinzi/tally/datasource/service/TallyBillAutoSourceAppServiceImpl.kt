package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceAppDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceAppDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceAppInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceAppService
import com.xiaojinzi.tally.datasource.data.toTallyBillAutoSourceAppDO
import com.xiaojinzi.tally.datasource.data.toTallyBillAutoSourceAppDTO
import com.xiaojinzi.tally.datasource.data.toTallyBillAutoSourceAppDetailDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import com.xiaojinzi.lib.res.dto.AutoBillSourceAppType

@ServiceAnno(TallyBillAutoSourceAppService::class)
class TallyBillAutoSourceAppServiceImpl : TallyBillAutoSourceAppService {

    override suspend fun insert(target: TallyBillAutoSourceAppInsertDTO): TallyBillAutoSourceAppDTO {
        val dao = dbTally.tallyBillAutoSourceAppDao()
        val targetDO = target.toTallyBillAutoSourceAppDO()
        dao.insert(target = targetDO)
        return dao.getById(uid = targetDO.uid)!!.toTallyBillAutoSourceAppDTO()
    }

    override suspend fun insertAndReturnDetail(target: TallyBillAutoSourceAppInsertDTO): TallyBillAutoSourceAppDetailDTO {
        val dao = dbTally.tallyBillAutoSourceAppDao()
        val targetDO = target.toTallyBillAutoSourceAppDO()
        dao.insert(target = targetDO)
        return dao.getDetailById(uid = targetDO.uid)!!.toTallyBillAutoSourceAppDetailDTO()
    }

    override suspend fun insertList(targetList: List<TallyBillAutoSourceAppInsertDTO>): List<TallyBillAutoSourceAppDTO> {
        val dao = dbTally.tallyBillAutoSourceAppDao()
        return targetList
            .map {
                it.toTallyBillAutoSourceAppDO()
            }
            .also {
                dao.insertList(
                    targetList = it
                )
            }
            .map { dao.getById(uid = it.uid)!!.toTallyBillAutoSourceAppDTO() }
    }

    override suspend fun update(target: TallyBillAutoSourceAppDTO) {
        dbTally.tallyBillAutoSourceAppDao()
            .update(target = target.toTallyBillAutoSourceAppDO())
    }

    override suspend fun getById(id: String): TallyBillAutoSourceAppDTO? {
        return dbTally.tallyBillAutoSourceAppDao()
            .getById(uid = id)
            ?.toTallyBillAutoSourceAppDTO()
    }

    override suspend fun getDetailByType(sourceType: AutoBillSourceAppType): TallyBillAutoSourceAppDetailDTO? {
        return dbTally.tallyBillAutoSourceAppDao()
            .getDetailBySourceType(sourceAppType = sourceType.dbValue)
            ?.toTallyBillAutoSourceAppDetailDTO()
    }

    override suspend fun getAll(): List<TallyBillAutoSourceAppDTO> {
        return dbTally.tallyBillAutoSourceAppDao()
            .getAll()
            .map { it.toTallyBillAutoSourceAppDTO() }
    }

    override suspend fun getAllDetail(): List<TallyBillAutoSourceAppDetailDTO> {
        return dbTally.tallyBillAutoSourceAppDao()
            .getAllDetail()
            .map { it.toTallyBillAutoSourceAppDetailDTO() }
    }

}