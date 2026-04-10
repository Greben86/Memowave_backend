package dev.greben.memowave.mapper

import dev.greben.memowave.dto.SignUpRequest
import dev.greben.memowave.entities.User
import dev.greben.memowave.dto.UserResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {

    @Mapping(target = "userRole", constant = "ROLE_USER")
    @Mapping(target = "passwordHash", source = "encodedPassword")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    fun fromDto(dto: SignUpRequest, encodedPassword: String): User

    fun toDto(entity: User): UserResponse

    /**
     * Обновление сущности User на основе данных из UserResponse
     */
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    fun updateFromDto(@MappingTarget entity: User, dto: UserResponse): User
}