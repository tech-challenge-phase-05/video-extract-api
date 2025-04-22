package br.com.videoextractor.controller


import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.core.service.GetVideosService
import br.com.videoextractor.core.service.VideoPathGeneratorServiceTest
import br.com.videoextractor.controller.response.PresignedUrl
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.core.service.VideoPathGeneratorService
import br.com.videoextractor.domain.VideoProcessStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class VideoExtractorControllerTest {

    @Mock
    private lateinit var videoPathGeneratorService: VideoPathGeneratorService

    @Mock
    private lateinit var getVideosService: GetVideosService

    @InjectMocks
    private lateinit var videoExtractorController: VideoExtractorController

    @Test
    fun `should create video path`() {
        val clientId = "123"
        val presignedUrl = "http://example.com/upload"
       whenever(videoPathGeneratorService.generatePath(clientId)).thenReturn(presignedUrl)
        val response = videoExtractorController.createVideoPath(clientId)
        assertEquals(response, ResponseEntity.ok(PresignedUrl(presignedUrl)))

    }

    @Test
    fun `should get all videos by client`() {
        val clientId = "123"
        val originalVideo = OriginalVideo("3", "http://example.com/video.mp4", "video.mp4")
        val originalVideo2 = OriginalVideo("4", "http://example.com/video.mp4", "video.mp4")
        val processedFrame = ProcessedFrame("5","http://example.com/processed.mp4", LocalDateTime.now(), "processed.mp4", 1, 5)
        val processedFrame2 = ProcessedFrame("5","http://example.com/processed.mp4", LocalDateTime.now(), "processed.mp4", 1, 5)
        val videoList = listOf(
            VideoProcessingTaskEntity("1", originalVideo, processedFrame, VideoProcessStatus.PENDING),
            VideoProcessingTaskEntity("2", originalVideo2, processedFrame2, VideoProcessStatus.FINISHED)
        )
        whenever(getVideosService.getAllVideosByClient(clientId)).thenReturn(videoList)
        val response = videoExtractorController.getAllVideosByClient(clientId)
        assertEquals(response, ResponseEntity.ok(videoList))
    }
}