package br.com.videoextractor.controller

import br.com.videoextractor.controller.message.UpdateVideoTaskMessage
import br.com.videoextractor.core.service.UpdateTaskProcessService
import br.com.videoextractor.domain.VideoProcessStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class SqsListenerControllerTest {

    @Mock
    private lateinit var updateTaskProcessService: UpdateTaskProcessService

    @InjectMocks
    private lateinit var sqsListenerController: SqsListenerController

    @Test
    fun `should process SQS message`() {
        val updateVideo = UpdateVideoTaskMessage("1","http://example.com/video.mp4", "video.mp4", VideoProcessStatus.FINISHED)
        sqsListenerController.receiveMessage(updateVideo)
        verify(updateTaskProcessService).updateTaskProcess(updateVideo)
    }

    @Test
    fun `should handle SQS message processing failure`() {
        val updateVideo = UpdateVideoTaskMessage("1","http://example.com/video.mp4", "video.mp4", VideoProcessStatus.FINISHED)
        doThrow(RuntimeException()).whenever(updateTaskProcessService).updateTaskProcess(updateVideo)
        assertFailsWith<RuntimeException> {
            sqsListenerController.receiveMessage(updateVideo)
        }

    }
}