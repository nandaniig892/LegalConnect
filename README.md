# 🏥 Rural Healthcare Accessibility Platform

A full-stack digital health platform designed to bridge the healthcare gap in rural India — connecting patients, doctors, pharmacies, and administrators through an intelligent, role-based web application.

---

## 🚀 Live Demo

> **Frontend (Netlify):** *(will be updated after first deploy)*
> **Backend (Spring Boot):** Configure via `application.yml`

---

## 🎭 Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| **Admin** | admin@ruralhealthcare.com | Admin@123 |
| **Doctor** | doctor@ruralhealthcare.com | Doctor@123 |
| **Pharmacy** | pharmacy@ruralhealthcare.com | Pharmacy@123 |
| **Patient** | patient@ruralhealthcare.com | Patient@123 |

> **Note:** Doctor and Pharmacy accounts require Admin approval before login is possible.

---

## ✨ Features

### 🧑‍⚕️ Patient
- **AI Symptom Checker** — Enter symptoms and receive AI-powered triage assessment
  > ⚠️ *LEGAL_NOTICE: This is not a medical diagnosis. Please consult a physical health practitioner immediately in emergencies.*
- **Appointment Slot Booking** — Browse available doctor slots and book consultations
- **Offline Records Sync** — View cached health records even without internet
- **Nearby Pharmacies** — Map-based search for pharmacies in rural areas

### 👨‍⚕️ Doctor
- **Appointment Queue** — View today's scheduled patients in real time
- **Slot Management** — Create and manage available time slots
- **E-Prescription Writer** — Generate digital prescriptions linked to appointments

### 💊 Pharmacy
- **Inventory Management** — Track medicine stock with low-stock alerts
- **Status Badges** — Real-time IN_STOCK / LOW_STOCK / OUT_OF_STOCK indicators

### 🔑 Admin
- **Platform Statistics** — Live counts of users, appointments, and prescriptions
- **Doctor & Pharmacy Approvals** — One-click approve/reject workflow
- **User Audit Logs** — Track registrations and role changes

---

## 🏗️ Tech Stack

### Frontend
| Technology | Purpose |
|---|---|
| **React 18** | UI Framework |
| **Vite 5** | Build Tool |
| **Tailwind CSS 3** | Styling |
| **React Router 6** | Client-side Routing |
| **Axios** | HTTP Client with JWT Interceptors |
| **Lucide React** | Icon Library |

### Backend
| Technology | Purpose |
|---|---|
| **Spring Boot 3.4.5** | REST API Framework |
| **Spring Security + JWT** | Authentication & Authorization |
| **Spring Data JPA** | Database ORM |
| **MySQL** | Relational Database |
| **Maven** | Build Tool |

---

## 📁 Project Structure

```
Rural_healthcare_Platform/
├── netlify.toml              # Netlify build config
├── .gitignore
├── README.md
├── frontend/                 # React + Vite SPA
│   ├── src/
│   │   ├── App.jsx           # Root component + Auth/Theme context
│   │   ├── main.jsx
│   │   ├── index.css         # Global styles (Tailwind + custom)
│   │   ├── components/
│   │   │   └── Sidebar.jsx   # Role-based navigation
│   │   ├── pages/
│   │   │   ├── Login.jsx
│   │   │   ├── Register.jsx
│   │   │   ├── PatientDashboard.jsx
│   │   │   ├── DoctorDashboard.jsx
│   │   │   ├── PharmacyDashboard.jsx
│   │   │   └── AdminDashboard.jsx
│   │   └── utils/
│   │       └── api.js        # Axios client with token refresh
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   └── netlify.toml
└── backend/                  # Spring Boot API
    └── src/main/java/com/ruralhealthcare/
        ├── controller/
        ├── service/
        ├── model/
        ├── repository/
        └── config/
```

---

## ⚙️ Local Development Setup

### Prerequisites
- Node.js 20+
- Java 21+
- MySQL 8+
- Maven 3.9+

### Backend
```bash
cd backend
# Configure application.yml with your DB credentials
mvn spring-boot:run
# API runs on http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# App runs on http://localhost:5173
# Proxies /api/* to backend automatically
```

---

## 🔐 Security

- JWT-based stateless authentication with Access + Refresh token rotation
- Role-based access control: `ROLE_PATIENT`, `ROLE_DOCTOR`, `ROLE_PHARMACY`, `ROLE_ADMIN`
- Doctor and Pharmacy accounts are locked until Admin approves them
- All protected API routes require `Authorization: Bearer <token>` header

---

## 📄 License

MIT License — feel free to use, modify and distribute.
