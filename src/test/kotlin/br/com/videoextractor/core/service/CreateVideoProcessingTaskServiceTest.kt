package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.mongodb.repository.entities.OriginalVideo
import br.com.videoextractor.adapters.mongodb.repository.entities.ProcessedFrame
import br.com.videoextractor.adapters.mongodb.repository.entities.UserEntity
import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.adapters.mongodb.repository.port.UserRepositoryPort
import br.com.videoextractor.adapters.mongodb.repository.port.VideoProcessingTaskRepositoryPort
import br.com.videoextractor.domain.VideoProcessStatus
import br.com.videoextractor.domain.VideoProcessingData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockitoExtension::class)
class CreateVideoProcessingTaskServiceTest {
    @Mock
    private lateinit var videoProcessingRepository: VideoProcessingTaskRepositoryPort

    @Mock
    private lateinit var userRepositoryPort: UserRepositoryPort

    @InjectMocks
    private lateinit var createVideoProcessingTaskService: CreateVideoProcessingTaskService

    @Test
    fun `should create a new video processing task`() {
        // Given
        val videoProcessingTask = VideoProcessingTaskEntity(
            id = "videoId",
            originalVideo = OriginalVideo(
                id = "videoId",
                url = "presignedUrl",
                fileName = "key/video.mp4"
            ),
            ProcessedFrame(
                id = "videoId",
                url = "videoUrl",
                fileName = "videoName",
                startTime = 1,
                endTime = 5
            ),
            status = VideoProcessStatus.PENDING
        )
        val user = UserEntity(
            id = "userId",
            allProcessingTasks = listOf(videoProcessingTask)
        )


        whenever(videoProcessingRepository.save(any())).thenReturn(videoProcessingTask)

        whenever(userRepositoryPort.saveUser(user)).thenReturn(user)

        // When
        val result = createVideoProcessingTaskService.createNewVideoProcessingTask(
            user,
            "videoId",
            VideoProcessingData("presignedUrl", "bucket", "key/video.mp4")
        )

        // Then
        assertEquals(videoProcessingTask, result)
    }

     @Test
     fun `should throw an exception when try to create a new video processing task with an existing videoId`() {

         // Given
         val videoProcessingTask = VideoProcessingTaskEntity(
             id = "videoId",
             originalVideo = OriginalVideo(
                 id = "videoId",
                 url = "presignedUrl",
                 fileName = "key/video.mp4"
             ),
             ProcessedFrame(
                 id = "videoId",
                 url = "videoUrl",
                 fileName = "videoName",
                 startTime = 1,
                 endTime = 5
             ),
             status = VideoProcessStatus.PENDING
         )
         val user = UserEntity(
             id = "userId",
             allProcessingTasks = listOf(videoProcessingTask)
         )
         whenever(videoProcessingRepository.save(any())).thenThrow(RuntimeException("Video already exists"))

         // When
         val exception =  assertFailsWith<RuntimeException> {
             createVideoProcessingTaskService.createNewVideoProcessingTask(
                 user,
                 "videoId",
                 VideoProcessingData("presignedUrl", "bucket", "key/video.mp4")
             )
         }
         assertEquals(exception.message, "Video already exists")

     }
}