package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    public ProductResponse createProduct(ProductRequest request){
        Product product = new Product();
        updateProductFromRequest(product, request);
        Product savedProduct =  productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id).map(existingProduct -> {
            updateProductFromRequest(existingProduct, request);
            Product savedProduct = productRepository.save(existingProduct);
            return mapToProductResponse(savedProduct);
        });
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setActive(false);
            productRepository.save(product);
            return true;
        }).orElse(false);
    }

    private ProductResponse mapToProductResponse(Product savedProduct) {
        ProductResponse response = new ProductResponse();
        response.setId(savedProduct.getId());
        response.setName(savedProduct.getName());
        response.setDescription(savedProduct.getDescription());
        response.setPrice(savedProduct.getPrice());
        response.setCategory(savedProduct.getCategory());
        response.setStockQuantity(savedProduct.getStockQuantity());
        response.setImgUrl(savedProduct.getImgUrl());
        response.setActive(savedProduct.getActive());
        return response;
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(request.getCategory());
        product.setStockQuantity(request.getStockQuantity());
        product.setImgUrl(request.getImgUrl());
    }

}
