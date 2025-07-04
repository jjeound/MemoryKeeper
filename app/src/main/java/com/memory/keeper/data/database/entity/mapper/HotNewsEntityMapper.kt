package com.memory.keeper.data.database.entity.mapper

import com.memory.keeper.data.database.entity.HotNewsEntity
import com.memory.keeper.data.model.News

object HotNewsEntityMapper : EntityMapper<News, HotNewsEntity> {

    override fun asEntity(domain: News, category: String?): HotNewsEntity {
        return HotNewsEntity(
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

    override fun asDomain(entity: HotNewsEntity): News {
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

fun News.asHotEntity(category: String? = null): HotNewsEntity {
    return HotNewsEntityMapper.asEntity(this, category)
}

fun HotNewsEntity.asHotDomain(): News {
    return HotNewsEntityMapper.asDomain(this)
}