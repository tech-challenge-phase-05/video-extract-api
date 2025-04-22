import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.core.service.GetVideosService
import br.com.videoextractor.core.service.exception.NotFoundException
import br.com.videoextractor.domain.VideoProcessStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class GetVideosServiceTest {

    @Mock
    private lateinit var userRepositoryPort: UserRepositoryPort

    @InjectMocks
    private lateinit var getVideosService: GetVideosService

    val clientId = "123"
    val originalVideo = OriginalVideo("3", "http://example.com/video.mp4", "video.mp4")
    val originalVideo2 = OriginalVideo("4", "http://example.com/video.mp4", "video.mp4")
    val processedFrame = ProcessedFrame("5", "http://example.com/processed.mp4", LocalDateTime.now(), "processed.mp4", 1, 5)
    val processedFrame2 = ProcessedFrame("5", "http://example.com/processed.mp4", LocalDateTime.now(),"processed.mp4", 1, 5)
    val videoList = listOf(
        VideoProcessingTaskEntity("1", originalVideo, processedFrame, VideoProcessStatus.PENDING),
        VideoProcessingTaskEntity("2", originalVideo2, processedFrame2, VideoProcessStatus.FINISHED)
    )
    val usr = UserEntity("123", videoList)

    @Test
    fun `should return all videos by client`() {

        whenever(userRepositoryPort.findById(clientId)).thenReturn(usr)

        val result = getVideosService.getAllVideosByClient(clientId)

        assertEquals(videoList, result)
    }

    @Test
    fun `should return empty list when no videos found`() {
        usr.allProcessingTasks = emptyList()
        whenever(userRepositoryPort.findById(clientId)).thenReturn(usr)

        val result = getVideosService.getAllVideosByClient(clientId)

        assertEquals(emptyList<VideoProcessingTaskEntity>(), result)
    }
    @Test
    fun `should throw exception when no usr is found`() {
        whenever(userRepositoryPort.findById(clientId)).thenReturn(null)

        val result = assertThrows<NotFoundException> {
            getVideosService.getAllVideosByClient(clientId)
        }
        assertEquals(result.message,"User: $clientId not found")
    }
}