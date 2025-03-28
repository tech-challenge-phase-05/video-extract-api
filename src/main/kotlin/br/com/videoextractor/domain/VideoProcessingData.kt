package br.com.videoextractor.domain

import java.net.URL

class VideoProcessingData(
    val presignedUrl: String,
    val bucket: String,
    val key: String
){
    val s3Path:String
        get() = "s3://$bucket/$key"
}

