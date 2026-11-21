-- Telecom BSS: Subscription Lifecycle + Billing (postpaid mobile)
-- MySQL 8+. Flyway location: src/main/resources/db/migration/
-- Create the database manually first:
--   CREATE DATABASE telecom_bss CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN','CSR','CUSTOMER') NOT NULL,
    enabled       TINYINT(1) NOT NULL DEFAULT 1,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE customers (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE,
    full_name         VARCHAR(120) NOT NULL,
    date_of_birth     DATE NOT NULL,
    address           VARCHAR(255),
    city              VARCHAR(80),
    kyc_status        ENUM('PENDING','VERIFIED','REJECTED') NOT NULL DEFAULT 'PENDING',
    kyc_document_type VARCHAR(30),
    kyc_document_no   VARCHAR(40),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE TABLE sim_cards (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    msisdn VARCHAR(15) NOT NULL UNIQUE,   -- the phone number
    iccid  VARCHAR(22) NOT NULL UNIQUE,   -- the SIM serial
    status ENUM('AVAILABLE','ASSIGNED','BLOCKED') NOT NULL DEFAULT 'AVAILABLE'
) ENGINE=InnoDB;

CREATE TABLE plans (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(80) NOT NULL UNIQUE,
    description         VARCHAR(255),
    monthly_fee         DECIMAL(10,2) NOT NULL,
    data_mb             INT NOT NULL,
    voice_minutes       INT NOT NULL,
    sms_count           INT NOT NULL,
    overage_data_per_mb DECIMAL(8,4) NOT NULL,
    overage_voice_per_min DECIMAL(8,4) NOT NULL,
    overage_sms_each    DECIMAL(8,4) NOT NULL,
    active              TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE addons (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                VARCHAR(80) NOT NULL UNIQUE,
    description         VARCHAR(255),
    price               DECIMAL(10,2) NOT NULL,
    extra_data_mb       INT NOT NULL DEFAULT 0,
    extra_voice_minutes INT NOT NULL DEFAULT 0,
    extra_sms           INT NOT NULL DEFAULT 0,
    active              TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE subscriptions (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    plan_id       BIGINT NOT NULL,
    sim_id        BIGINT NOT NULL,
    status        ENUM('PENDING','ACTIVE','SUSPENDED','TERMINATED') NOT NULL DEFAULT 'PENDING',
    bill_day      TINYINT NOT NULL,           -- day of month the cycle starts (1-28)
    activated_at  DATETIME NULL,
    terminated_at DATETIME NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sub_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_sub_plan     FOREIGN KEY (plan_id)     REFERENCES plans(id),
    CONSTRAINT fk_sub_sim      FOREIGN KEY (sim_id)      REFERENCES sim_cards(id),
    INDEX idx_sub_customer (customer_id),
    INDEX idx_sub_status (status)
) ENGINE=InnoDB;

CREATE TABLE subscription_addons (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    addon_id        BIGINT NOT NULL,
    added_on        DATE NOT NULL,
    removed_on      DATE NULL,
    CONSTRAINT fk_sa_sub   FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    CONSTRAINT fk_sa_addon FOREIGN KEY (addon_id)        REFERENCES addons(id)
) ENGINE=InnoDB;

CREATE TABLE subscription_status_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    from_status     VARCHAR(20) NULL,
    to_status       VARCHAR(20) NOT NULL,
    reason          VARCHAR(255),
    changed_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hist_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
) ENGINE=InnoDB;

CREATE TABLE usage_records (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    usage_type      ENUM('VOICE','SMS','DATA') NOT NULL,
    quantity        DECIMAL(12,2) NOT NULL,   -- minutes / count / MB
    used_at         DATETIME NOT NULL,
    CONSTRAINT fk_usage_sub FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    INDEX idx_usage_sub_time (subscription_id, used_at)
) ENGINE=InnoDB;

CREATE TABLE invoices (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_no      VARCHAR(30) NOT NULL UNIQUE,
    subscription_id BIGINT NOT NULL,
    customer_id     BIGINT NOT NULL,
    period_start    DATE NOT NULL,
    period_end      DATE NOT NULL,
    plan_charge     DECIMAL(10,2) NOT NULL DEFAULT 0,
    addon_charge    DECIMAL(10,2) NOT NULL DEFAULT 0,
    overage_charge  DECIMAL(10,2) NOT NULL DEFAULT 0,
    tax_amount      DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount    DECIMAL(10,2) NOT NULL,
    status          ENUM('GENERATED','PAID','OVERDUE','CANCELLED') NOT NULL DEFAULT 'GENERATED',
    issued_on       DATE NOT NULL,
    due_date        DATE NOT NULL,
    CONSTRAINT fk_inv_sub  FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    CONSTRAINT fk_inv_cust FOREIGN KEY (customer_id)     REFERENCES customers(id),
    -- one invoice per subscription per cycle: makes the billing job idempotent
    UNIQUE KEY uq_invoice_cycle (subscription_id, period_start)
) ENGINE=InnoDB;

CREATE TABLE invoice_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id  BIGINT NOT NULL,
    item_type   ENUM('PLAN','ADDON','OVERAGE','TAX') NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount      DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_inv FOREIGN KEY (invoice_id) REFERENCES invoices(id)
) ENGINE=InnoDB;

CREATE TABLE payments (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount     DECIMAL(10,2) NOT NULL,
    method     ENUM('UPI','CARD','NETBANKING') NOT NULL,
    status     ENUM('SUCCESS','FAILED','PENDING') NOT NULL,
    txn_ref    VARCHAR(50) NOT NULL UNIQUE,
    paid_at    DATETIME NOT NULL,
    CONSTRAINT fk_pay_inv FOREIGN KEY (invoice_id) REFERENCES invoices(id)
) ENGINE=InnoDB;

CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    channel     ENUM('EMAIL','SMS') NOT NULL,
    subject     VARCHAR(150),
    message     VARCHAR(500) NOT NULL,
    status      ENUM('SENT','FAILED') NOT NULL DEFAULT 'SENT',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notif_cust FOREIGN KEY (customer_id) REFERENCES customers(id)
) ENGINE=InnoDB;