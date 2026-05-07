package es.NTTEnterprise.RIntellix.ms_risk_engine.application.usecases;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.entities.RiskMetrics;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.ContractCategory;
import es.NTTEnterprise.RIntellix.ms_risk_engine.domain.enums.RiskGrade;

@Service
public class RiskCalculationService {

    private final double ccfNormal;
    private final double ccfRevolving;
    private final double lgdLoan;
    private final double lgdCcNormal;
    private final double lgdCcRevolving;
    private final double mortgageHaircut;
    private final double mortgageLiquidationCostRate;
    private final double mortgageLgdFloor;

    public RiskCalculationService(
            @Value("${risk.simulation.default.ccf.normal}") final double ccfNormal,
            @Value("${risk.simulation.default.ccf.revolving}") final double ccfRevolving,
            @Value("${risk.simulation.lgd.loan}") final double lgdLoan,
            @Value("${risk.simulation.lgd.cc.normal}") final double lgdCcNormal,
            @Value("${risk.simulation.lgd.cc.revolving}") final double lgdCcRevolving,
            @Value("${risk.simulation.lgd.mortgage.haircut}") final double mortgageHaircut,
            @Value("${risk.simulation.lgd.mortgage.liquidation-cost-rate}") final double mortgageLiquidationCostRate,
            @Value("${risk.simulation.lgd.floor}") final double mortgageLgdFloor) {
        this.ccfNormal = ccfNormal;
        this.ccfRevolving = ccfRevolving;
        this.lgdLoan = lgdLoan;
        this.lgdCcNormal = lgdCcNormal;
        this.lgdCcRevolving = lgdCcRevolving;
        this.mortgageHaircut = mortgageHaircut;
        this.mortgageLiquidationCostRate = mortgageLiquidationCostRate;
        this.mortgageLgdFloor = mortgageLgdFloor;
    }

    public RiskMetrics calculate(final Double pd, final ContractCategory category,
            final Double requestedAmount, final Double ltv,
            final Double annualIncome, final Integer termMonths, final Double interestRate) {

        double safePd = clampRatio(pd == null ? 0.0 : pd);
        double safeRequestedAmount = sanitizeAmount(requestedAmount);
        double safeLtv = clampRatio(ltv == null ? 0.0 : ltv);

        double ead = calculateEad(category, safeRequestedAmount);
        double lgd = calculateLgd(category, ead, safeRequestedAmount, safeLtv);
        double ecl = safePd * lgd * ead;
        RiskGrade riskGrade = calculateRiskGrade(safePd, ecl, ead, safeRequestedAmount, annualIncome, termMonths,
                interestRate);

        return new RiskMetrics(safePd, lgd, ead, ecl, riskGrade);
    }

    public Double calculateFrenchMonthlyPayment(final Double amount, final Integer termMonths,
            final Double annualRate) {
        double principal = sanitizeAmount(amount);
        int terms = termMonths == null || termMonths <= 0 ? 1 : termMonths;
        double monthlyRate = (annualRate == null ? 0.0 : annualRate) / 1200.0;

        if (monthlyRate == 0.0) {
            return principal / terms;
        }

        double numerator = principal * monthlyRate;
        double denominator = 1.0 - Math.pow(1.0 + monthlyRate, -terms);
        if (denominator == 0.0) {
            return principal / terms;
        }
        return numerator / denominator;
    }

    public ContractCategory resolveContractCategory(final String requestTypeRaw) {
        if (requestTypeRaw == null) {
            return ContractCategory.LOAN;
        }
        String normalized = requestTypeRaw.trim().replace(' ', '_').toUpperCase();
        if (normalized.contains("HIPOTECA") || normalized.contains("MORTGAGE")) {
            return ContractCategory.MORTGAGE;
        }
        if (normalized.contains("REVOLVING")) {
            return ContractCategory.CC_REVOLVING;
        }
        if (normalized.contains("TARJETA") || normalized.contains("CARD")) {
            return ContractCategory.CC_NORMAL;
        }
        return ContractCategory.LOAN;
    }

    private double calculateEad(final ContractCategory category, final double requestedAmount) {
        return switch (category) {
            case MORTGAGE, LOAN -> requestedAmount;
            case CC_NORMAL -> requestedAmount * ccfNormal;
            case CC_REVOLVING -> requestedAmount * ccfRevolving;
        };
    }

    private double calculateLgd(final ContractCategory category, final double ead, final double requestedAmount,
            final double ltv) {
        if (ead <= 0.0) {
            return mortgageLgdFloor;
        }

        return switch (category) {
            case LOAN -> lgdLoan;
            case CC_NORMAL -> lgdCcNormal;
            case CC_REVOLVING -> lgdCcRevolving;
            case MORTGAGE -> calculateMortgageLgd(ead, requestedAmount, ltv);
        };
    }

    private double calculateMortgageLgd(final double ead, final double requestedAmount, final double ltv) {
        if (ltv <= 0.0) {
            return Math.max(mortgageLgdFloor, 0.35);
        }

        double appraisalValue = requestedAmount / ltv;
        double liquidationCost = appraisalValue * mortgageLiquidationCostRate;
        double recoverableCollateral = appraisalValue * (1.0 - mortgageHaircut) - liquidationCost;
        double lgd = (ead - recoverableCollateral) / ead;
        if (Double.isNaN(lgd) || Double.isInfinite(lgd)) {
            return Math.max(mortgageLgdFloor, 0.35);
        }
        return clampRatio(Math.max(lgd, mortgageLgdFloor));
    }

    private RiskGrade calculateRiskGrade(final double pd, final double ecl, final double ead,
            final double amount, final Double annualIncome, final Integer termMonths, final Double rate) {

        int gradeIndex;
        if (pd < 0.05) {
            gradeIndex = 0;
        } else if (pd < 0.12) {
            gradeIndex = 1;
        } else if (pd < 0.22) {
            gradeIndex = 2;
        } else {
            gradeIndex = 3;
        }

        double eclRatio = ead <= 0.0 ? 1.0 : (ecl / ead);
        if (eclRatio > 0.08 && gradeIndex < 3) {
            gradeIndex += 1;
        }
        if (eclRatio < 0.02 && gradeIndex > 0) {
            gradeIndex -= 1;
        }

        double monthlyPayment = calculateFrenchMonthlyPayment(amount, termMonths, rate);
        double income = annualIncome == null ? 0.0 : annualIncome;
        double monthlyIncome = income / 12.0;
        double paymentToIncome = monthlyIncome <= 0.0 ? 1.0 : monthlyPayment / monthlyIncome;
        if (paymentToIncome > 0.45 && gradeIndex < 3) {
            gradeIndex += 1;
        }

        return switch (gradeIndex) {
            case 0 -> RiskGrade.A;
            case 1 -> RiskGrade.B;
            case 2 -> RiskGrade.C;
            default -> RiskGrade.D;
        };
    }

    private double sanitizeAmount(final Double amount) {
        if (amount == null || amount < 0.0) {
            return 0.0;
        }
        return amount;
    }

    private double clampRatio(final double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 1.0) {
            return 1.0;
        }
        return value;
    }
}
