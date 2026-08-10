# LucenLiChat (千言) — AI-Powered Instant Messaging Platform

A microservice-based IM platform with an **AI agent** powered by **LangChain4j**, **Qwen (DashScope)**, **MCP (Model Context Protocol)**, and **RAG (Retrieval-Augmented Generation)**.

## Features

### AI Agent (AiService)
- **Multi-turn Conversational AI**: Qwen-max model via DashScope, supporting both synchronous chat and streaming (SSE)
- **LangChain4j Agent**: Declarative AI service with `@SystemMessage`, `@InputGuardrails`, tool integration, and Redis-backed chat memory
- **MCP Tool Integration**: Model Context Protocol client connecting to Zhipu BigModel for real-time web search capabilities
- **RAG System**: PGVector embedding store + EmbeddingStoreIngestor for knowledge-grounded responses; supports dynamic knowledge insertion via API
- **AI Safety**: Input guardrails for content moderation; system prompt engineering for safe, concise responses
- **AI Observability**: Micrometer + Prometheus metrics collection with `ChatModelListener` for request count, token usage, error rate, and latency monitoring
- **Agentic Tools**: TimeTool, RagTool (knowledge management), EmailTool, and MCP web search — all orchestrated by the LangChain4j agent

### User Service
- **Authentication**: JWT-based auth with access/refresh token pairs, Redis token store, single-device enforcement
- **Email Verification**: QQ SMTP-based verification code system
- **Security**: AOP-based request logging, JWT interceptor, password hashing

### Architecture
- **Microservice Design**: 7-module Maven multi-module project (UserService, AiService, GateWay, OfflineDataService, RealTimeService, RedPacketService, Common)
- **Gateway**: Unified entry point (port 10010) for frontend routing

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.5.13, Java 17 |
| AI Framework | LangChain4j 1.1.0-beta7 |
| LLM | Qwen-max (DashScope) — chat + streaming + embedding |
| RAG | PGVector + EmbeddingStoreIngestor |
| MCP | langchain4j-mcp → Zhipu BigModel web search |
| ORM | MyBatis-Plus 3.5.12 |
| Database | MySQL 8.0 |
| Cache | Redis (chat memory, token store) |
| Vector DB | PGVector (1024-dim embeddings) |
| Auth | JJWT 0.11.5 |
| Email | Spring Boot Mail (QQ SMTP) |
| Monitoring | Micrometer + Prometheus + Actuator |

## AI Architecture

```
┌─────────┐     ┌──────────┐     ┌────────────────────────┐
│ Browser │────▶│ GateWay  │────▶│ AiService :8105        │
│         │     │ :10010   │     │                        │
└─────────┘     └──────────┘     │ ┌────────────────────┐ │
                                 │ │ LangChain4j Agent  │ │
                                 │ │ (AiServices.builder)│ │
                                 │ │                    │ │
                                 │ │ Chat Model: Qwen   │─┼──▶ DashScope API
                                 │ │ Streaming: Qwen    │─┼──▶ DashScope API
                                 │ │ Embedding: Qwen    │─┼──▶ PGVector
                                 │ │                    │ │
                                 │ │ Tools:             │ │
                                 │ │  - TimeTool        │ │
                                 │ │  - RagTool         │─┼──▶ PGVector RAG
                                 │ │  - EmailTool       │─┼──▶ QQ SMTP
                                 │ │  - MCP Web Search  │─┼──▶ Zhipu BigModel
                                 │ │                    │ │
                                 │ │ Memory: Redis      │ │
                                 │ │ Guardrails: Safe   │ │
                                 │ │ Monitor: Micrometer│ │
                                 │ └────────────────────┘ │
                                 └────────────────────────┘
```

### Agentic AI Workflow

1. **User sends message** → GateWay routes to AiService
2. **Input guardrail** checks content safety
3. **LangChain4j agent** orchestrates:
   - Retrieves chat memory from Redis (last 20 messages)
   - Determines if tool invocation is needed
   - Executes tools (RAG query, web search, time query, email)
   - Generates response via Qwen-max with system prompt
   - Updates chat memory
4. **Metrics collected** via `ChatModelListener` for observability

## Quick Start

### Prerequisites
- JDK 17+, Maven 3.6+
- MySQL 8.0, Redis, PGVector
- DashScope API key (Qwen)
- Zhipu BigModel API key (MCP web search)

### Configuration
1. Copy `.env.example` to `.env` and fill in actual values
2. All sensitive configuration reads from environment variables
3. Set profile to `dev` for local development

### Build & Run
```bash
# Build all modules
mvn clean install -DskipTests

# Start services
# UserService (port 8104)
cd UserService && mvn spring-boot:run

# AiService (port 8105, context-path /api)
cd AiService && mvn spring-boot:run -Dspring.profiles.active=dev

# GateWay (port 10010)
cd GateWay && mvn spring-boot:run
```

## Module Structure

```
LucenLiChat/
├── Common/              # Shared library
├── UserService/         # Auth & user management (port 8104)
├── AiService/           # AI agent service (port 8105)
│   ├── ai/              # AiChat interface + config
│   ├── config/          # DashScope, RAG, MCP, Redis configs
│   ├── controller/      # Chat endpoints (sync + streaming)
│   ├── guardrail/       # Content safety filters
│   ├── job/             # RAG data loader
│   ├── model/dto/       # Request/response DTOs
│   ├── tool/            # Agent tools (Time, RAG, Email)
│   ├── Monitor/         # AI metrics & observability
│   └── resources/
│       ├── docs/        # RAG knowledge base (.md files)
│       ├── front/       # Test frontend (HTML)
│       └── system-prompt/ # AI system prompts
├── GateWay/             # API gateway (port 10010)
├── OfflineDataService/  # Offline data processing (scaffold)
├── RealTimeService/     # Real-time messaging (scaffold)
└── RedPacketService/    # Red packet feature (scaffold)
```
