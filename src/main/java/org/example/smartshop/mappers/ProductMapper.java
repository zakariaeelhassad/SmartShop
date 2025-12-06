package org.example.smartshop.mappers;

import org.example.smartshop.entities.DTO.product.ProductCreateDto;
import org.example.smartshop.entities.DTO.product.ProductResponseDto;
import org.example.smartshop.entities.DTO.product.ProductUpdateDto;
import org.example.smartshop.entities.models.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponseDto toDto(Product product);

    Product toEntity(ProductCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget Product product);
}