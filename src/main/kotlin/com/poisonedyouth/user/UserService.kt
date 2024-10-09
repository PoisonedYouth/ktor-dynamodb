package com.poisonedyouth.user

class UserService(
    private val userRepository: UserRepository
) {

    suspend fun addUser(user: User) {
        val existingUser = userRepository.findById(user.userId.value)
        if (existingUser != null) {
            error("User with userId '${user.userId}' already exists.")
        }
        userRepository.add(user)
    }

    suspend fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

}