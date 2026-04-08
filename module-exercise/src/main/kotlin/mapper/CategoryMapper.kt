package dev.greben.memowave.mapper

import dev.greben.memowave.dto.CategoryRequest
import dev.greben.memowave.dto.CategoryResponse
import dev.greben.memowave.entities.Category
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface CategoryMapper {

    @Mapping(target = "pack", expression = "java(null)")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    fun fromDto(dto: CategoryRequest): Category

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    fun updateFromDto(@MappingTarget entity: Category, dto: CategoryRequest): Category

    fun toDto(entity: Category): CategoryResponse
}