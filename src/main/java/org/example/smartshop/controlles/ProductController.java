package org.example.smartshop.controlles;

import lombok.RequiredArgsConstructor;
import org.example.smartshop.entities.DTO.product.ProductCreateDto;
import org.example.smartshop.entities.DTO.product.ProductResponseDto;
import org.example.smartshop.entities.DTO.product.ProductUpdateDto;
import org.example.smartshop.services.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final IProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateDto dto) {
        return ResponseEntity.ok(productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateDto dto
    ) {
        // Ensure the path variable id is used
        ProductUpdateDto request = new ProductUpdateDto(
                id,
                dto.name(),
                dto.unitPrice(),
                dto.stock(),
                dto.deleted()
        );

        return ResponseEntity.ok(productService.updateProduct(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}
