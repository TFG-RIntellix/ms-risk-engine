# ms-risk-engine

> Credit risk scoring microservice for the **RIntellix** platform — part of the NTT Enterprise ecosystem.

**ms-risk-engine** is a Spring Boot 4 microservice that orchestrates real-time credit risk scoring by consuming loan/mortgage/credit-card requests from Apache Kafka, invoking an external AI prediction model (XGBoost), computing financial risk metrics (EAD, LGD, ECL), and publishing enriched scoring results for downstream persistence.

---

## Table of Contents

- [Architecture](#architecture)
- [Scoring Pipeline](#scoring-pipeline)
- [Domain Model](#domain-model)
- [Risk Calculation Strategies](#risk-calculation-strategies)
- [SHAP Explainability](#shap-explainability)
- [Inter-Service Communication](#inter-service-communication)
- [Configuration](#configuration)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)

---

## Architecture

The service follows **Hexagonal Architecture** (Ports & Adapters) organized in three layers:

```
ms-risk-engine/
├── domain/             # Pure business logic, zero framework dependencies
│   ├── entities/       # Scoring, RiskMetrics, RiskFeature, ModelPredictionResult
│   ├── enums/          # RiskGrade, RequestType, ContractCategory
│   ├── exceptions/     # Domain-specific exceptions
│   ├── ports/          # Input & output port interfaces
│   └── strategies/     # Risk calculation strategies (Loan, Mortgage, Credit Card)
├── application/        # Use cases, orchestration, DTOs, mappers
│   ├── usecases/       # ScoringProcessingService, ScoringModelInvocationService
│   ├── strategies/     # Model execution strategies (per product type)
│   ├── mappers/        # Payload & result mappers
│   ├── dtos/           # Input/output DTOs
│   ├── constraints/    # Validation constraints
│   └── ports/          # Application-level port interfaces
└── infraestructure/    # Framework adapters, Kafka, HTTP clients
    ├── adapters/
    │   ├── input/      # Kafka consumer (ScoringKafkaConsumer)
    │   └── output/     # Kafka producer, AI model HTTP client
    ├── config/         # Spring configuration beans
    └── mappers/        # Infrastructure-level mappers
```

---

## Scoring Pipeline

The end-to-end scoring flow follows an **event-driven, asynchronous** pattern:

```
┌──────────────┐       ┌──────────────────┐       ┌───────────────┐
│ ms-core-data │──────►│  GenerateScoring  │──────►│ ms-risk-engine│
│              │ Kafka │  (Kafka Topic)    │       │               │
└──────────────┘       └──────────────────┘       └───────┬───────┘
                                                          │
                                          ┌───────────────┼───────────────┐
                                          │               │               │
                                          ▼               ▼               ▼
                                   ┌────────────┐ ┌─────────────┐ ┌────────────┐
                                   │ Map Payload │ │ Invoke AI   │ │ Calculate  │
                                   │ (Mapper)    │ │ Model (HTTP)│ │ EAD/LGD    │
                                   └────────────┘ └──────┬──────┘ └─────┬──────┘
                                                         │              │
                                                         ▼              ▼
                                                  ┌─────────────────────────┐
                                                  │ Assemble Full Metrics   │
                                                  │ (PD + EAD + LGD → ECL) │
                                                  └────────────┬────────────┘
                                                               │
                                                               ▼
                                                  ┌─────────────────────────┐
                                                  │  Enrich Explainability  │
                                                  │  (SHAP + feature values)│
                                                  └────────────┬────────────┘
                                                               │
                                                               ▼
                                                  ┌─────────────────────────┐
                                                  │   PersistScoring        │
                                                  │   (Kafka Topic)         │
                                                  └─────────────────────────┘
```

### Parallelized Execution

The AI model call and risk metric pre-computation run **in parallel**:

1. The model invocation fires asynchronously via `CompletableFuture`.
2. While the model call is in-flight, EAD and LGD are computed using the appropriate risk calculation strategy.
3. When the model returns PD, the service joins both results to assemble ECL and the final risk grade.

---

## Domain Model

| Entity | Description |
|---|---|
| **Scoring** | Root aggregate — stores request ID, model version, execution date, input snapshot, risk metrics, and explainability features. |
| **RiskMetrics** | PD (Probability of Default), LGD (Loss Given Default), EAD (Exposure at Default), ECL (Expected Calculated Loss), Risk Level. |
| **RiskFeature** | Single SHAP explainability feature — feature name, the value sent to the model, SHAP impact contribution, and direction (increase/decrease). |
| **ModelPredictionResult** | AI model response — PD, risk segment, SHAP base value, and top SHAP feature explanations. |

---

## Risk Calculation Strategies

Product-specific risk calculations are encapsulated as **Strategy Pattern** implementations:

| Strategy | Product Types | Key Logic |
|---|---|---|
| `LoanRiskCalculationStrategy` | Loans (PRESTAMO) | Standard EAD = loan amount; fixed LGD |
| `MortgageRiskCalculationStrategy` | Mortgages (HIPOTECA) | LGD adjusted by collateral haircut and liquidation costs |
| `StandardCreditCardRiskCalculationStrategy` | Standard credit cards | EAD = balance × CCF (normal) |
| `RevolvingCreditCardRiskCalculationStrategy` | Revolving credit cards | EAD = balance × CCF (revolving); higher LGD |

**ECL Formula:** `ECL = PD × LGD × EAD`

---

## SHAP Explainability

The AI model returns SHAP (SHapley Additive exPlanations) values for the top contributing features. The service enriches each SHAP feature with the actual **input value** sent to the model by performing a case-insensitive lookup against the model payload snapshot.

Example output:
```json
{
  "featureName": "Num_Moras_Previas",
  "featureValue": "3",
  "shapValue": 2.2487,
  "description": "increase"
}
```

---

## Inter-Service Communication

| Channel | Direction | Counterpart | Purpose |
|---|---|---|---|
| **Kafka** `GenerateScoring` | Inbound | ms-core-data | Receives scoring generation requests |
| **Kafka** `PersistScoring` | Outbound | ms-core-data | Publishes enriched scoring results |
| **HTTP** (WebClient) | Outbound | ms-model (Python) | Invokes AI prediction endpoints |

---

## Configuration

Key configuration properties (`application.properties`):

| Property | Description | Default |
|---|---|---|
| `server.port` | Service port | `8082` |
| `spring.kafka.bootstrap-servers` | Kafka broker address | `localhost:9092` |
| `scoring.kafka.topic.generation` | Inbound Kafka topic | `GenerateScoring` |
| `scoring.kafka.topic.persist` | Outbound Kafka topic | `PersistScoring` |
| `risk.model.base-url` | AI model service URL | `http://localhost:8000` |
| `risk.model.predict-loan-path` | Loan prediction endpoint | `/api/v1/risk/predict-loan` |
| `risk.model.version` | Model version label | `xgboost-loan-v1` |
| `risk.simulation.lgd.*` | LGD configuration per product | See properties file |

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Spring Boot | 4.0.4 | Application framework |
| Spring Kafka | — | Event-driven messaging |
| Spring WebClient | — | Reactive HTTP client for AI model calls |
| Jackson | — | JSON serialization/deserialization |
| Lombok | — | Boilerplate reduction |
| Maven | — | Build & dependency management |

---

## Getting Started

### Prerequisites

- Java 17+
- Apache Kafka running on `localhost:9092`
- [ms-model](../ms-model) (Python AI service) running on `localhost:8000`
- [ms-core-data](../ms-core-data) running on `localhost:8081`

### Build & Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
```

### Kafka Topics

Ensure the following topics exist in your Kafka cluster:
- `GenerateScoring` — consumed by this service
- `PersistScoring` — produced by this service

---

## Author

**Lucía Fernández Mancebo** — NTT Enterprise · RIntellix Platform
