CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ========== users ==========
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'PHARMACIST')),
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE
);

-- ========== categories ==========
CREATE TABLE categories (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- ========== medicines ==========
CREATE TABLE medicines (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(150)   NOT NULL,
    batch_number   VARCHAR(50),
    category_id    BIGINT         NOT NULL REFERENCES categories(id),
    quantity       INTEGER        NOT NULL,
    reorder_level  INTEGER        NOT NULL,
    expiry_date    DATE           NOT NULL,
    price          NUMERIC(10,2),
    supplier       VARCHAR(150),
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

-- ========== suppliers ==========
CREATE TABLE suppliers (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email          VARCHAR(255) NOT NULL UNIQUE,
    phone          VARCHAR(20),
    address        VARCHAR(255),
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP
);

-- ========== purchase_orders ==========
CREATE TABLE purchase_orders (
    id                     BIGSERIAL PRIMARY KEY,
    supplier_id            BIGINT        NOT NULL REFERENCES suppliers(id),
    order_date             DATE          NOT NULL,
    expected_delivery_date DATE,
    status                 VARCHAR(20)   NOT NULL
        CHECK (status IN ('PENDING', 'APPROVED', 'DELIVERED', 'CANCELLED')),
    total_amount           NUMERIC(19,2) NOT NULL,
    created_at             TIMESTAMP
);

-- ========== purchase_order_items ==========
CREATE TABLE purchase_order_items (
    id                BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT        NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    medicine_id       BIGINT        NOT NULL REFERENCES medicines(id),
    quantity          INTEGER       NOT NULL,
    unit_price        NUMERIC(19,2) NOT NULL
);

-- ========== prescriptions ==========
CREATE TABLE prescriptions (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_name  VARCHAR(255) NOT NULL,
    doctor_name   VARCHAR(255) NOT NULL,
    date_issued   DATE         NOT NULL,
    pharmacist_id UUID         NOT NULL REFERENCES users(id),
    created_at    TIMESTAMP    NOT NULL
);

-- ========== prescription_items ==========
CREATE TABLE prescription_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id     UUID    NOT NULL REFERENCES prescriptions(id) ON DELETE CASCADE,
    medicine_id         BIGINT  NOT NULL REFERENCES medicines(id),
    quantity            INTEGER NOT NULL,
    dosage_instructions VARCHAR(255)
);

-- ========== sales ==========
CREATE TABLE sales (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_date     TIMESTAMP     NOT NULL,
    pharmacist_id UUID          NOT NULL REFERENCES users(id),
    total_amount  NUMERIC(19,2) NOT NULL
);

-- ========== sale_items ==========
CREATE TABLE sale_items (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_id     UUID          NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    medicine_id BIGINT        NOT NULL REFERENCES medicines(id),
    quantity    INTEGER       NOT NULL,
    unit_price  NUMERIC(19,2) NOT NULL
);

-- ========== stock_alerts ==========
CREATE TABLE stock_alerts (
    id          BIGSERIAL PRIMARY KEY,
    medicine_id BIGINT       NOT NULL REFERENCES medicines(id),
    alert_type  VARCHAR(30)  NOT NULL CHECK (alert_type IN ('LOW_STOCK', 'NEAR_EXPIRY')),
    message     VARCHAR(255) NOT NULL,
    resolved    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP,
    resolved_at TIMESTAMP
);