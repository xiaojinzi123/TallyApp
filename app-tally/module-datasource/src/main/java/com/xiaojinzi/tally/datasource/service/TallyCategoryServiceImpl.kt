package com.xiaojinzi.tally.datasource.service

import com.xiaojinzi.component.anno.ServiceAnno
import com.xiaojinzi.module.base.support.flow.SharedStateFlow
import com.xiaojinzi.module.base.support.flow.sharedStateIn
import com.xiaojinzi.tally.base.service.datasource.*
import com.xiaojinzi.tally.datasource.data.*
import com.xiaojinzi.tally.datasource.db.dbTally
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * 记账类别相关的实现
 */
@DelicateCoroutinesApi
@ServiceAnno(TallyCategoryService::class)
class TallyCategoryServiceImpl : TallyCategoryService {

    override val categoryGroupCountObservable = MutableSharedFlow<Long>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    ).apply {
        dbTally
            .tallyCategoryGroupDao().subscribeCount()
            .onEach {
                this.emit(value = it)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(scope = GlobalScope)
    }

    override val categoryCountObservable = MutableSharedFlow<Long>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.SUSPEND
    ).apply {
        dbTally
            .tallyCategoryDao()
            .subscribeCount()
            .onEach {
                this.emit(value = it)
            }
            .flowOn(Dispatchers.IO)
            .launchIn(scope = GlobalScope)
    }

    override val categoryGroupListObservable: SharedStateFlow<List<TallyCategoryGroupDTO>> by lazy {
        dbTally
            .tallyCategoryGroupDao()
            .subscribeAll()
            .map { list ->
                list.map {
                    it.toTallyCategoryGroupDTO()
                }
            }
            .flowOn(context = Dispatchers.IO)
            .sharedStateIn(
                initValue = emptyList(),
                scope = GlobalScope
            )
    }

    override val categoryListObservable: SharedStateFlow<List<TallyCategoryDTO>> by lazy {
        dbTally
            .tallyCategoryDao()
            .subscribeAll()
            .map { list ->
                list.map {
                    it.toTallyCategoryDTO()
                }
            }
            .flowOn(context = Dispatchers.IO)
            .sharedStateIn(
                initValue = emptyList(),
                scope = GlobalScope
            )
    }

    override val groupWithCategoryListObservable: Flow<List<Pair<TallyCategoryGroupDTO, List<TallyCategoryDTO>>>> by lazy {
        combine(categoryGroupListObservable, categoryListObservable) { groupList, cateList ->
            // 根据 groupId 分组
            val map = cateList.groupBy { it.groupId }
            groupList
                .map { group ->
                    Pair(
                        first = group,
                        map[group.uid]?: emptyList()
                    )
                }
        }
    }

    override suspend fun insertTallyCategory(target: TallyCategoryInsertDTO): TallyCategoryDTO {
        val uid = target
            .toTallCategoryDO()
            .apply {
                dbTally
                    .tallyCategoryDao()
                    .insert(
                        value = this
                    )
            }
            .uid
        return dbTally
            .tallyCategoryDao()
            .getByUid(uid = uid)!!
            .toTallyCategoryDTO()
    }

    override suspend fun getTallyCategoryById(id: String): TallyCategoryDTO? {
        return dbTally
            .tallyCategoryDao()
            .getByUid(uid = id)
            ?.toTallyCategoryDTO()
    }

    override suspend fun insertTallyCategoryGroup(target: TallyCategoryGroupInsertDTO): TallyCategoryGroupDTO {
        val uid = target
            .toTallCategoryDO()
            .apply {
                dbTally
                    .tallyCategoryGroupDao()
                    .insert(value = this)
            }
            .uid
        return dbTally
            .tallyCategoryGroupDao().getByUid(uid = uid)!!
            .toTallyCategoryGroupDTO()
    }

    override suspend fun updateTallyCategoryGroup(target: TallyCategoryGroupDTO) {
        withContext(context = Dispatchers.IO) {
            dbTally
                .tallyCategoryGroupDao()
                .update(
                    value = target.toTallyCategoryGroupDO()
                )
        }
    }

    override suspend fun updateTallyCategory(target: TallyCategoryDTO) {
        dbTally
            .tallyCategoryDao()
            .update(target = target.toTallyCategoryDO())
    }

    override suspend fun getAllTallyCategoryGroups(): List<TallyCategoryGroupDTO> {
        val groups = dbTally
            .tallyCategoryGroupDao()
            .getAll()
            .map {
                it.toTallyCategoryGroupDTO()
            }
        return groups
    }

    override suspend fun getTallyCategoryGroupById(id: String): TallyCategoryGroupDTO? {
        return dbTally.tallyCategoryGroupDao()
            .getByUid(uid = id)
            ?.toTallyCategoryGroupDTO()
    }

    override suspend fun getTallyCategoriesByGroupId(groupId: String): List<TallyCategoryDTO> {
        return dbTally
            .tallyCategoryDao()
            .getByGroupId(groupId = groupId)
            .map { it.toTallyCategoryDTO() }
    }

    override suspend fun getAllTallyCategories(): List<TallyCategoryDTO> {
        return getAllTallyCategoryGroups()
            .map {
                getTallyCategoriesByGroupId(groupId = it.uid)
            }
            .flatten()
    }

    override suspend fun getTallyCategoryDetailById(id: String): TallyCategoryWithGroupDTO? {
        return dbTally
            .tallyCategoryDao()
            .getDetailByUid(uid = id)
            ?.toTallyCategoryWithGroupDTO()
    }

    override suspend fun getAllTallyCategoryDetails(): List<TallyCategoryWithGroupDTO> {
        return dbTally
            .tallyCategoryDao()
            .getAllWithGroup()
            .map {
                it.toTallyCategoryWithGroupDTO()
            }
    }

    override suspend fun deleteCateGroupById(uid: String) {
        dbTally
            .tallyCategoryGroupDao()
            .deleteById(uid = uid)
    }

    override suspend fun deleteCateById(uid: String) {
        dbTally
            .tallyCategoryDao()
            .deleteById(uid = uid)
    }

    override fun subscribeCategoriesByType(type: TallyCategoryGroupTypeDTO): Flow<List<TallyCategoryDTO>> {
        return dbTally
            .tallyCategoryDao()
            .subscribeCategoriesByGroupType(type = type.dbValue)
            .map { list ->
                list.map {
                    it.toTallyCategoryDTO()
                }
            }
    }

}