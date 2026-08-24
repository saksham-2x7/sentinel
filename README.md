# 🛡️ Sentinel — On-Device AI Code Security Scanner

> **iQOO Hackathon 2026 | Track 06: Developer Tools**

Sentinel is a mobile-native, fully on-device AI code security scanner built for the iQOO platform. It detects vulnerabilities in seconds using Phi-3 Mini running on the Snapdragon NPU — with zero internet, zero cloud, and zero data egress.

## Features
- 🔍 Detects SQL Injection, XSS, Hardcoded Secrets, Broken Auth, Insecure Comms & more
- ⚡ ~9 second scan on Snapdragon NPU
- 🔒 100% on-device — code never leaves your phone
- 📁 iQOO Office Kit file picker integration
- 📊 Severity-ranked findings with line numbers and fix suggestions
- 🕓 Scan history with security score tracking

## Tech Stack
- **Language**: Kotlin + Jetpack Compose
- **AI Model**: Phi-3 Mini 4K Instruct (INT4 quantized, 3.8B params)
- **AI Runtime**: MediaPipe LLM Inference API (Snapdragon NPU)
- **Database**: Room DB
- **Integration**: iQOO Office Kit

## Team
| Name | Role |
|------|------|
| Naman Raghav | Team Lead, Android Dev, Debugging |
| Chezhil | AI/ML, Complex Problem Solving |
| Ansh | Product, UI/UX, Prompt Engineering |

## Tagline
> *Your code. Your phone. Your rules.*
