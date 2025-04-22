package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.adapters.s3.S3Adapter
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
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UpdateTaskProcessServiceTest {

    @Mock
    private lateinit var videoProcessingTaskRepository: VideoProcessingTaskRepositoryPort

    @Mock
    private lateinit var s3Adapter: S3Adapter

    @InjectMocks
    private lateinit var updateTaskProcessService: UpdateTaskProcessService

    private val taskId = "1"
    private val originalVideo = OriginalVideo("3", "http://example.com/video.mp4", "video.mp4")
    private val processedFrame = ProcessedFrame("5", null, null, null, 0, 0)

    @Test
    fun `should update task status and generate presigned URL when status is FINISHED`() {
        val videoTask = VideoProcessingTaskEntity(taskId, originalVideo, processedFrame, VideoProcessStatus.PROCESSING)

        whenever(videoProcessingTaskRepository.findById(taskId)).thenReturn(videoTask)
        whenever(s3Adapter.generateDownloadPresignedUrl(any())).thenReturn("https://presigned.url")

        val message = UpdateVideoTaskMessage(
            videoId = taskId,
            status = VideoProcessStatus.FINISHED,
            processedFileKey = "processed.mp4"
        )

        updateTaskProcessService.updateTaskProcess(message)

        // Assertions sobre mutabilidade
        assertEquals(VideoProcessStatus.FINISHED, videoTask.status)
        assertEquals("processed.mp4", videoTask.processedFrame.fileName)
        assertEquals("https://presigned.url", videoTask.processedFrame.url)

        verify(videoProcessingTaskRepository).save(videoTask)
    }

    @Test
    fun `should update only status when not FINISHED`() {
        val videoTask = VideoProcessingTaskEntity(taskId, originalVideo, processedFrame, VideoProcessStatus.PENDING)

        whenever(videoProcessingTaskRepository.findById(taskId)).thenReturn(videoTask)

        val newStatus = VideoProcessStatus.ERROR
        val message = UpdateVideoTaskMessage(
            videoId = taskId,
            status = newStatus,
            processedFileKey = null
        )

        updateTaskProcessService.updateTaskProcess(message)

        assertEquals(newStatus, videoTask.status)
        assertEquals(null, videoTask.processedFrame.url)

        verify(videoProcessingTaskRepository).save(videoTask)
    }

    @Test
    fun `should throw exception when task not found`() {
        whenever(videoProcessingTaskRepository.findById(taskId)).thenReturn(null)

        val message = UpdateVideoTaskMessage(
            videoId = taskId,
            status = VideoProcessStatus.FINISHED,
            processedFileKey = "processed.mp4"
        )

        val result = assertThrows<NotFoundException> {
            updateTaskProcessService.updateTaskProcess(message)
        }

        assertEquals("VideoProcessingTask not found", result.message)
    }
}
