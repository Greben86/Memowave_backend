package dev.greben.memowave.mapper

import dev.greben.memowave.model.UserRequest
import dev.greben.memowave.entities.User
import dev.greben.memowave.model.UserResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface UserMapper {

    @Mapping(target = "userRole", constant = "ROLE_USER")
    fun dtoToEntity(dto: UserRequest): User

    fun entityToDto(entity: User): UserResponse
}

object MappersFactory {
    @JvmStatic
    fun userMapper(): UserMapper = Mappers.getMapper(UserMapper::class.java)
}