package com.carrental.modules.location.mapper;

import com.carrental.modules.location.dto.LocationDTO;
import com.carrental.modules.location.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between Location entity and LocationDTO.
 * 
 * Handles all transformations between entity and DTO, including
 * partial updates using @MappingTarget.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LocationMapper {

    /**
     * Convert Location entity to LocationDTO
     * 
     * @param location the entity to convert
     * @return the corresponding DTO
     */
    LocationDTO toDTO(Location location);

    /**
     * Convert LocationDTO to Location entity
     * 
     * @param locationDTO the DTO to convert
     * @return the corresponding entity
     */
    Location toEntity(LocationDTO locationDTO);

    /**
     * Update an existing Location entity with data from LocationDTO
     * 
     * Used for PATCH/PUT operations where we only update provided fields
     * 
     * @param locationDTO the DTO containing update data
     * @param location the entity to update (will be modified)
     */
    void updateEntityFromDTO(LocationDTO locationDTO, @MappingTarget Location location);
}