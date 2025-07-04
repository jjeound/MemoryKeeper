package com.memory.keeper.data.database.entity.mapper

import com.memory.keeper.data.database.entity.LatestNewsEntity
import com.memory.keeper.data.model.News

object LatestNewsEntityMapper : EntityMapper<News, LatestNewsEntity> {

    override fun asEntity(domain: News, category: String?): LatestNewsEntity {
        return LatestNewsEntity(
            id = domain.id,
            title = domain.title,
            thumbnail = domain.thumbnail,
            left = domain.left,
            right = domain.right,
            center = domain.center,
            page = domain.page,
            category = category,
            totalPages = domain.totalPage
        )
    }

    override fun asDomain(entity: LatestNewsEntity): News {
        return News(
            id = entity.id,
            title = entity.title,
            thumbnail = entity.thumbnail,
            left = entity.left,
            right = entity.right,
            center = entity.center,
            page = entity.page,
            totalPage = entity.totalPages
        )
    }
}

fun News.asLatestEntity(category: String? = null): LatestNewsEntity {
    return LatestNewsEntityMapper.asEntity(this, category)
}

fun LatestNewsEntity.asLatestDomain(): News {
    return LatestNewsEntityMapper.asDomain(this)
}