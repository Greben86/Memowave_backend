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

    @Mapping(target = "status", constant = "PROCESS")
    fun toProcess(event: UploadFileEvent): ProcessFileEvent

    @Mapping(target = "status", source = "status")
    fun changeProcess(event: ProcessFileEvent, status: String): ProcessFileEvent
}