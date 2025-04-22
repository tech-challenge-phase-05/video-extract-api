package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.adapters.s3.S3Adapter
import br.com.videoextractor.core.service.exception.NotFoundException
import br.com.videoextractor.domain.VideoProcessStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.logging.Logger

@Service
class GetVideosService(
    private val userRepositoryPort: UserRepositoryPort,
    private val s3Adapter: S3Adapter,
    private val videoProcessingTaskRepositoryPort: VideoProcessingTaskRepositoryPort
    ) {
    val logger = Logger.getLogger(this.javaClass.name)
    fun getAllVideosByClient(clientId: String): List<VideoProcessingTaskEntity>{
        logger.info("Getting all videos from client $clientId")

        val user = userRepositoryPort.findById(clientId)
            ?: throw NotFoundException("User: $clientId not found")

        val now = LocalDateTime.now()
        val expirationLimit = Duration.ofHours(12)

        val updatedTasks = user.allProcessingTasks.map { task ->
            val processedFrame = task.processedFrame

            if (
                task.status == VideoProcessStatus.FINISHED &&
                processedFrame.url != null &&
                processedFrame.lastUpdatedUrlDt != null
            ) {
                val timeSinceUpdate = Duration.between(processedFrame.lastUpdatedUrlDt, now)
                if (timeSinceUpdate > expirationLimit) {
                    logger.info("Presigned URL expired for video ${task.originalVideo.id}, regenerating...")

                    val newUrl = s3Adapter.generateDownloadPresignedUrl(processedFrame.fileName ?: "")
                    logger.info("Presigned URL generated again for video ${task.originalVideo.id}")
                    processedFrame.url = newUrl
                    processedFrame.lastUpdatedUrlDt = now

                    videoProcessingTaskRepositoryPort.save(task)
                }
            }

            task
        }

        return updatedTasks
    }
}