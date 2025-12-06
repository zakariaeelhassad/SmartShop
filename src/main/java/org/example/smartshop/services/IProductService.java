package org.example.smartshop.services;

import org.example.smartshop.entities.DTO.product.ProductCreateDto;
import org.example.smartshop.entities.DTO.product.ProductResponseDto;
import org.example.smartshop.entities.DTO.product.ProductUpdateDto;

import java.util.List;

public interface IProductService {

    ProductResponseDto createProduct(ProductCreateDto dto);
    ProductResponseDto updateProduct(ProductUpdateDto dto);
    void deleteProduct(Long id);
    List<ProductResponseDto> getAllProducts();
    ProductResponseDto getProductById(Long id);
}
