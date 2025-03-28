package br.com.videoextractor.adapters.mongodb.repository

import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VideoProcessingTaskRepository: MongoRepository<VideoProcessingTaskEntity, String>