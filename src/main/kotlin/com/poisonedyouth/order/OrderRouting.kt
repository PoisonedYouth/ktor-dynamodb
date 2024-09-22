package com.poisonedyouth.order

import com.poisonedyouth.product.ProductId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing

fun Application.configureRouting(orderService: OrderService) {
    routing {
        post("/order") {
            orderService.addOrder(call.receive())
            call.respond(HttpStatusCode.Created)
        }
        put("/order") {
            call.respond(HttpStatusCode.OK, orderService.updateOrder(call.receive()))
        }
        get("/order/") {
            val orderId = call.queryParameters["orderId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'orderId' parameter.")
            val productId = call.queryParameters["productId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'productId' parameter.")
            val order = orderService.getOrder(orderId = OrderId(orderId), productId = ProductId(productId))
            if (order == null) {
                call.respond(HttpStatusCode.NotFound, "Order not found.")
            } else {
                call.respond(order)
            }
        }
        get("/order/all") {
            call.respond(HttpStatusCode.OK, orderService.getAllOrders())
        }
        delete("/order") {
            val orderId = call.queryParameters["orderId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing 'orderId' parameter.")
            val productId = call.queryParameters["productId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing 'productId' parameter.")
            orderService.deleteOrder(orderId = OrderId(orderId), productId = ProductId(productId))
            call.respond(HttpStatusCode.Accepted)
        }
    }
}
