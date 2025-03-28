package br.com.videoextractor.adapters.mongodb.repository

import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort

import org.springframework.stereotype.Service

@Service
class UserMongoAdapter(
    val userMongoRepository: UserMongoRepository
): UserRepositoryPort {
    override fun findById(clientId: String): UserEntity? {
        return userMongoRepository.findById(clientId).orElse(null)
    }
    override fun createUser(clientId: String): UserEntity {
        val user = userMongoRepository.save(UserEntity(clientId, emptyList()))
        return user
    }

    override fun saveUser(userEntity: UserEntity): UserEntity {
        val user = userMongoRepository.save(userEntity)
        return user
    }
}