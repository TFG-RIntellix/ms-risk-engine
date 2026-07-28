package es.NTTEnterprise.RIntellix.ms_risk_engine.application.mappers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;



import es.NTTEnterprise.RIntellix.ms_risk_engine.application.dtos.input.CreditCardScoringGenerationRequest;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.services.DtiCalculationService;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadFieldNames;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.ModelPayloadUtilities;
import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.SimulationConstants;

/**
 * Mapper for transforming credit card scoring requests into
 * the payload contract required by the ms-model API.
 *
 * Normalizes enum values and converts interest rate from percentage
 * to decimal fraction while preserving English field naming throughout.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 04-25-2026
 */

public class CreditCardModelPayloadMapper {

        private final ModelPayloadUtilities payloadUtilities;
        private final DtiCalculationService dtiCalculationService;

        public CreditCardModelPayloadMapper(final ModelPayloadUtilities payloadUtilities,
                        final DtiCalculationService dtiCalculationService) {
                this.payloadUtilities = Objects.requireNonNull(payloadUtilities,
                                LogMessage.MODEL_PAYLOAD_UTILITIES_CANNOT_BE_NULL);
                this.dtiCalculationService = Objects.requireNonNull(dtiCalculationService,
                                LogMessage.DTI_CALCULATION_SERVICE_CANNOT_BE_NULL);
        }

        /**
         * Maps credit card generation request to model payload.
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
                modelPayload.put(ModelPayloadFieldNames.FIELD_GENDER,
                                payloadUtilities.normalizeEnumForField(ModelPayloadFieldNames.FIELD_GENDER,
                                                request.getGender()));
                modelPayload.put(ModelPayloadFieldNames.FIELD_MARITAL_STATUS,
                                payloadUtilities.normalizeEnumForField(ModelPayloadFieldNames.FIELD_MARITAL_STATUS,
                                                request.getMaritalStatus()));
                modelPayload.put(ModelPayloadFieldNames.FIELD_EMPLOYMENT_STATUS,
                                payloadUtilities.normalizeEnumForField(ModelPayloadFieldNames.FIELD_EMPLOYMENT_STATUS,
                                                request.getEmploymentStatus()));
                modelPayload.put(ModelPayloadFieldNames.FIELD_EMPLOYMENT_SENIORITY_YEARS,
                                request.getEmploymentSeniorityYears());
                modelPayload.put(ModelPayloadFieldNames.FIELD_ANNUAL_INCOME, request.getAnnualIncome());
                modelPayload.put(ModelPayloadFieldNames.FIELD_INCOME_TYPE,
                                payloadUtilities.normalizeEnumForField(ModelPayloadFieldNames.FIELD_INCOME_TYPE,
                                                request.getIncomeType()));
                modelPayload.put(ModelPayloadFieldNames.FIELD_HOME_OWNERSHIP,
                                payloadUtilities.normalizeEnumForField(ModelPayloadFieldNames.FIELD_HOME_OWNERSHIP,
                                                request.getHomeOwnership()));
                modelPayload.put(ModelPayloadFieldNames.FIELD_EXISTING_OBLIGATIONS, request.getExistingObligations());
                modelPayload.put(ModelPayloadFieldNames.FIELD_DEPENDENTS, request.getDependents());
                modelPayload.put(ModelPayloadFieldNames.FIELD_CREDIT_LIMIT, request.getCreditLimit());
                modelPayload.put(ModelPayloadFieldNames.FIELD_IS_REVOLVING,
                                request.getIsRevolving() ? "Si" : "No");
                modelPayload.put(ModelPayloadFieldNames.FIELD_INTEREST_RATE,
                                payloadUtilities.normalizeInterestRateToFraction(request.getInterestRate()));
                modelPayload.put(ModelPayloadFieldNames.FIELD_LTI, request.getLti());
                modelPayload.put(ModelPayloadFieldNames.FIELD_DTI, calculateModelDti(request));
                modelPayload.put(ModelPayloadFieldNames.FIELD_PREVIOUS_DEFAULTS_COUNT,
                                request.getPreviousDefaultsCount());

                return modelPayload;
        }

        // TODO: Move it to the class that corresponds. Breaks single responsability
        // principle.
        private double calculateModelDti(final CreditCardScoringGenerationRequest request) {
                if (request == null) {
                        return SimulationConstants.ZERO_VALUE;
                }
                final double annualIncome = SimulationConstants.getSafe(request.getAnnualIncome());
                final double existingObligations = SimulationConstants.getSafe(request.getExistingObligations());
                final double creditLimit = SimulationConstants.getSafe(request.getCreditLimit());
                return dtiCalculationService.calculateModelDtiForCreditCardScoring(
                                annualIncome,
                                existingObligations,
                                creditLimit,
                                request.getIsRevolving());
        }
}
