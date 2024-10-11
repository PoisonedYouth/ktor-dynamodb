package com.poisonedyouth.user

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.time.Instant

fun Application.configureRouting(userService: UserService) {
    routing {
        post("/user") {
            userService.addUser(call.receive())
            call.respond(HttpStatusCode.Created)
        }
        post("/user-list") {
            userService.addAllUsers(call.receive())
            call.respond(HttpStatusCode.Created)
        }

        get("/user/all") {
            call.respond(HttpStatusCode.OK, userService.getAllBatch())
        }
    }
}