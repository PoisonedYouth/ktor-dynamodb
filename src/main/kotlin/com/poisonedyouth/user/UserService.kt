package com.poisonedyouth.user

import java.time.LocalDateTime

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun addUser(user: User) {
        val existingUser = userRepository.findById(user.userId)
        if (existingUser != null) {
            error("User with userId '${user.userId}' already exists.")
        }
        userRepository.add(user)
    }

    suspend fun addAllUsers(users: List<User>) {
        for (user in users) {
            val existingUser = userRepository.findById(user.userId)
            if (existingUser != null) {
                error("User with userId '${user.userId}' already exists.")
            }
        }
        userRepository.batchWrite(users)
    }

    suspend fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    suspend fun getAllBatch(): List<User> {
        return userRepository.batchGet(
            (11111..11131).map { UserId(it) }
        )
    }

    suspend fun getAllByJobStatus(jobStatus: JobStatus): List<User> {
        return userRepository.findByJobStatus(jobStatus)
    }

    suspend fun getAllByNameStartingWith(namePrefix: String): List<User> {
        return userRepository.findByNameStartingWith(namePrefix)
    }

    suspend fun getAllCreatedInRange(start: LocalDateTime, end: LocalDateTime): List<User> {
        require(start.isBefore(end)) {
            "start '$start' must be before end '$end'."
        }
        return userRepository.findAllCreatedInRange(start, end)
    }

    suspend fun getAllExpired(): List<User> {
        return userRepository.findAllExpired()
    }
}