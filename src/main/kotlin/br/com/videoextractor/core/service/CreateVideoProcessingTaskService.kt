package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.domain.VideoProcessStatus
import br.com.videoextractor.domain.VideoProcessingData
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Logger

@Service
class CreateVideoProcessingTaskService(
    private val userRepositoryPort: UserRepositoryPort,
    private val videoProcessingTaskPort: VideoProcessingTaskRepositoryPort
) {
    val logger = Logger.getLogger(this.javaClass.name)
    fun createNewVideoProcessingTask( user: UserEntity, videoUuid: String, videoProcessingData: VideoProcessingData): VideoProcessingTaskEntity{
        val videoProcessingTask =  VideoProcessingTaskEntity(
            id = UUID.randomUUID().toString(),
            originalVideo = OriginalVideo(
                id = videoUuid,
                url = videoProcessingData.s3Path,
                fileName = videoProcessingData.key
            ),
            status = VideoProcessStatus.PENDING,
            processedFrame = ProcessedFrame(startTime = 0, endTime = 5)
        )
        logger.info("Creating new register for VideoProcessingTask $videoProcessingTask")
        val newVideoProcessingTask = videoProcessingTaskPort.save(videoProcessingTask)
        logger.info("VideoProcessingTask saved successfully, VideoProcessingTask : $newVideoProcessingTask")
        user.allProcessingTasks = user.allProcessingTasks.plus(videoProcessingTask)
        val newUser = userRepositoryPort.saveUser(user)
        logger.info("User saved successfully, saved User: $newUser")
        return videoProcessingTask
    }
}