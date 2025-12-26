---
description: Build and Run Workflow (Turbo Mode)
---

// turbo-all

1. Clean the project
```bash
./gradlew clean
```

2. Build the backend
```bash
./gradlew :backend:build
```

3. Build the frontend
```bash
./gradlew :frontend:compileKotlinWasmJs
```

4. Run the backend
```bash
./gradlew :backend:bootRun
```

5. Run the frontend in dev mode
```bash
./gradlew :frontend:wasmJsBrowserDevelopmentRun
```
