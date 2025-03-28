package br.com.videoextractor.controller

import br.com.videoextractor.controller.message.UpdateVideoTaskMessage
import br.com.videoextractor.core.service.UpdateTaskProcessService
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Service
import java.util.logging.Logger

@Service
class SqsListenerController(
    val updateTaskProcessService: UpdateTaskProcessService,
) {
    val logger = Logger.getLogger(this.javaClass.name)
    @SqsListener(value = ["\${video-extractor.queue.name}"])
    fun receiveMessage(updateMessage: UpdateVideoTaskMessage) {
        logger.info("Updating task process with the following message: $updateMessage")
        updateTaskProcessService.updateTaskProcess(updateMessage)
    }
}