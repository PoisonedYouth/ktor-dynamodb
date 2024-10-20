package com.poisonedyouth.user

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.time.Instant
import java.time.LocalDateTime

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

//        get("/user") {
//            val jobStatus = call.queryParameters["jobStatus"]
//                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'jobStatus' parameter.")
//            call.respond(HttpStatusCode.OK, userService.getAllByJobStatus(JobStatus.valueOf(jobStatus)))
//        }
//        get("/user") {
//            val name = call.queryParameters["name"]
//                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'name' parameter.")
//            call.respond(HttpStatusCode.OK, userService.getAllByNameStartingWith(name))
//        }

        get("/user") {
            val start = call.queryParameters["start"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'start' parameter.")
            val end = call.queryParameters["end"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing 'end' parameter.")
            call.respond(HttpStatusCode.OK, userService.getAllCreatedInRange(
                start = LocalDateTime.parse(start),
                end = LocalDateTime.parse(end)
            ))
        }
    }
}