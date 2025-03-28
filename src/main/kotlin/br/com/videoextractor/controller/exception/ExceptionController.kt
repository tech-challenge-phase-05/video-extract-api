package br.com.videoextractor.controller.exception

import br.com.videoextractor.core.service.exception.NotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.logging.Logger

@ControllerAdvice
class ExceptionController {
    val logger = Logger.getLogger(this.javaClass.name)
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<String> {
        logger.warning(e.message)
        return ResponseEntity.status(404).body(e.message)
    }
}