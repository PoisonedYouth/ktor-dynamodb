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
import java.time.Instant

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
            val customerId = call.queryParameters["customerId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'customerId' parameter.")
            val orderDate = call.queryParameters["orderDate"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'orderDate' parameter.")
            val order = orderService.getOrder(customerId = CustomerId(customerId), orderDate = Instant.ofEpochMilli(orderDate.toLong()))
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
            val customerId = call.queryParameters["customerId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing 'customerId' parameter.")
            val orderDate = call.queryParameters["productId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing 'orderDate' parameter.")
            orderService.deleteOrder(customerId = CustomerId(customerId), orderDate = Instant.ofEpochMilli(orderDate.toLong()))
            call.respond(HttpStatusCode.Accepted)
        }

        get("/order/payment") {
            val paymentType = call.queryParameters["paymentType"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'paymentType' parameter.")
            val orderDate = call.queryParameters["orderDate"]?.toLong()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'orderDate' parameter.")
            call.respond(
                HttpStatusCode.OK, orderService.getAllOfPaymentType(
                    paymentType = PaymentType.valueOf(paymentType),
                    orderDate = Instant.ofEpochMilli(orderDate)
                )
            )
        }

        get("/order/product") {
            val productId = call.queryParameters["productId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'productId' parameter.")
            val from = call.queryParameters["fromOrderDate"]?.toLong()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'fromOrderDate' parameter.")
            val to = call.queryParameters["toOrderDate"]?.toLong()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'toOrderDate' parameter.")
            call.respond(
                HttpStatusCode.OK, orderService.getAllOrdersOfProduct(
                    productId = ProductId(productId),
                    from = Instant.ofEpochMilli(from), to = Instant.ofEpochMilli(to)
                )
            )
        }
    }
}
