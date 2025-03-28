package br.com.videoextractor.adapters.S3

import br.com.videoextractor.domain.VideoProcessingData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.util.logging.Logger

@Service
class S3Adapter(
    @Value("\${bucket.name}") val bucketName:String,
    @Value("\${path.prefix}") val pathPrefix:String,
) {
    val logger = Logger.getLogger(this.javaClass.name)

    fun generatePresignedUrl(videoUuid: String): VideoProcessingData {
        val key = pathPrefix+videoUuid
        val presignedUrl = this.createPresignedUrl( bucketName, key , mapOf("uuid" to videoUuid))
        return VideoProcessingData(presignedUrl, bucketName,key)
    }
    fun createPresignedUrl(bucketName: String, keyName: String, metadata: Map<String, String>): String {
        S3Presigner.create().use { presigner ->
            val objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .metadata(metadata)
                .build()
            val presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(java.time.Duration.ofHours(12))
                .putObjectRequest(objectRequest)
                .build()


            val presignedRequest = presigner.presignPutObject(presignRequest)
            val myURL = presignedRequest.url().toString()
            val method = presignedRequest.httpRequest().method()
            logger.info("Presigned URL to upload a file to: [$myURL]")
            logger.info("HTTP method: [$method]")
            return presignedRequest.url().toExternalForm()
        }
    }

}