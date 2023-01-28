package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.support.ktx.LogSupport
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceViewDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceViewDetailDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceViewInsertDTO
import com.xiaojinzi.tally.base.service.datasource.TallyBillAutoSourceViewService
import com.xiaojinzi.tally.datasource.data.toTallyBillAutoSourceViewDO
import com.xiaojinzi.tally.datasource.data.toTallyBillAutoSourceViewDTO
import com.xiaojinzi.tally.datasource.db.dbTally
import com.xiaojinzi.lib.res.dto.AutoBillSourceViewType

@ServiceAnno(TallyBillAutoSourceViewService::class)
class TallyBillAutoSourceViewServiceImpl : TallyBillAutoSourceViewService {

    override suspend fun insert(target: TallyBillAutoSourceViewInsertDTO): TallyBillAutoSourceViewDTO {
        val dao = dbTally.tallyBillAutoSourceViewDao()
        val targetDO = target.toTallyBillAutoSourceViewDO()
        dao.insert(target = targetDO)
        return dao.getById(uid = targetDO.uid)!!.toTallyBillAutoSourceViewDTO()
    }

    override suspend fun insertList(targetList: List<TallyBillAutoSourceViewInsertDTO>): List<TallyBillAutoSourceViewDTO> {
        val dao = dbTally.tallyBillAutoSourceViewDao()
        return targetList
            .map { it.toTallyBillAutoSourceViewDO() }
            .toList()
            .apply {
                dao.insertList(targetList = this)
            }
            .map { dao.getById(uid = it.uid)!! }
            .map { it.toTallyBillAutoSourceViewDTO() }
    }

    override suspend fun update(target: TallyBillAutoSourceViewDTO) {
        dbTally
            .tallyBillAutoSourceViewDao()
            .update(target = target.toTallyBillAutoSourceViewDO())
            .apply {
                LogSupport.d(content = "update lines = $this")
            }
    }

    override suspend fun getById(id: String): TallyBillAutoSourceViewDTO? {
        return dbTally.tallyBillAutoSourceViewDao()
            .getById(uid = id)
            ?.toTallyBillAutoSourceViewDTO()
    }

    override suspend fun getDetailByType(type: AutoBillSourceViewType): TallyBillAutoSourceViewDetailDTO? {
        return dbTally.tallyBillAutoSourceViewDao()
            .getDetailById(sourceViewType = type.dbValue)
            ?.toTallyBillAutoSourceViewDTO()
    }

    override suspend fun getDetailById(id: String): TallyBillAutoSourceViewDetailDTO? {
        return dbTally.tallyBillAutoSourceViewDao()
            .getDetailById(uid = id)
            ?.toTallyBillAutoSourceViewDTO()
    }

    override suspend fun getAllDetail(): List<TallyBillAutoSourceViewDetailDTO> {
        return dbTally.tallyBillAutoSourceViewDao()
            .getAllDetail()
            .map { it.toTallyBillAutoSourceViewDTO() }
    }

}