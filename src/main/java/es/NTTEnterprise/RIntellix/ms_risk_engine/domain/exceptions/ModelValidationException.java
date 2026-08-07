package es.NTTEnterprise.RIntellix.ms_risk_engine.domain.exceptions;

/**
 * Exception thrown when the model service returns a 422 Unprocessable Entity
 * response.
 * 
 * This indicates that the request payload failed validation checks in the model
 * service.
 * The model server could not process the request due to semantic errors in the
 * provided data.
 * 
 * @author Lucía Fernández Mancebo
 * @date 08/05/2026
 */
public class ModelValidationException extends ModelPredictionException {

    private static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    /**
     * Constructs a ModelValidationException with a message.
     *
     * @param message the error message describing the validation failure.
     */
    public ModelValidationException(final String message) {
        super(message, UNPROCESSABLE_ENTITY_STATUS);
    }

    /**
     * Constructs a ModelValidationException with a message and cause.
     *
     * @param message the error message describing the validation failure.
     * @param cause   the underlying cause.
     */
    public ModelValidationException(final String message, final Throwable cause) {
        super(message, UNPROCESSABLE_ENTITY_STATUS, cause);
    }
}
