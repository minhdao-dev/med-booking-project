-- =============================================
-- V1: Create schema + specialties + clinics
-- =============================================

CREATE SCHEMA IF NOT EXISTS app;

-- ═══ SPECIALTIES (Chuyên khoa) ═══

CREATE TABLE app.specialties
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    slug        VARCHAR(120) NOT NULL UNIQUE,
    description TEXT,
    image_url   VARCHAR(500),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ═══ CLINICS (Phòng khám / Bệnh viện) ═══

CREATE TABLE app.clinics
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    address     VARCHAR(500) NOT NULL,
    description TEXT,
    image_url   VARCHAR(500),
    phone       VARCHAR(15),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);