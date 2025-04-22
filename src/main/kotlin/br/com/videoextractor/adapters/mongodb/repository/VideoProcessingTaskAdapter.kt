package br.com.videoextractor.adapters.mongodb.repository

import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import org.springframework.stereotype.Service

@Service
class VideoProcessingTaskAdapter(
    private val videoProcessingTaskRepository: VideoProcessingTaskRepository
): VideoProcessingTaskRepositoryPort {
    override fun findById(id:String): VideoProcessingTaskEntity? {
        return videoProcessingTaskRepository.findByOriginalVideo_Id(id)
    }
    override fun save(videoProcessingTaskEntity: VideoProcessingTaskEntity): VideoProcessingTaskEntity {
        return videoProcessingTaskRepository.save(videoProcessingTaskEntity)
    }
}