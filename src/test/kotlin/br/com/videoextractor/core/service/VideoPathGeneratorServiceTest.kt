package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.S3.S3Adapter
import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.domain.VideoProcessStatus
import br.com.videoextractor.domain.VideoProcessingData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class VideoPathGeneratorServiceTest {

    @Mock
    private lateinit var s3Adapter: S3Adapter

    @Mock
    private lateinit var userManagerService: VideoUserManagerService

    @InjectMocks
    private lateinit var videoPathGeneratorService: VideoPathGeneratorService

    @Test
    fun `should generate path and create new video record`() {
        val clientId = "123"
        val presignedUrl = "http://example.com/presigned-url"
        val videoProcessingData = VideoProcessingData(presignedUrl, "bucket", "fileName")
        val videoUuid = "uuid generated"

        whenever(s3Adapter.generatePresignedUrl(any())).thenReturn(videoProcessingData)
        whenever(userManagerService.createNewVideoRecord(eq(clientId), any(), eq(videoProcessingData))).thenReturn(
            VideoProcessingTaskEntity(
                videoUuid,
                OriginalVideo(videoUuid, "http://example.com/presigned-url", "fileName"),
                ProcessedFrame(videoUuid, "http://example.com/presigned-url", LocalDateTime.now(), "fileName", 1, 5),
                VideoProcessStatus.PENDING
            )
        )

        val result = videoPathGeneratorService.generatePath(clientId)

        assertEquals(presignedUrl, result)
    }
}