package dev.greben.memowave.mapper

import dev.greben.memowave.dto.EventFileProcess
import dev.greben.memowave.dto.EventFileUpload
import dev.greben.memowave.dto.FileProcessStatus
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
    fun toProcess(event: EventFileUpload, status: FileProcessStatus): EventFileProcess
}