# Simulation Draft Calculation Error Analysis

## Error Identification
**Error**: `java.lang.IllegalArgumentException: Requested amount must be greater than 0`
**Timestamp**: 2026-05-23 17:19:46.695
**Request ID**: `665c1b2c3d4e5f6a7b8c9d02`

## Error Stack Trace
```
Servlet.service() for servlet [dispatcherServlet] threw exception
java.lang.IllegalArgumentException: Requested amount must be greater than 0
    at RiskCalculationDefaults.validateRequestAmount(RiskCalculationDefaults.java:127)
    at LoanRiskCalculationStrategy.calculatePrePdMetrics(LoanRiskCalculationStrategy.java:36)
    at RiskMetricsCalculationService.calculateRiskMetrics(RiskMetricsCalculationService.java:112)
    at CalculateSimulationDraftUseCase.calculateDraft(CalculateSimulationDraftUseCase.java:141)
    at SimulationDraftController.calculateDraft(SimulationDraftController.java:53)
```

## Root Cause Analysis

### The Bug: Field Name Normalization Failure

The error occurs due to a **critical field name mapping bug** in `SimulationModelPayloadMapper.getProperFieldName()`.

#### Data Flow Breakdown

1. **API Request** - Form changes arrive with camelCase English names:
   ```json
   {
     "requestedAmount": 25000,
     "termMonths": 60,
     "annualIncome": 50000
   }
   ```

2. **normalizeFormChangesToLowercase()** - Converts to lowercase:
   ```
   {requestedamount: 25000, termmonths: 60, annualincome: 50000}
   ```

3. **getProperFieldName()** - Maps back to proper camelCase:
   - Recognizes `"loanamount"` ✓
   - **Does NOT recognize** `"requestedamount"` ✗ (BUG!)
   - Falls through to default: returns `"requestedamount"` as-is

4. **Result** - Form changes have wrong field name:
   ```
   {
     "requestedamount": 25000,  // ✗ WRONG - Should be "loanAmount"
     "termMonths": 60,
     "annualIncome": 50000
   }
   ```

5. **After mergeData()** - Merged variables contain both:
   ```
   {
     "loanAmount": 18000.55,        // From base scoring
     "requestedamount": 25000,      // From form changes (wrong key!)
     "termMonths": 60,
     "annualIncome": 50000
   }
   ```

6. **In recalculateRiskIndicators()** - Field lookup fails:
   ```java
   final double loanAmount = getDouble(mergedVariables, 
       "loanAmount",  // Looking for this
       0);            // Gets default value because "requestedamount" doesn't match!
   ```
   - Lookup finds: `null`
   - Returns default: `0`

7. **Validation Error**:
   ```java
   if (requestedAmount <= 0) {
       throw new IllegalArgumentException(
           "Requested amount must be greater than 0");
   }
   ```
   Fails because `requestedAmount = 0`

### Why This Happens

The `getProperFieldName()` method only recognizes ONE English variant per field:
```java
case "loanamount":           // ✓ Recognized
    return ModelPayloadFieldNames.FIELD_LOAN_AMOUNT;
// case "requestedamount":   // ✗ MISSING! (Bug is here)
```

But API clients send `requestedAmount`, which normalizes to `requestedamount` and doesn't match the switch case.

## Impact

- **Severity**: CRITICAL 🔴
- **Affected Feature**: Simulation Draft Calculation
- **Products**: All loan types (LOAN, MORTGAGE, CREDIT_CARD)
- **Symptom**: Any simulation with modified `requestedAmount` fails
- **User Impact**: Cannot create simulations with adjusted loan amounts

## Solution

Add missing field name variant to `getProperFieldName()` method in `SimulationModelPayloadMapper`:

```java
case "requestedamount":  // Add this line!
    return ModelPayloadFieldNames.FIELD_LOAN_AMOUNT;
```

This ensures the commonly-used API field name `requestedAmount` is properly recognized and mapped to the internal field name `loanAmount`.

## Prevention

To prevent similar issues in the future:

1. **Document all field name variants** in a configuration or enum
2. **Add unit tests** for field name normalization with all variants
3. **Consider case-insensitive exact matching** instead of manual switch statements
4. **Auto-generate field mapping** from the source of truth (ModelPayloadFieldNames)

## Test Case

After the fix, this request should work:
```json
{
  "requestId": "665c1b2c3d4e5f6a7b8c9d02",
  "requestType": "LOAN",
  "formChanges": {
    "requestedAmount": 25000,
    "termMonths": 60,
    "annualIncome": 50000
  }
}
```

Expected response: ✓ Successful simulation with calculated metrics and deltas.
