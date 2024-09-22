package com.poisonedyouth.product

class ProductService(
    private val productRepository: ProductRepository
) {

    suspend fun addProduct(product: Product) {
        val existingProduct = productRepository.findById(product.productId.value)
        if (existingProduct != null) {
            error("Product with id ${product.productId} already exists.")
        }
        productRepository.add(product)
    }

    suspend fun updateProduct(product: Product) {
        productRepository.findById(product.productId.value)
            ?: error("Product with id ${product.productId} not found.")
        productRepository.update(product)
    }

    suspend fun getProduct(productId: ProductId): Product? {
        return productRepository.findById(productId = productId.value)
    }

    suspend fun deleteProduct(productId: ProductId) {
        productRepository.deleteById(productId.value)
    }

    suspend fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }
}