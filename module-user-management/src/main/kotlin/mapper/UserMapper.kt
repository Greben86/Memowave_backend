package dev.greben.memowave.mapper

import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.entities.User
import dev.greben.memowave.dto.UserResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {

    @Mapping(target = "userRole", constant = "ROLE_USER")
    @Mapping(target = "password", source = "encodedPassword")
    fun fromDto(dto: SignUpRequest, encodedPassword: String): User

    fun toDto(entity: User): UserResponse
}