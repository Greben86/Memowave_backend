package dev.greben.memowave.mapper

import dev.greben.memowave.dto.SessionResponse
import dev.greben.memowave.entities.Session
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface SessionMapper {

    @Mapping(target = "sessionId", source = "id")
    fun toDto(entity: Session): SessionResponse

}