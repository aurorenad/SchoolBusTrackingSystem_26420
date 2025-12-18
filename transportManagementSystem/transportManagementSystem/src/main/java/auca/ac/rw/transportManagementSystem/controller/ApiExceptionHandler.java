package auca.ac.rw.transportManagementSystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({ DataIntegrityViolationException.class, TransactionSystemException.class })
    public ResponseEntity<String> handleDataAndTransaction(Exception ex) {
        String message = rootMessage(ex);
        // Usually these are caused by DB constraints (duplicate key / null violates not-null / etc.)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(UnexpectedRollbackException.class)
    public ResponseEntity<String> handleUnexpectedRollback(UnexpectedRollbackException ex) {
        // This usually means a RuntimeException happened inside @Transactional but got swallowed.
        // We surface it as a 400 so the frontend can show a meaningful message.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(rootMessage(ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAny(Exception ex) {
        // Log full stack trace server-side, keep response concise.
        log.error("Unhandled error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + rootMessage(ex));
    }

    private String rootMessage(Throwable t) {
        Throwable cur = t;
        int guard = 0;
        while (cur.getCause() != null && cur.getCause() != cur && guard++ < 20) {
            cur = cur.getCause();
        }
        String msg = cur.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            msg = t.getMessage();
        }
        return msg == null || msg.trim().isEmpty() ? "Request failed" : msg;
    }
}


