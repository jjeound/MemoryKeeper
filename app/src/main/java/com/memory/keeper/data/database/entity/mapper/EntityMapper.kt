package com.memory.keeper.data.database.entity.mapper

interface EntityMapper<Domain, Entity> {

    fun asEntity(domain: Domain, category: String?): Entity

    fun asDomain(entity: Entity): Domain
}