-- =============================================
-- V1__init_schema.sql
-- Create schema, enum types, and all tables
-- =============================================

CREATE SCHEMA IF NOT EXISTS app;

-- ═══ ENUM TYPES ═══

CREATE TYPE app.user_role AS ENUM ('PATIENT', 'DOCTOR', 'ADMIN');
CREATE TYPE app.doctor_status AS ENUM ('PENDING_APPROVAL', 'APPROVED', 'REJECTED');
CREATE TYPE app.slot_status AS ENUM ('AVAILABLE', 'BOOKED', 'BLOCKED');
CREATE TYPE app.booking_status AS ENUM ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW', 'EXPIRED');
CREATE TYPE app.gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE app.cancelled_by AS ENUM ('PATIENT', 'DOCTOR', 'SYSTEM');
CREATE TYPE app.otp_type AS ENUM ('VERIFY_EMAIL', 'RESET_PASSWORD');

-- ═══ USERS ═══

CREATE TABLE app.users
(
    id          UUID PRIMARY KEY       DEFAULT gen_random_uuid(),
    email       VARCHAR(255)  NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    full_name   VARCHAR(100)  NOT NULL,
    phone       VARCHAR(15),
    role        app.user_role NOT NULL DEFAULT 'PATIENT',
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    is_verified BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON app.users (email);
CREATE INDEX idx_users_role ON app.users (role);

-- ═══ SPECIALTIES ═══

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

-- ═══ CLINICS ═══

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

-- ═══ DOCTOR PROFILES ═══

CREATE TABLE app.doctor_profiles
(
    id               UUID PRIMARY KEY           DEFAULT gen_random_uuid(),
    user_id          UUID              NOT NULL UNIQUE REFERENCES app.users (id),
    specialty_id     INT               NOT NULL REFERENCES app.specialties (id),
    clinic_id        INT REFERENCES app.clinics (id),
    title            VARCHAR(50),
    description      TEXT,
    experience_years INT               NOT NULL DEFAULT 0 CHECK (experience_years >= 0),
    consultation_fee DECIMAL(10, 0)    NOT NULL DEFAULT 0 CHECK (consultation_fee >= 0),
    status           app.doctor_status NOT NULL DEFAULT 'PENDING_APPROVAL',
    created_at       TIMESTAMP         NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP         NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_doctor_profiles_specialty ON app.doctor_profiles (specialty_id) WHERE status = 'APPROVED';
CREATE INDEX idx_doctor_profiles_clinic ON app.doctor_profiles (clinic_id) WHERE status = 'APPROVED';
CREATE INDEX idx_doctor_profiles_status ON app.doctor_profiles (status);

-- ═══ WORKING SCHEDULES ═══

CREATE TABLE app.working_schedules
(
    id                    SERIAL PRIMARY KEY,
    doctor_id             UUID        NOT NULL REFERENCES app.doctor_profiles (id),
    day_of_week           VARCHAR(10) NOT NULL CHECK (day_of_week IN
                                                      ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY',
                                                       'SATURDAY', 'SUNDAY')),
    start_time            TIME        NOT NULL,
    end_time              TIME        NOT NULL,
    slot_duration_minutes INT         NOT NULL DEFAULT 30 CHECK (slot_duration_minutes > 0),
    is_active             BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP   NOT NULL DEFAULT NOW(),
    UNIQUE (doctor_id, day_of_week),
    CHECK (start_time < end_time)
);

-- ═══ TIME SLOTS ═══

CREATE TABLE app.time_slots
(
    id         BIGSERIAL PRIMARY KEY,
    doctor_id  UUID            NOT NULL REFERENCES app.doctor_profiles (id),
    date       DATE            NOT NULL,
    start_time TIME            NOT NULL,
    end_time   TIME            NOT NULL,
    status     app.slot_status NOT NULL DEFAULT 'AVAILABLE',
    version    INT             NOT NULL DEFAULT 0,
    created_at TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE (doctor_id, date, start_time),
    CHECK (start_time < end_time)
);

CREATE INDEX idx_time_slots_doctor_date ON app.time_slots (doctor_id, date) WHERE status = 'AVAILABLE';
CREATE INDEX idx_time_slots_date_status ON app.time_slots (date, status);

-- ═══ BOOKINGS ═══

CREATE TABLE app.bookings
(
    id               UUID PRIMARY KEY            DEFAULT gen_random_uuid(),
    patient_id       UUID               NOT NULL REFERENCES app.users (id),
    doctor_id        UUID               NOT NULL REFERENCES app.doctor_profiles (id),
    time_slot_id     BIGINT             NOT NULL REFERENCES app.time_slots (id),
    booking_code     VARCHAR(20)        NOT NULL UNIQUE,
    status           app.booking_status NOT NULL DEFAULT 'PENDING',
    patient_name     VARCHAR(100)       NOT NULL,
    patient_phone    VARCHAR(15)        NOT NULL,
    patient_gender   app.gender,
    patient_dob      DATE,
    patient_address  VARCHAR(500),
    symptoms         TEXT,
    reason           TEXT,
    note             TEXT,
    cancelled_by     app.cancelled_by,
    cancelled_reason TEXT,
    created_at       TIMESTAMP          NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP          NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bookings_patient ON app.bookings (patient_id, created_at DESC);
CREATE INDEX idx_bookings_doctor ON app.bookings (doctor_id, created_at DESC);
CREATE INDEX idx_bookings_status ON app.bookings (status) WHERE status IN ('PENDING', 'CONFIRMED');
CREATE UNIQUE INDEX idx_bookings_active_slot ON app.bookings (time_slot_id) WHERE status NOT IN ('CANCELLED', 'EXPIRED');

-- ═══ REFRESH TOKENS ═══

CREATE TABLE app.refresh_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES app.users (id),
    token      VARCHAR(500) NOT NULL UNIQUE,
    expires_at TIMESTAMP    NOT NULL,
    is_revoked BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_token ON app.refresh_tokens (token) WHERE is_revoked = FALSE;

-- ═══ OTP TOKENS ═══

CREATE TABLE app.otp_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES app.users (id),
    otp_code   VARCHAR(10)  NOT NULL,
    type       app.otp_type NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    is_used    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);