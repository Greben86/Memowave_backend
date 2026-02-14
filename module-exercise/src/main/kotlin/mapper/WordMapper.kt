package dev.greben.memowave.mapper

import dev.greben.memowave.dto.WordRequest
import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.entities.Word
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface WordMapper {

    @Mapping(target = "category", expression = "java(null)")
    fun fromDto(dto: WordRequest): Word

    @Mapping(target = "category", expression = "java(entity.getCategory().getName())")
    fun toDto(entity: Word): WordResponse
}