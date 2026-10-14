CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN'))
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE, -- UUID Is already long, so we'll hash it using light algorithm
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE -- We won't delete Refresh Token immediately due to security logs
    -- we'll revoke and schedule will delete revoked tokens every defined time
);

-- Fast token lookups during refresh requests
CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_tokens_hash ON refresh_tokens(token_hash);

-- Fast user lookups (e.g., revoking all tokens for a user upon password reset)
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Speeds up scheduled cleanup queries for expired or revoked tokens
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_cleanup ON refresh_tokens(expires_at, revoked);

--
-- 1. Fleet Metadata
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    vehicle_code VARCHAR(50) NOT NULL UNIQUE, -- e.g. "MY-CAR-01"
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    model VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vehicles_user_id ON vehicles(user_id);
-- 2. Real-Time Telemetry & Position History
CREATE TABLE IF NOT EXISTS telemetry_logs (
    id BIGSERIAL PRIMARY KEY,
    vehicle_code VARCHAR(50) NOT NULL REFERENCES vehicles(vehicle_code) ON DELETE CASCADE,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    speed DOUBLE PRECISION NOT NULL, -- Speed in km/h
    fuel_level DOUBLE PRECISION NOT NULL, -- Fuel percentage (0 - 100%)
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast time-series lookup per vehicle
CREATE INDEX IF NOT EXISTS idx_telemetry_vehicle_time
ON telemetry_logs (vehicle_code, recorded_at DESC);

