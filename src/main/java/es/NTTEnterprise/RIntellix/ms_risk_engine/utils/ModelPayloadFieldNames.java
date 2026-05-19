package es.NTTEnterprise.RIntellix.ms_risk_engine.utils;

/**
 * Constants for model payload field names used in mapper classes.
 * Centralizes all Spanish field names expected by ms-model API
 * to prevent hardcoding and enable easy maintenance.
 *
 * @author Lucia Fernandez Mancebo
 * @Date 09-05-2026
 */
public final class ModelPayloadFieldNames {

    private ModelPayloadFieldNames() {
        throw new UnsupportedOperationException(LogMessage.UTILITY_CLASS_NEVER_INSTANTIATE);
    }

    // ============================================================
    // LOAN & MORTGAGE MODEL PAYLOAD FIELDS
    // ============================================================

    /** Age field name in model payload. */
    public static final String FIELD_EDAD = "edad";

    /** Gender field name in model payload. */
    public static final String FIELD_GENERO = "genero";

    /** Marital status field name in model payload. */
    public static final String FIELD_ESTADO_CIVIL = "estado_civil";

    /** Education level field name in model payload. */
    public static final String FIELD_EDUCACION = "educacion";

    /** Employment status field name in model payload. */
    public static final String FIELD_SITUACION_LABORAL = "situacion_laboral";

    /** Occupation sector field name in model payload. */
    public static final String FIELD_SECTOR_TRABAJO = "sector_trabajo";

    /** Number of dependents field name in model payload. */
    public static final String FIELD_DEPENDIENTES = "dependientes";

    /** Home ownership type field name in model payload. */
    public static final String FIELD_VIVIENDA = "vivienda";

    /** Has mortgage flag field name in model payload. */
    public static final String FIELD_TIENE_HIPOTECA = "tiene_hipoteca";

    /** Annual income field name in model payload. */
    public static final String FIELD_INGRESOS_ANUALES = "ingresos_anuales";

    /** Loan type field name in model payload. */
    public static final String FIELD_TIPO_PRESTAMO = "tipo_prestamo";

    /** Loan purpose field name in model payload. */
    public static final String FIELD_PROPOSITO = "proposito";

    /** Loan amount field name in model payload. */
    public static final String FIELD_MONTO_PRESTAMO = "monto_prestamo";

    /** Loan term in months field name in model payload. */
    public static final String FIELD_PLAZO_MESES = "plazo_meses";

    /** Interest rate field name in model payload. */
    public static final String FIELD_TASA_INTERES = "tasa_interes";

    /** Loan-to-value ratio field name in model payload. */
    public static final String FIELD_LTV = "ltv";

    /** Debt-to-income ratio field name in model payload. */
    public static final String FIELD_DTI = "dti";

    /** Number of previous loans field name in model payload. */
    public static final String FIELD_NUM_PRESTAMOS_PREVIOS = "num_prestamos_previos";

    /** Number of previous defaults field name in model payload. */
    public static final String FIELD_NUM_MORAS_PREVIAS = "num_moras_previas";

    // ============================================================
    // CREDIT CARD MODEL PAYLOAD FIELDS
    // ============================================================

    /** Request type field name in credit card model payload. */
    public static final String FIELD_REQUEST_TYPE = "request_type";

    /** Request ID field name in credit card model payload. */
    public static final String FIELD_REQUEST_ID = "request_id";
}
