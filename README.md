# Transport Governance System

An enterprise-style backend platform designed to manage school transport vendors, compliance, financial reconciliation, and transfer workflows.

## Key Features

- Role-Based Access Control (RBAC)
- Vendor–School transport governance
- Invoice and payment reconciliation
- Transfer workflow with state machine
- Compliance tracking (RTO, insurance, licenses)
- Audit logging for traceability
- Event-driven workflows

## Tech Stack

Backend
- Java
- Spring Boot
- PostgreSQL
- JWT Authentication

Frontend
- React

Infrastructure
- Docker
- AWS Deployment
- CI/CD

## Architecture

Modular Monolith Architecture with domain modules:

- Authentication
- Students
- Vendors
- Routes
- Vehicles
- Invoices
- Transfers
- Compliance
- Audit Logging

## System Goals

- Transparency in transport fee management
- Vendor compliance monitoring
- Transfer dispute traceability
- Operational visibility for schools
