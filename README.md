# GitHub-GPT — AI-Powered GitHub Repository Assistant

**GitHub-GPT** is an AI-powered developer assistant that helps developers understand, explore, and interact with GitHub repositories using natural language.

Instead of manually searching through hundreds of files, developers can connect a GitHub repository, index its source code, and prepare it for AI-powered code understanding and question answering.

GitHub-GPT uses **Retrieval-Augmented Generation (RAG)**, code chunking, vector embeddings, and semantic search to transform repository source code into an AI-searchable knowledge base.

---

## 📌 Overview

GitHub-GPT allows developers to connect a GitHub repository and prepare its source code for AI-powered conversations.

The application:

1. Connects to a GitHub repository.
2. Fetches the repository file tree using the GitHub API.
3. Filters supported source files.
4. Retrieves source-code content.
5. Splits large files into smaller code chunks.
6. Generates vector embeddings for each chunk.
7. Stores the embeddings in PostgreSQL using pgvector.
8. Tracks indexing progress.
9. Marks the repository as `READY` when indexing is completed.
10. Uses the indexed code as context for repository-aware AI chat.

### Example

Suppose a repository contains:

```text
User.java
RepoService.java
RepoController.java
application.yml
```

A developer can eventually ask questions such as:

```text
How does authentication work in this project?

Explain the responsibilities of RepoService.java.

Where is repository indexing initiated?

How does the application connect to PostgreSQL?
```

The goal is to make unfamiliar codebases easier to understand through natural language.

---

# 🎯 Problem Statement

Understanding an unfamiliar codebase can be difficult and time-consuming.

Developers often need to:

* Search through hundreds of files.
* Trace method calls.
* Understand relationships between classes.
* Find business logic.
* Understand configuration.
* Identify dependencies between components.
* Manually locate relevant code before asking questions.

GitHub-GPT addresses this problem by converting repository source code into a searchable AI-ready knowledge base.

The indexing pipeline transforms source files into smaller code chunks, generates embeddings, and stores those embeddings in a vector database.

This allows relevant code to be retrieved semantically when answering developer questions.

---

# ✨ Key Features

## GitHub Repository Integration

* Connect GitHub repositories.
* Validate repository access.
* Fetch repository file trees using the GitHub API.
* Retrieve source-code files.
* Prepare repositories for AI-powered analysis.

## ⚡ Asynchronous Repository Indexing

Repository indexing can involve hundreds or thousands of files.

GitHub-GPT uses asynchronous processing so that the API does not remain blocked while the repository is being indexed.

Features include:

* Background indexing.
* Indexing status tracking.
* File processing counters.
* Chunk counters.
* Failure handling.
* Repository re-indexing.

## 🧠 Intelligent Code Processing

The indexing pipeline:

* Filters supported source files.
* Excludes unnecessary directories.
* Skips binary files.
* Removes unwanted generated/build files.
* Splits large source files into smaller chunks.
* Preserves file and repository metadata.

## 🔢 Vector Embeddings

Each code chunk is converted into a numerical vector representation using an embedding model.

These vectors capture semantic information about the code and allow similar or relevant code to be retrieved later.

## 🗄️ PostgreSQL + pgvector

GitHub-GPT uses:

* **PostgreSQL** for repository metadata.
* **pgvector** for storing vector embeddings.
* Vector similarity search for retrieving relevant code.

## 📊 Indexing Progress

The application tracks:

```text
Total Files
Processed Files
Generated Chunks
Indexing Status
Index Completion Time
```

Example:

```text
Files Processed : 25 / 40
Chunks Generated: 137
Status          : INDEXING
Progress        : 62.5%
```

## 💬 Repository-Aware AI Chat

The indexed repository can be used as context for AI-powered conversations.

The intended flow is:

```text
User Question
      ↓
Semantic Search
      ↓
Relevant Code Chunks
      ↓
Repository Context
      ↓
AI Model
      ↓
Answer
```

---

# 🏗️ Technology Stack

| Technology              | Purpose                              |
| ----------------------- | ------------------------------------ |
| Java                    | Backend development                  |
| Spring Boot             | Application framework                |
| Spring REST             | REST API development                 |
| Spring Async / Executor | Background indexing                  |
| PostgreSQL              | Relational database                  |
| pgvector                | Vector storage and similarity search |
| GitHub REST API         | Repository and source-code access    |
| OpenAI Embeddings API   | Code embeddings                      |
| React                   | Frontend/dashboard                   |
| Vite                    | Frontend development/build tool      |
| Git                     | Version control                      |
| GitHub                  | Source-code hosting                  |

> The exact dependencies and versions depend on the current project configuration.

---

# 🏛️ System Architecture

GitHub-GPT follows an asynchronous repository-indexing architecture.

```text
                         Developer
                             |
                             v
                    React Dashboard
                             |
                             v
                       REST API
                             |
                             v
                    Repository Service
                             |
              +--------------+--------------+
              |                             |
              v                             v
        PostgreSQL                  Async Executor
                                             |
                                             v
                                      GitHub API
                                             |
                                             v
                                    GitHub Repository
                                             |
                                             v
                                      File Filtering
                                             |
                                             v
                                       Code Chunking
                                             |
                                             v
                                    Embedding Service
                                             |
                                             v
                                  OpenAI Embeddings
                                             |
                                             v
                                  PostgreSQL + pgvector
                                             |
                                             v
                                     Indexed Repository
                                             |
                                             v
                                      AI Code Chat
```

---

# 🔄 How GitHub-GPT Works

The system is divided into two major phases.

## Phase 1 — Indexing Request

When the user clicks the **Index Repository** button:

```text
Frontend
   ↓
POST /api/repos/{id}/index
   ↓
Repository Controller
   ↓
Validate Repository
   ↓
Create / Update Index Record
   ↓
Set Status = INDEXING
   ↓
Start Async Job
   ↓
Return API Response
```

The API does not wait for the entire repository to finish processing.

This keeps the application responsive.

---

# ⚙️ Phase 2 — Background Indexing

The background indexing process performs the following steps.

## 1. Remove Previous Vectors

Before re-indexing a repository, previously generated embeddings can be removed.

```text
Repository
    ↓
Delete Existing Embeddings
    ↓
Start Fresh Index
```

This prevents old embeddings from remaining alongside newly generated vectors.

---

## 2. Fetch Repository Tree

The GitHub API is used to retrieve the repository file tree.

Example:

```text
src/
 ├── main/
 │    ├── java/
 │    │    ├── controller/
 │    │    ├── service/
 │    │    └── repository/
 │    │
 │    └── resources/
 │         └── application.yml
```

The file tree helps determine which files should be processed.

---

## 3. Filter Files

GitHub-GPT filters files before processing them.

Example excluded content:

```text
node_modules/
.git/
target/
build/
dist/
binary files
generated files
lock files
unsupported extensions
```

The exact filtering rules depend on the implementation.

---

## 4. Retrieve File Contents

For every supported source file:

```text
GitHub Repository
       ↓
File Path
       ↓
GitHub API
       ↓
File Content
```

The indexing worker then processes the retrieved source code.

---

## 5. Code Chunking

Large source files are divided into smaller pieces.

For example:

```java
public class UserService {

    public User createUser(...) {
        ...
    }

    public User getUser(...) {
        ...
    }

    public void deleteUser(...) {
        ...
    }
}
```

Instead of embedding the entire file as one large document, the source can be divided into smaller chunks.

Each chunk can contain metadata such as:

```text
Repository ID
File Path
Chunk Index
Source Code
```

This makes semantic retrieval more focused.

---

# 🧠 Embedding Generation

Each code chunk is sent to the configured embedding model.

```text
Code Chunk
    ↓
Embedding API
    ↓
Vector Representation
    ↓
PostgreSQL + pgvector
```

Conceptually:

```text
Java Code
   ↓
[0.021, -0.182, 0.734, ...]
```

The vector represents the semantic characteristics of the code.

---

# 🔎 Semantic Search

When a developer asks a question:

```text
How does authentication work?
```

The question can be converted into an embedding and compared with stored code embeddings.

```text
User Question
      ↓
Question Embedding
      ↓
Vector Similarity Search
      ↓
Relevant Code Chunks
      ↓
Repository Context
      ↓
AI Model
      ↓
Natural Language Answer
```

This is the core Retrieval-Augmented Generation workflow.

---

# 📊 Indexing Status Lifecycle

A repository can move through the following states:

```text
              ┌──────────────┐
              │ NOT_INDEXED  │
              └──────┬───────┘
                     │
                     v
              ┌──────────────┐
              │   INDEXING   │
              └──────┬───────┘
                     │
             ┌───────┴────────┐
             │                │
             v                v
       ┌──────────┐     ┌──────────┐
       │  READY   │     │  FAILED  │
       └──────────┘     └──────────┘
```

| Status        | Description                                 |
| ------------- | ------------------------------------------- |
| `NOT_INDEXED` | Repository has not been indexed             |
| `INDEXING`    | Repository indexing is currently running    |
| `READY`       | Repository indexing completed successfully  |
| `FAILED`      | Indexing encountered an unrecoverable error |

A failed repository can be indexed again after resolving the issue.

---

# 📈 Progress Tracking

The backend stores indexing progress.

Example API response:

```json
{
  "repoId": 1,
  "status": "INDEXING",
  "filesTotal": 10,
  "filesProcessed": 5,
  "chunkCount": 16,
  "indexedAt": null
}
```

The frontend can display:

```text
Indexing repository...

Files processed: 5 / 10
Chunks generated: 16

Progress: 50%
```

The frontend can periodically request the repository status until the indexing process reaches:

```text
READY
```

or

```text
FAILED
```

---

# 🗃️ Database Design

GitHub-GPT uses PostgreSQL for application and repository metadata and pgvector for code embeddings.

## Repositories

Stores repository and indexing information.

| Column            | Description                |
| ----------------- | -------------------------- |
| `id`              | Repository record ID       |
| `user_id`         | Repository owner/user      |
| `repo_name`       | GitHub repository name     |
| `repo_url`        | Repository URL             |
| `status`          | Current indexing status    |
| `files_total`     | Total eligible files       |
| `files_processed` | Processed files            |
| `chunk_count`     | Generated code chunks      |
| `indexed_at`      | Index completion timestamp |
| `error_message`   | Error details              |

## Code Embeddings

Stores the actual code chunks and vector embeddings.

| Column        | Description           |
| ------------- | --------------------- |
| `id`          | Embedding record ID   |
| `repo_id`     | Associated repository |
| `file_path`   | Original file path    |
| `chunk_index` | Position of the chunk |
| `content`     | Source-code chunk     |
| `embedding`   | Vector representation |

The vector column is provided by the PostgreSQL `pgvector` extension.

The exact vector dimension depends on the configured embedding model.

---

# 🔌 API Endpoints

## Start Repository Indexing

```http
POST /api/repos/{id}/index
```

Starts the repository indexing process.

Example response:

```json
{
  "repoId": 1,
  "status": "INDEXING",
  "message": "Repository indexing started"
}
```

---

## Get Repository Indexing Status

```http
GET /api/repos/{id}/status
```

Returns the current indexing status and progress.

Example:

```json
{
  "repoId": 1,
  "status": "READY",
  "filesTotal": 3,
  "filesProcessed": 3,
  "chunkCount": 8,
  "indexedAt": "2026-09-28T10:30:00"
}
```

> The exact endpoints and response structures should match the controllers and DTOs implemented in the project.

---

# 📁 Project Structure

A typical backend structure is:

```text
github-gpt/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── ...
│   │
│   └── test/
│
├── frontend/
│   └── ...
│
├── pom.xml
├── README.md
└── .gitignore
```

The exact package and directory structure may vary depending on the current implementation.

---

# 🚀 Getting Started

## Prerequisites

Make sure the following are installed:

* Java JDK compatible with the project
* Maven
* PostgreSQL
* Git
* GitHub account
* GitHub access token
* OpenAI API key
* Node.js and npm if the frontend is included

---

# 1. Clone the Repository

```bash
git clone https://github.com/YogeshGangji/github-gpt.git
```

Move into the project:

```bash
cd github-gpt
```

---

# 2. Configure PostgreSQL

Create the database:

```sql
CREATE DATABASE devpilot;
```

Connect to the database and enable pgvector:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

Make sure the configured PostgreSQL user has permission to access the database and extension.

---

# 3. Configure Environment Variables

Configure the required environment variables.

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/devpilot
SPRING_DATASOURCE_USERNAME=your_database_username
SPRING_DATASOURCE_PASSWORD=your_database_password

GITHUB_TOKEN=your_github_token

OPENAI_API_KEY=your_openai_api_key
```

Use environment variables or an untracked local configuration file.

### ⚠️ Important

Never commit the following to GitHub:

```text
API keys
GitHub tokens
Database passwords
Private credentials
Secrets
```

Add sensitive configuration files to `.gitignore`.

---

# 4. Run the Backend

Using Maven:

```bash
mvn clean install
```

Then:

```bash
mvn spring-boot:run
```

Or run the generated JAR:

```bash
java -jar target/github-gpt.jar
```

The exact JAR name depends on the Maven configuration.

---

# 5. Run the Frontend

If the project contains a React/Vite frontend:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

Open the local URL provided by Vite.

---

# 🔐 Security Considerations

GitHub-GPT handles repository source code and external API credentials, so security is important.

Recommended practices include:

* Validate authenticated users before accessing repositories.
* Verify repository ownership/access.
* Keep GitHub tokens on the backend.
* Keep OpenAI API keys on the backend.
* Never expose API keys to the frontend.
* Never commit secrets to GitHub.
* Avoid logging GitHub access tokens.
* Avoid unnecessarily logging private repository source code.
* Validate repository IDs.
* Restrict database access.
* Prevent unauthorized users from accessing indexed repository content.

---

# 🔮 Future Enhancements

Planned improvements can include:

### 🤖 AI Repository Chat

Allow developers to directly ask questions about indexed repositories.

### 🔎 Semantic Code Search

Search repository code using natural language.

Example:

```text
Find where JWT authentication is implemented.
```

### 📍 Code Citations

Return answers with:

```text
File: UserService.java
Lines: 45-72
```

### 🔄 Incremental Indexing

Instead of indexing the entire repository every time, index only changed files using GitHub webhooks.

### 🌐 Multiple Programming Languages

Support:

```text
Java
C++
Python
JavaScript
TypeScript
Go
C#
etc.
```

### 🔁 Retry Mechanism

Automatically retry failed indexing operations.

### 🛑 Index Cancellation

Allow users to cancel a running indexing job.

### 🐳 Docker Deployment

Containerize:

```text
Backend
Frontend
PostgreSQL
```

### 📦 Multi-Repository Workspace

Allow developers to manage multiple repositories inside a single workspace.

### 🔗 Dependency Analysis

Analyze relationships between:

```text
Classes
Services
Controllers
Repositories
Modules
Dependencies
```

---

# 🧩 RAG Architecture

The planned AI question-answering architecture follows:

```text
                 User Question
                      |
                      v
                Query Embedding
                      |
                      v
             Vector Similarity Search
                      |
                      v
              Relevant Code Chunks
                      |
                      v
             Repository Context
                      |
                      v
                  AI Model
                      |
                      v
              Generated Answer
```

This approach helps the AI model answer questions using repository-specific context rather than relying only on its general training knowledge.

---

# 🎯 Project Goal

The main goal of GitHub-GPT is to make software repositories easier to understand.

Instead of manually navigating through a large codebase:

```text
Developer
    |
    | Natural Language Question
    v
GitHub-GPT
    |
    +--> Semantic Search
    |
    +--> Relevant Source Code
    |
    +--> Repository Context
    |
    v
AI-Generated Explanation
```

GitHub-GPT provides the foundation for an AI-powered developer assistant capable of understanding repository structure, source code, and project-specific logic.

---

# 👨‍💻 Author

**Yogesh Gangji**

Java | Spring Boot | PostgreSQL | pgvector | GitHub API | AI/RAG

---

# ⭐ Project

GitHub Repository:

```text
https://github.com/YogeshGangji/github-gpt
```

If you find the project useful, consider giving the repository a ⭐ on GitHub.
