package br.com.videoextractor.adapters.mongodb.repository.entities

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document("userDocument")
data class UserEntity(
    val id:String,
    @DBRef
    var allProcessingTasks: List<VideoProcessingTaskEntity>
)