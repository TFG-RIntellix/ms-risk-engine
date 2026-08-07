package es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.output;

import java.time.LocalDateTime;

/**
 * Standard error response DTO used by GlobalExceptionHandler.
 *
 * @author Lucía Fernández Mancebo
 * @date 08/05/2026
 */
public class ErrorResponseDTO {

    private final String errorCode;
    private final String message;
    private final int httpStatus;
    private final LocalDateTime timestamp;

    public ErrorResponseDTO(final String errorCode, final String message, final int httpStatus,
            final LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
