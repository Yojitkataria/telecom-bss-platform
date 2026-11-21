-- Dummy data for Telecom BSS (dev only)
-- password_hash values are PLACEHOLDERS, not real bcrypt hashes.
-- When you build auth, generate real ones with BCryptPasswordEncoder.

INSERT INTO users (id, email, password_hash, role) VALUES
(1, 'admin@telco.com',  '$2a$10$placeholderplaceholderplaceholderplaceholderplaceholde', 'ADMIN'),
(2, 'rahul@mail.com',   '$2a$10$placeholderplaceholderplaceholderplaceholderplaceholde', 'CUSTOMER'),
(3, 'priya@mail.com',   '$2a$10$placeholderplaceholderplaceholderplaceholderplaceholde', 'CUSTOMER'),
(4, 'arjun@mail.com',   '$2a$10$placeholderplaceholderplaceholderplaceholderplaceholde', 'CUSTOMER'),
(5, 'sneha@mail.com',   '$2a$10$placeholderplaceholderplaceholderplaceholderplaceholde', 'CUSTOMER');

INSERT INTO customers (id, user_id, full_name, date_of_birth, address, city, kyc_status, kyc_document_type, kyc_document_no) VALUES
(1, 2, 'Rahul Sharma', '1995-03-12', '12 MG Road',        'Bengaluru', 'VERIFIED', 'AADHAAR', 'XXXX-XXXX-1111'),
(2, 3, 'Priya Nair',   '1998-07-25', '45 Lake View',      'Kochi',     'VERIFIED', 'PAN',     'ABCDE1234F'),
(3, 4, 'Arjun Rao',    '1990-11-02', '7 Temple Street',   'Mysuru',    'VERIFIED', 'AADHAAR', 'XXXX-XXXX-3333'),
(4, 5, 'Sneha Iyer',   '2000-01-30', '88 Park Avenue',    'Chennai',   'PENDING',  'AADHAAR', 'XXXX-XXXX-4444');

INSERT INTO sim_cards (id, msisdn, iccid, status) VALUES
(1, '9876500001', '8991000000000000011', 'ASSIGNED'),
(2, '9876500002', '8991000000000000022', 'ASSIGNED'),
(3, '9876500003', '8991000000000000033', 'ASSIGNED'),
(4, '9876500004', '8991000000000000044', 'ASSIGNED'),
(5, '9876500005', '8991000000000000055', 'AVAILABLE'),
(6, '9876500006', '8991000000000000066', 'AVAILABLE');

INSERT INTO plans (id, name, description, monthly_fee, data_mb, voice_minutes, sms_count, overage_data_per_mb, overage_voice_per_min, overage_sms_each, active) VALUES
(1, 'Basic 299',    '3 GB data, 100 mins, 100 SMS',       299.00,   3072,  100,  100, 0.0200, 1.0000, 0.5000, 1),
(2, 'Standard 499', '20 GB data, 500 mins, 300 SMS',      499.00,  20480,  500,  300, 0.0200, 1.0000, 0.5000, 1),
(3, 'Premium 999',  '100 GB data, 3000 mins, 1000 SMS',   999.00, 102400, 3000, 1000, 0.0150, 0.8000, 0.4000, 1),
(4, 'Legacy 199',   'Retired plan, no new signups',       199.00,   1024,   50,   50, 0.0300, 1.5000, 0.7500, 0);

INSERT INTO addons (id, name, description, price, extra_data_mb, extra_voice_minutes, extra_sms, active) VALUES
(1, 'Extra 5GB',        '5 GB additional data',     99.00, 5120,   0,   0, 1),
(2, 'Talktime Pack',    '300 additional minutes',   79.00,    0, 300,   0, 1),
(3, 'SMS Bundle',       '500 additional SMS',       49.00,    0,   0, 500, 1);

INSERT INTO subscriptions (id, customer_id, plan_id, sim_id, status, bill_day, activated_at, terminated_at) VALUES
(1, 1, 2, 1, 'ACTIVE',    1,  '2026-09-01 10:00:00', NULL),
(2, 2, 1, 2, 'ACTIVE',    10, '2026-09-10 11:30:00', NULL),
(3, 3, 3, 3, 'SUSPENDED', 5,  '2026-08-05 09:15:00', NULL),
(4, 4, 2, 4, 'PENDING',   15, NULL, NULL);

INSERT INTO subscription_addons (subscription_id, addon_id, added_on, removed_on) VALUES
(1, 1, '2026-09-01', NULL);

INSERT INTO subscription_status_history (subscription_id, from_status, to_status, reason, changed_at) VALUES
(1, NULL,      'PENDING',   'Subscription created',        '2026-09-01 09:55:00'),
(1, 'PENDING', 'ACTIVE',    'KYC verified, SIM activated', '2026-09-01 10:00:00'),
(2, NULL,      'PENDING',   'Subscription created',        '2026-09-10 11:20:00'),
(2, 'PENDING', 'ACTIVE',    'KYC verified, SIM activated', '2026-09-10 11:30:00'),
(3, NULL,      'PENDING',   'Subscription created',        '2026-08-05 09:00:00'),
(3, 'PENDING', 'ACTIVE',    'KYC verified, SIM activated', '2026-08-05 09:15:00'),
(3, 'ACTIVE',  'SUSPENDED', 'Invoice overdue by 7 days',   '2026-11-22 00:00:00'),
(4, NULL,      'PENDING',   'Awaiting KYC verification',   '2026-11-15 14:00:00');

-- Usage: sub 1 (normal), sub 2 (light), sub 3 (heavy, causes overage)
INSERT INTO usage_records (subscription_id, usage_type, quantity, used_at) VALUES
(1, 'VOICE',   12.50, '2026-11-02 09:10:00'),
(1, 'VOICE',   45.00, '2026-11-05 19:40:00'),
(1, 'SMS',      6.00, '2026-11-06 12:00:00'),
(1, 'DATA',  1500.00, '2026-11-07 21:00:00'),
(1, 'DATA',  3200.00, '2026-11-12 20:30:00'),
(2, 'VOICE',   20.00, '2026-11-11 10:00:00'),
(2, 'SMS',     15.00, '2026-11-13 11:00:00'),
(2, 'DATA',   800.00, '2026-11-14 22:00:00'),
(3, 'VOICE',  900.00, '2026-10-10 15:00:00'),
(3, 'DATA', 98000.00, '2026-10-15 23:00:00'),
(3, 'DATA', 12000.00, '2026-10-28 23:30:00'),
(3, 'SMS',    120.00, '2026-10-20 13:00:00');

-- Invoices (GST 18%)
INSERT INTO invoices (id, invoice_no, subscription_id, customer_id, period_start, period_end, plan_charge, addon_charge, overage_charge, tax_amount, total_amount, status, issued_on, due_date) VALUES
(1, 'INV-2026-10-0001', 1, 1, '2026-10-01', '2026-10-31', 499.00,  99.00,   0.00, 107.64,  705.64, 'PAID',      '2026-11-01', '2026-11-15'),
(2, 'INV-2026-10-0002', 3, 3, '2026-10-05', '2026-11-04', 999.00,   0.00, 120.00, 201.42, 1320.42, 'OVERDUE',   '2026-11-05', '2026-11-15'),
(3, 'INV-2026-10-0003', 2, 2, '2026-10-10', '2026-11-09', 299.00,   0.00,   0.00,  53.82,  352.82, 'GENERATED', '2026-11-10', '2026-11-25');

INSERT INTO invoice_items (invoice_id, item_type, description, amount) VALUES
(1, 'PLAN',    'Standard 499 monthly fee',        499.00),
(1, 'ADDON',   'Extra 5GB add-on',                 99.00),
(1, 'TAX',     'GST 18%',                         107.64),
(2, 'PLAN',    'Premium 999 monthly fee',         999.00),
(2, 'OVERAGE', 'Data overage 8000 MB x 0.0150',   120.00),
(2, 'TAX',     'GST 18%',                         201.42),
(3, 'PLAN',    'Basic 299 monthly fee',           299.00),
(3, 'TAX',     'GST 18%',                          53.82);

INSERT INTO payments (invoice_id, amount, method, status, txn_ref, paid_at) VALUES
(1, 705.64,  'UPI',  'SUCCESS', 'TXN-UPI-0001',  '2026-11-03 18:20:00'),
(2, 1320.42, 'CARD', 'FAILED',  'TXN-CARD-0002', '2026-11-12 10:05:00');

INSERT INTO notifications (customer_id, channel, subject, message, status) VALUES
(1, 'EMAIL', 'Your invoice is ready',  'Invoice INV-2026-10-0001 of Rs 705.64 is generated.', 'SENT'),
(3, 'SMS',   'Payment overdue',        'Your bill of Rs 1320.42 is overdue. Pay to avoid suspension.', 'SENT'),
(3, 'EMAIL', 'Payment failed',         'Your card payment for INV-2026-10-0002 failed.', 'SENT');