package dev.greben.memowave.mapper

import dev.greben.memowave.dto.ProcessFileEvent
import dev.greben.memowave.dto.UploadFileEvent
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface FileEventMapper {

    @Mapping(target = "status", source = "status")
    fun toProcess(event: UploadFileEvent, status: String): ProcessFileEvent
}