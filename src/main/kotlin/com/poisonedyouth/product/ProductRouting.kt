package com.poisonedyouth.product

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing

fun Application.configureRouting(productService: ProductService) {
    routing {
        post("/product") {
            productService.addProduct(call.receive())
            call.respond(HttpStatusCode.Created)
        }
        put("/product") {
            call.respond(HttpStatusCode.OK, productService.updateProduct(call.receive()))
        }
        get("/product/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter.")
            val product = productService.getProduct(ProductId(id))
            if (product == null) {
                call.respond(HttpStatusCode.NotFound, "Product not found.")
            } else {
                call.respond(product)
            }
        }
        get("/product/all") {
            call.respond(HttpStatusCode.OK, productService.getAllProducts())
        }
        delete("/product/{id}") {
            val id = call.parameters["id"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter.")
            productService.deleteProduct(ProductId(id))
            call.respond(HttpStatusCode.Accepted)
        }

    }
}
