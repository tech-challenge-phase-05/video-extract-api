package br.com.videoextractor.domain

enum class VideoProcessStatus(
    val status: String
) {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    FINISHED("FINISHED"),
    ERROR("ERROR")
}