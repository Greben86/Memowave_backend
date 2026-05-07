package dev.greben.memowave.mapper

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.entities.Word
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface WordMapper {

    @Mapping(target = "category", expression = "java(null)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    fun fromDto(dto: WordRequest): Word

    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    fun updateFromDto(@MappingTarget entity: Word, dto: WordRequest): Word

    @Mapping(target = "categoryId", expression = "java(entity.getCategory().getId())")
    fun toDto(entity: Word): WordResponse
}