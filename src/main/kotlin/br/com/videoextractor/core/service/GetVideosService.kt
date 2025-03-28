package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.core.service.exception.NotFoundException
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class GetVideosService(
    private val userRepositoryPort: UserRepositoryPort
) {
    val logger = Logger.getLogger(this.javaClass.name)
    fun getAllVideosByClient(clientId: String): List<VideoProcessingTaskEntity>{
        logger.info("Getting all videos from client $clientId")
        val user = userRepositoryPort.findById(clientId)?: throw NotFoundException("User: $clientId not found")
        logger.info("Returned user from database: $user")
        return user.allProcessingTasks
    }
}