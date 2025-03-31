package br.com.videoextractor.core.service

import br.com.videoextractor.adapters.S3.S3Adapter
import org.springframework.stereotype.Service
import java.util.*

@Service
class VideoPathGeneratorService(
    private val s3Adapter: S3Adapter,
    private val userManagerService: VideoUserManagerService,
) {
    fun generatePath(idCliente: String):String {
        val videoUuid = UUID.randomUUID().toString()
        val videoProcessingData = s3Adapter.generatePresignedUrl(videoUuid)
        userManagerService.createNewVideoRecord(idCliente,videoUuid, videoProcessingData)
        return videoProcessingData.presignedUrl
    }
}