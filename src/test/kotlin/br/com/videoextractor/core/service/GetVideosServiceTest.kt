import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.adapters.s3.S3Adapter
import br.com.videoextractor.core.service.GetVideosService
import br.com.videoextractor.core.service.exception.NotFoundException
import br.com.videoextractor.domain.VideoProcessStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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
class GetVideosServiceTest {

    @Mock
    private lateinit var userRepositoryPort: UserRepositoryPort

    @Mock
    private lateinit var s3Adapter: S3Adapter

    @Mock
    private lateinit var videoProcessingTaskRepositoryPort: VideoProcessingTaskRepositoryPort

    @InjectMocks
    private lateinit var getVideosService: GetVideosService

    private val clientId = "123"

    private fun createUserWithTasks(): UserEntity {
        val now = LocalDateTime.now()
        val expiredTime = now.minusHours(13)

        val originalVideo = OriginalVideo("3", "http://example.com/video.mp4", "video.mp4")
        val originalVideo2 = OriginalVideo("4", "http://example.com/video.mp4", "video.mp4")

        val processedFrame = ProcessedFrame("5", "http://example.com/processed.mp4", now, "processed.mp4", 1, 5)
        val processedFrame2 = ProcessedFrame("6", "http://example.com/processed.mp4", expiredTime, "processed.mp4", 1, 5)

        val videoList = listOf(
            VideoProcessingTaskEntity("1", originalVideo, processedFrame, VideoProcessStatus.PENDING),
            VideoProcessingTaskEntity("2", originalVideo2, processedFrame2, VideoProcessStatus.FINISHED)
        )
        return UserEntity(clientId, videoList)
    }

    @Test
    fun `should return all videos by client`() {
        val usr = createUserWithTasks()

        whenever(userRepositoryPort.findById(clientId)).thenReturn(usr)
        whenever(s3Adapter.generateDownloadPresignedUrl(any())).thenReturn("https://new.url")
        whenever(videoProcessingTaskRepositoryPort.save(any())).thenReturn(usr.allProcessingTasks[1])

        val result = getVideosService.getAllVideosByClient(clientId)

        assertEquals(2, result.size)
        assertEquals(VideoProcessStatus.PENDING, result[0].status)
        assertEquals(VideoProcessStatus.FINISHED, result[1].status)

        // Valida que houve chamada de regeneração de URL para o vídeo expirado
        verify(s3Adapter).generateDownloadPresignedUrl("processed.mp4")
        verify(videoProcessingTaskRepositoryPort).save(any())
    }

    @Test
    fun `should return empty list when no videos found`() {
        val usr = UserEntity(clientId, emptyList())

        whenever(userRepositoryPort.findById(clientId)).thenReturn(usr)

        val result = getVideosService.getAllVideosByClient(clientId)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should throw exception when no usr is found`() {
        whenever(userRepositoryPort.findById(clientId)).thenReturn(null)

        val result = assertThrows<NotFoundException> {
            getVideosService.getAllVideosByClient(clientId)
        }

        assertEquals("User: $clientId not found", result.message)
    }
}
