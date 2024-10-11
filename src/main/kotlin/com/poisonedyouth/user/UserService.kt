package com.poisonedyouth.user

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

}