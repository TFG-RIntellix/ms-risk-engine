# ms-risk-engine

**Microservicio de orquestación del cálculo de riesgo de la plataforma RIntellix.**

`Java 17` · `Spring Boot 4` · `OpenFeign` · `Apache Kafka` · `Arquitectura Hexagonal`

---

## 1. Descripción general

`ms-risk-engine` es el orquestador de una simulación de riesgo crediticio. No calcula él mismo
la probabilidad de impago (eso se delega en `ms-model`); en su lugar, coordina todo el flujo de
la simulación: valida el borrador recibido, obtiene los datos de la solicitud, invoca al
servicio de scoring de ML, aplica las estrategias de cálculo de riesgo según el tipo de producto
y publica el resultado final del scoring para que `ms-core-data` lo persista.

Responsabilidades principales:

- Exponer el endpoint que inicia un nuevo borrador de simulación.
- Recuperar el contexto de solicitud/scoring desde `ms-core-data`.
- Invocar a `ms-model` para obtener la predicción de PD (probabilidad de impago) y su
  explicación SHAP.
- Aplicar estrategias de riesgo específicas por producto (p. ej., tarjetas de crédito revolving
  frente a préstamos a plazo).
- Publicar el evento de scoring resultante en Kafka para que lo consuman `ms-core-data` y
  `ms-reporting`.

## 2. Aspectos clave del sistema

- **Arquitectura hexagonal (puertos y adaptadores).** `domain` contiene entidades, estrategias y
  servicios sin dependencias de framework; `application` contiene los casos de uso, puertos de
  entrada y DTOs; `infrastructure` contiene los adaptadores REST/Kafka, los clientes Feign y la
  configuración.
- **Patrón Strategy para el cálculo de riesgo.** `domain/strategies` (con su contraparte de
  infraestructura en `infrastructure/adapters/input/kafka/strategy`) implementa una estrategia
  por producto crediticio — p. ej., `RevolvingCreditCardRiskCalculationStrategy` — de forma que
  se pueden añadir nuevos tipos de producto sin modificar la lógica de cálculo existente.
- **Clientes HTTP declarativos (Feign).** `infrastructure/adapters/output/clients` contiene
  `MsCoreDataClient` (lee datos de solicitud/scoring) y `MsModelClient` (invoca el endpoint de
  predicción de ML), manteniendo la integración saliente declarativa y fácil de testear.
- **Propagación del resultado dirigida por eventos.** Una vez calificada una simulación, el
  resultado se emite por Kafka en lugar de mediante una llamada síncrona, desacoplando el motor
  de sus consumidores.
- **Gestión de errores aislada.** `infrastructure/adapters/output/handler` centraliza cómo se
  traducen los fallos de las llamadas salientes (Feign, Kafka) en resultados a nivel de dominio.

### Recurso REST principal

| Recurso | Ruta | Notas |
|---|---|---|
| Borrador de simulación | `POST /api/v1/simulations/draft` | Inicia un nuevo flujo de simulación de riesgo |

### Estructura del repositorio

El siguiente esquema ilustra la distribución del código fuente y cómo las piezas clave de la arquitectura descrita encajan en las carpetas principales del proyecto:

![Estructura de directorios](./estructura_directorios_ms_risk_engine.svg)

## 3. Tecnologías

- **Lenguaje / runtime:** Java 17
- **Framework:** Spring Boot 4 (`spring-boot-starter-web`, `spring-boot-starter-webflux`,
  `spring-boot-starter-validation`, `spring-boot-starter-actuator`, `spring-boot-starter-logging`)
- **Llamadas entre servicios:** Spring Cloud OpenFeign (`spring-cloud-starter-openfeign`)
- **Mensajería:** `spring-kafka`
- **Utilidades:** Lombok, Jackson

## 4. Requisitos previos

- JDK 17 o superior
- Maven 3.9+

## 5. Puesta en marcha

> `**IMPORTANTE**`

> **Despliegue global de la plataforma**:
> Este repositorio contiene únicamente el código del motor de riesgo. Para levantar la plataforma RIntellix completa (incluyendo este servicio, Kafka, Keycloak y el resto de microservicios), clona el repositorio principal de infraestructura **[TFG-RIntellix/rintellix-deployment]** y sigue sus instrucciones.

Los siguientes comandos se proporcionan para el desarrollo local, revisión de código y ejecución de pruebas:

```bash
# 1. Clonar el repositorio
git clone https://github.com/TFG-RIntellix/ms-risk-engine.git
cd ms-risk-engine

# 2. Compilar y ejecutar pruebas locales
mvn clean test
```

## 6. Configuración

Las siguientes propiedades se consumen a través de `application.yaml` o variables de entorno correspondientes:

| Propiedad | Descripción | Valor por defecto |
|---|---|---|
| `app.clients.core-data.url` | URL base del cliente Feign para `ms-core-data` | `http://localhost:8081` |
| `app.clients.model.url` | URL base del cliente Feign para `ms-model` | `http://localhost:8000` |
| `spring.kafka.bootstrap-servers` | Servidores bootstrap del broker de Kafka | `localhost:9092` |
| `app.kafka.topics.scoring-result` | Topic donde se publica el resultado del scoring | `risk.scoring.result` |
| `server.port` | Puerto en el que escucha el servicio (interno) | `8082` |

Consulta `SIMULATION_ERROR_ANALYSIS.md` en este repositorio para un desglose de los modos de fallo conocidos en el flujo de simulación y cómo se gestionan actualmente.

## 7. Servicios relacionados

- **ms-core-data** — proporciona los datos de la solicitud y persiste el resultado final del
  scoring.
- **ms-model** — proporciona la predicción de probabilidad de impago y la explicación SHAP.
- **ms-reporting** — consume el evento de scoring para generar el informe de crédito.
- **ms-sec-gateway** — enruta el tráfico externo hacia este servicio.

## 8. Autora

Lucía Fernández Mancebo — TFG *RIntellix*, Universidad de Cantabria.



