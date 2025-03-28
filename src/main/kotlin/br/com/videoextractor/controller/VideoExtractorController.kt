package br.com.videoextractor.controller

import br.com.videoextractor.adapters.mongodb.repository.entities.VideoProcessingTaskEntity
import br.com.videoextractor.controller.response.PresignedUrl
import br.com.videoextractor.core.service.GetVideosService
import br.com.videoextractor.core.service.VideoPathGeneratorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

@RestController(value = "/video")
class VideoExtractorController(
    val videoPathGeneratorService: VideoPathGeneratorService,
    val getVideosService: GetVideosService
) {
    val logger = Logger.getLogger(this.javaClass.name)
    @PostMapping("/client/{clientId}/upload/url")
    fun createVideoPath(@PathVariable clientId: String): ResponseEntity<PresignedUrl> {
        logger.info("Creating new video path for client $clientId")
        val presignedUrl = videoPathGeneratorService.generatePath(clientId)
        return ResponseEntity.ok(PresignedUrl(presignedUrl))
    }
   @GetMapping("/client/{clientId}/status")
   fun getAllVideosByClient(@PathVariable clientId: String): ResponseEntity<List<VideoProcessingTaskEntity>> {
       logger.info("Listing all videos for client $clientId")
       val videos = getVideosService.getAllVideosByClient(clientId)
       return ResponseEntity.ok(videos)
   }
}