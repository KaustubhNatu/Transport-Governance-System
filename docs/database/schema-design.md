# Transport Governance System

## Database Schema Design

### Overview

The Transport Governance System database manages school transport operations where schools rely on independent transport vendors.

The database supports student transport enrollment, vendor management, fee tracking, transfer workflows, compliance monitoring, and audit logging.

The system uses PostgreSQL with a relational schema designed to maintain data consistency, traceability, and clear relationships between entities.

---

### Core Entity Groups

The database is organized into the following logical modules.

| Module            | Purpose                              |
| ----------------- | ------------------------------------ |
| Identity & Access | Users and roles for RBAC             |
| School & Vendors  | Schools and transport vendors        |
| Students          | Students and parents                 |
| Transport         | Vehicles, drivers, and routes        |
| Enrollment        | Student transport assignments        |
| Finance           | Invoices and payments                |
| Transfers         | Student vendor transfer workflow     |
| Compliance        | Vehicle and driver document tracking |
| Audit             | System activity logs                 |

---

### Key Relationships

The main relationships in the database are:

* A **school** can work with multiple **transport vendors**
* A **vendor** manages **vehicles**, **drivers**, and **routes**
* A **student** belongs to a **school** and is linked to a **parent**
* A **transport enrollment** connects a student to a vendor route
* **Invoices** are generated for students using transport services
* **Payments** are recorded against invoices
* **transfer_requests** track movement of students between vendors

---

### Important Tables

| Table             | Description                                                |
| ----------------- | ---------------------------------------------------------- |
| users             | System users such as admins, vendors, drivers, and parents |
| roles             | RBAC roles                                                 |
| schools           | Schools using the system                                   |
| vendors           | Transport providers                                        |
| students          | School students                                            |
| vehicles          | Vendor vehicles                                            |
| drivers           | Vehicle drivers                                            |
| routes            | Transport routes                                           |
| student_transport | Student transport enrollment                               |
| invoices          | Monthly transport invoices                                 |
| payments          | Payment records                                            |
| transfer_requests | Student vendor transfer workflow                           |
| vehicle_documents | Vehicle compliance documents                               |
| driver_documents  | Driver compliance documents                                |
| audit_logs        | System activity history                                    |

---

### Transfer Workflow States

The student transfer process follows these states:

TRANSFER_INITIATED--
VENDOR_NOTIFIED--
DUES_REPORTED--
TRANSFER_COMPLETED--

---

### Audit Logging

The system records important changes using audit logs.

These logs track which user performed an action, what entity was modified, and when the change occurred.

This ensures transparency and traceability across financial and administrative operations.

---

### Future Extensions

Possible system extensions include:

* GPS vehicle tracking
* Automated transport attendance
* Payment gateway integration
* Vendor performance analytics
