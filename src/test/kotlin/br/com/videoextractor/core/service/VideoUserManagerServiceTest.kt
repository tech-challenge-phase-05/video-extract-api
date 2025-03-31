import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.core.service.CreateVideoProcessingTaskService
import br.com.videoextractor.core.service.VideoUserManagerService
import br.com.videoextractor.domain.VideoProcessStatus
import br.com.videoextractor.domain.VideoProcessingData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class VideoUserManagerServiceTest {

    @Mock
    private lateinit var userRepositoryPort: UserRepositoryPort

    @Mock
    private lateinit var  createVideoProcessingTaskService: CreateVideoProcessingTaskService

    @InjectMocks
    private lateinit var videoUserManagerService: VideoUserManagerService

    @Test
    fun `should create new user and a new video record`() {
        val clientId = "123"
        val videoUuid = "generated-uuid"
        val videoProcessingData = VideoProcessingData("http://example.com/presigned-url", "bucket", "fileName")
        val videoProcessingTask = VideoProcessingTaskEntity(
            id = videoUuid,
            originalVideo = OriginalVideo(videoUuid, videoProcessingData.s3Path, videoProcessingData.key),
            status = VideoProcessStatus.PENDING,
            processedFrame = ProcessedFrame(startTime = 0, endTime = 5)
        )

        val user = UserEntity(clientId,listOf(videoProcessingTask))

        whenever(userRepositoryPort.findById(clientId)).thenReturn(null)
        whenever(userRepositoryPort.createUser(clientId)).thenReturn(user)
        whenever(createVideoProcessingTaskService.createNewVideoProcessingTask(user,videoUuid, videoProcessingData )).thenReturn(videoProcessingTask)
        val result = videoUserManagerService.createNewVideoRecord(clientId, videoUuid, videoProcessingData)

        assertEquals(videoProcessingTask, result)
    }
    @Test
    fun `should create only the new video record`() {
        val clientId = "123"
        val videoUuid = "generated-uuid"
        val videoProcessingData = VideoProcessingData("http://example.com/presigned-url", "bucket", "fileName")
        val videoProcessingTask = VideoProcessingTaskEntity(
            id = videoUuid,
            originalVideo = OriginalVideo(videoUuid, videoProcessingData.s3Path, videoProcessingData.key),
            status = VideoProcessStatus.PENDING,
            processedFrame = ProcessedFrame(startTime = 0, endTime = 5)
        )

        val user = UserEntity(clientId,listOf(videoProcessingTask))

        whenever(userRepositoryPort.findById(clientId)).thenReturn(user)
        whenever(createVideoProcessingTaskService.createNewVideoProcessingTask(user,videoUuid, videoProcessingData )).thenReturn(videoProcessingTask)
        val result = videoUserManagerService.createNewVideoRecord(clientId, videoUuid, videoProcessingData)

        assertEquals(videoProcessingTask, result)
    }

}