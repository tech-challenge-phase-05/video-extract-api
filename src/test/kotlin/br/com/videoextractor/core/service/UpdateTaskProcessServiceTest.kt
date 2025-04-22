package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.controller.message.UpdateVideoTaskMessage
import br.com.videoextractor.core.service.exception.NotFoundException
import br.com.videoextractor.domain.VideoProcessStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UpdateTaskProcessServiceTest {

    @Mock
    private lateinit var videoProcessingTaskRepository: VideoProcessingTaskRepositoryPort

    @InjectMocks
    private lateinit var updateTaskProcessService: UpdateTaskProcessService

    val taskId = "1"
    val originalVideo = OriginalVideo("3", "http://example.com/video.mp4", "video.mp4")
    val processedFrame = ProcessedFrame("5", "http://example.com/processed.mp4", LocalDateTime.now(), "processed.mp4", 1, 5)
    val videoTask = VideoProcessingTaskEntity(taskId, originalVideo, processedFrame, VideoProcessStatus.PENDING)


    @Test
    fun `should update task status`() {
        val newStatus = VideoProcessStatus.FINISHED
        whenever(videoProcessingTaskRepository.findById(taskId)).thenReturn(videoTask)

        updateTaskProcessService.updateTaskProcess(
            UpdateVideoTaskMessage(
                taskId, "http://example.com/video.mp4", VideoProcessStatus.PROCESSING
            )
        )

        verify(videoProcessingTaskRepository).save(videoTask.copy(status = newStatus))
    }
    @Test
    fun `should keep attributes of videoTask`() {
        val old = VideoProcessingTaskEntity(taskId, originalVideo, processedFrame, VideoProcessStatus.PENDING)
        val newStatus = VideoProcessStatus.ERROR

        whenever(videoProcessingTaskRepository.findById(taskId)).thenReturn(old)

        updateTaskProcessService.updateTaskProcess(
            UpdateVideoTaskMessage(
                taskId, "http://example.com/newVideo.mp4", newStatus
            )
        )

        verify(videoProcessingTaskRepository).save(old.copy(status = newStatus))
    }

    @Test
    fun `should throw exception when task not found`() {
        whenever(videoProcessingTaskRepository.findById(taskId)).thenReturn(null)

        val result = assertThrows<NotFoundException> {
            updateTaskProcessService.updateTaskProcess(
                UpdateVideoTaskMessage(
                    taskId, "http://example.com/video.mp4", VideoProcessStatus.FINISHED
                )
            )
        }
        assertEquals(result.message, "VideoProcessingTask not found")
    }
}