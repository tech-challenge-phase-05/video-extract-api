package br.com.videoextractor.adapters.s3

import br.com.videoextractor.domain.VideoProcessingData
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.logging.Logger

@Service
class S3Adapter(
    @Value("\${bucket.name}") val bucketName: String,
    @Value("\${path.prefix}") val uploadPrefix: String
) {
    val logger = Logger.getLogger(this.javaClass.name)

    private val presigner = S3Presigner.create()

    fun generateUploadPresignedUrl(videoUuid: String): VideoProcessingData {
        val key = "$uploadPrefix$videoUuid.mp4"
        val url = createPresignedPutUrl(bucketName, key, mapOf("uuid" to videoUuid))
        return VideoProcessingData(url, bucketName, key)
    }

    fun generateDownloadPresignedUrl(zipKey: String): String {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(zipKey)
            .build()

        val presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(12))
            .getObjectRequest(getObjectRequest)
            .build()

        val presignedRequest = presigner.presignGetObject(presignRequest)
        val url = presignedRequest.url().toExternalForm()
        logger.info("Presigned URL to download a file: [$url]")
        return url
    }

    private fun createPresignedPutUrl(bucketName: String, key: String, metadata: Map<String, String>): String {
        val objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .metadata(metadata)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(12))
            .putObjectRequest(objectRequest)
            .build()

        val presignedRequest = presigner.presignPutObject(presignRequest)
        val url = presignedRequest.url().toExternalForm()
        logger.info("Presigned URL to upload a file: [$url]")
        return url
    }
}
