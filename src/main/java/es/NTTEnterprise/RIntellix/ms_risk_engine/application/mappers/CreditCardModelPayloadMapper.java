package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;

/**
 * Mapper for transforming credit card scoring requests into
 * the temporary payload contract required by ms-model API.
 *
 * Credit card request body is currently a placeholder until
 * final API contract is defined.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */
@Component
public class CreditCardModelPayloadMapper {

    /**
     * Maps credit card generation request to current placeholder payload.
     *
     * @param request               the source credit card scoring request.
     * @param normalizedRequestType the normalized request type.
     * @return the model payload to send to credit card prediction endpoint.
     */
    public Map<String, Object> toModelPayload(
            final CreditCardScoringGenerationRequest request,
            final String normalizedRequestType) {
        final Map<String, Object> modelPayload = new LinkedHashMap<>();
        modelPayload.put(ModelPayloadFieldNames.FIELD_AGE, request.getAge());
        modelPayload.put(ModelPayloadFieldNames.FIELD_GENDER, request.getGender());
        modelPayload.put(ModelPayloadFieldNames.FIELD_MARITAL_STATUS, request.getMaritalStatus());
        modelPayload.put(ModelPayloadFieldNames.FIELD_EMPLOYMENT_STATUS, request.getEmploymentStatus());
        modelPayload.put(ModelPayloadFieldNames.FIELD_EMPLOYMENT_SENIORITY_YEARS,
                request.getEmploymentSeniorityYears());
        modelPayload.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, request.getAnnualIncome());
        modelPayload.put(ModelPayloadFieldNames.FIELD_INCOME_TYPE, request.getIncomeType());
        modelPayload.put(ModelPayloadFieldNames.FIELD_HOME_OWNERSHIP, request.getHomeOwnership());
        modelPayload.put(ModelPayloadFieldNames.FIELD_DEPENDENTS, request.getDependents());
        modelPayload.put(ModelPayloadFieldNames.FIELD_CREDIT_LIMIT, request.getCreditLimit());
        modelPayload.put(ModelPayloadFieldNames.FIELD_IS_REVOLVING, request.getIsRevolving());
        modelPayload.put(ModelPayloadFieldNames.FIELD_INTEREST_RATE, request.getInterestRate());
        modelPayload.put(ModelPayloadFieldNames.FIELD_LTI, request.getLti());
        modelPayload.put(ModelPayloadFieldNames.FIELD_DTI, request.getDti());
        modelPayload.put(ModelPayloadFieldNames.FIELD_PREVIOUS_DEFAULTS_COUNT, request.getPreviousDefaultsCount());

        return modelPayload;
    }
}
