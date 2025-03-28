package br.com.videoextractor.adapters.mongodb.repository.port

import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity

interface UserRepositoryPort {
    fun findById(clientId:String): UserEntity?
    fun createUser(clientId:String): UserEntity
    fun saveUser(userEntity: UserEntity): UserEntity
}