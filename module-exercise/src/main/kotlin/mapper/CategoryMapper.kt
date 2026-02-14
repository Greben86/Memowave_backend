package dev.greben.memowave.mapper

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.entities.Category
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface CategoryMapper {

    fun fromDto(dto: CategoryRequest): Category

    fun toDto(entity: Category): CategoryResponse
}