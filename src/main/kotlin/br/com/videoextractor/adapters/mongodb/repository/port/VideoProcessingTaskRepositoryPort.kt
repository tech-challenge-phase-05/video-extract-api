package br.com.videoextractor.adapters.mongodb.repository.port

import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity

interface VideoProcessingTaskRepositoryPort {
    fun findById(id: String): VideoProcessingTaskEntity?
    fun save(videoProcessingTaskEntity: VideoProcessingTaskEntity): VideoProcessingTaskEntity
}