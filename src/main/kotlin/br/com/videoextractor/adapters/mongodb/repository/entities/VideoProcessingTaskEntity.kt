package br.com.videoextractor.adapters.mongodb.repository.entities

import br.com.videoextractor.domain.VideoProcessStatus
import org.springframework.data.mongodb.core.mapping.Document

@Document("videoProcessingTaskDocument")
data class VideoProcessingTaskEntity (
    var id: String,
    val originalVideo:OriginalVideo,
    val processedFrame:ProcessedFrame,
    var status: VideoProcessStatus,
)

data class  OriginalVideo(
    val id: String,
    val url: String,
    val fileName: String,
)

data class ProcessedFrame(
    var id: String? = null,
    var url: String? = null,
    var fileName: String?= null,
    val startTime: Long,
    val endTime:Long
)