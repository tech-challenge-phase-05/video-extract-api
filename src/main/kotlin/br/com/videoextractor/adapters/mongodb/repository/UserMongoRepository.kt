package br.com.videoextractor.adapters.mongodb.repository

import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserMongoRepository: MongoRepository<UserEntity, String>