package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.domain.VideoProcessingData
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class VideoUserManagerService(
    val userRepositoryPort: UserRepositoryPort,
    val createVideoProcessingTaskService: CreateVideoProcessingTaskService
) {
    val logger = Logger.getLogger(this.javaClass.name)
    fun createNewVideoRecord(idClient: String , videoUuid: String, videoProcessingData: VideoProcessingData): VideoProcessingTaskEntity {
        logger.info("Creating new video record for client $idClient with videoId: $videoUuid")
        val user = userRepositoryPort.findById(idClient)?:userRepositoryPort.createUser(idClient)
        logger.info("Usuário retornado : $user")
        return createVideoProcessingTaskService.createNewVideoProcessingTask(user, videoUuid, videoProcessingData)
    }
}
