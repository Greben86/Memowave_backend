package dev.greben.memowave.mapper

import dev.greben.memowave.dto.WordResponse
import dev.greben.memowave.entities.Word
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface WordMapper {

    fun fromDto(dto: WordResponse): Word

    fun toDto(entity: Word): WordResponse
}