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
        productRepository.updateProduct(product)
    }

    suspend fun getProduct(productId: String): Product? {
        return productRepository.findById(productId)
    }

    suspend fun deleteProduct(productId: String) {
        productRepository.deleteById(productId)
    }

    suspend fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }
}