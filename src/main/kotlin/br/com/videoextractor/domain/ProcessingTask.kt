package br.com.videoextractor.domain

class ProcessingTask(
    var id: String,
    var originalVideoUrl: String,
    var startTime: Long,
    var endTime:Long
)