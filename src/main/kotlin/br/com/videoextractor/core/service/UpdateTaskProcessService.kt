package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.controller.message.UpdateVideoTaskMessage
import br.com.videoextractor.domain.VideoProcessStatus
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.logging.Logger

@Service
class UpdateTaskProcessService(
    private val videoProcessingTaskRepositoryPort: VideoProcessingTaskRepositoryPort
) {
    val logger = Logger.getLogger(this.javaClass.name)
    fun updateTaskProcess(updateVideoTaskMessage: UpdateVideoTaskMessage) {
        logger.info("Trying to find video: ${updateVideoTaskMessage.videoId}")
        val videoProcessingTask  = videoProcessingTaskRepositoryPort.findById(updateVideoTaskMessage.videoId)
            ?: throw Exception("VideoProcessingTask not found")
        logger.info("Video found! Video: $videoProcessingTask")
        if(updateVideoTaskMessage.status == VideoProcessStatus.FINISHED) {
            videoProcessingTask.processedFrame.id = UUID.randomUUID().toString()
            videoProcessingTask.processedFrame.url = updateVideoTaskMessage.processedVideoUrl
            videoProcessingTask.processedFrame.fileName = updateVideoTaskMessage.fileName
        }
        videoProcessingTask.status = VideoProcessStatus.FINISHED
        logger.info("Saving updated video Status: ${videoProcessingTask}")
        videoProcessingTaskRepositoryPort.save(videoProcessingTask)
        logger.info("Successfully updated video status")
    }
}