package br.com.videoextractor.controller.message

import br.com.videoextractor.domain.VideoProcessStatus

data class UpdateVideoTaskMessage(
    val videoId: String,
    val processedFileKey: String?,
    val status: VideoProcessStatus
)
