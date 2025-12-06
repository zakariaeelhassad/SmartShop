package org.example.smartshop.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.product.ProductCreateDto;
import org.example.smartshop.entities.DTO.product.ProductResponseDto;
import org.example.smartshop.entities.DTO.product.ProductUpdateDto;
import org.example.smartshop.entities.enums.UserRole;
import org.example.smartshop.entities.models.Product;
import org.example.smartshop.mappers.ProductMapper;
import org.example.smartshop.repository.ProductRepository;
import org.example.smartshop.services.IProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AuthService authService;

    @Override
    public ProductResponseDto createProduct(ProductCreateDto dto) {
        authService.requireAdminRole();

        Product product = productMapper.toEntity(dto);
        product.setDeleted(false);

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    public ProductResponseDto updateProduct(ProductUpdateDto dto) {
        authService.requireAdminRole();

        Product existing = productRepository.findById(dto.id())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productMapper.updateEntityFromDto(dto, existing);

        Product updated = productRepository.save(existing);
        return productMapper.toDto(updated);
    }

    @Override
    public void deleteProduct(Long id) {
        authService.requireAdminRole();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return productMapper.toDto(product);
    }

}
