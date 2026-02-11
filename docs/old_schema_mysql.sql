-- Archived legacy MySQL-style schema and seed scripts removed from Flyway migrations.
-- Source files: V1__init.sql, V2__seed.sql

-- V1__init.sql
CREATE TABLE users (id BIGINT PRIMARY KEY AUTO_INCREMENT, email VARCHAR(190) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL, role VARCHAR(30) NOT NULL, status VARCHAR(30) NOT NULL, phone VARCHAR(50), created_at TIMESTAMP NULL, last_login_at TIMESTAMP NULL);
CREATE TABLE student_profiles (user_id BIGINT PRIMARY KEY, personal_details TEXT, qualifications TEXT, experience TEXT, location VARCHAR(120), cv_document_id BIGINT, transcript_document_id BIGINT, preferences JSON, CONSTRAINT fk_sp_user FOREIGN KEY (user_id) REFERENCES users(id));
CREATE TABLE company_profiles (user_id BIGINT PRIMARY KEY, company_name VARCHAR(190), registration_number VARCHAR(120), industry VARCHAR(100), contact_info TEXT, official_email VARCHAR(190), verification_status VARCHAR(30), submitted_at TIMESTAMP NULL, reviewed_at TIMESTAMP NULL, review_notes TEXT, CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users(id));
CREATE TABLE documents (id BIGINT PRIMARY KEY AUTO_INCREMENT, owner_user_id BIGINT, type VARCHAR(30), filename VARCHAR(255), mime_type VARCHAR(100), size BIGINT, storage_path VARCHAR(400), uploaded_at TIMESTAMP NULL);
CREATE TABLE careers (id BIGINT PRIMARY KEY AUTO_INCREMENT, title VARCHAR(190), field VARCHAR(120), demand_level VARCHAR(40), salary_range_min INT, salary_range_max INT, description TEXT);
CREATE TABLE bursaries (id BIGINT PRIMARY KEY AUTO_INCREMENT, company_id BIGINT, name VARCHAR(190), description TEXT, field_of_study VARCHAR(120), academic_level VARCHAR(120), start_date DATE, end_date DATE, funding_amount DECIMAL(12,2), benefits TEXT, status VARCHAR(40), eligibility_criteria JSON, location_filters JSON, required_subjects JSON, created_at TIMESTAMP NULL, updated_at TIMESTAMP NULL);
CREATE TABLE bursary_applications (id BIGINT PRIMARY KEY AUTO_INCREMENT, bursary_id BIGINT, student_id BIGINT, status VARCHAR(40), submitted_at TIMESTAMP NULL, updated_at TIMESTAMP NULL);
CREATE TABLE subscription_plans (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(30), price DECIMAL(12,2), billing_cycle VARCHAR(30), features JSON);
CREATE TABLE student_subscriptions (id BIGINT PRIMARY KEY AUTO_INCREMENT, student_id BIGINT, plan_id BIGINT, status VARCHAR(30), started_at TIMESTAMP NULL, ends_at TIMESTAMP NULL, last_payment_status VARCHAR(30));
CREATE TABLE payment_transactions (id BIGINT PRIMARY KEY AUTO_INCREMENT, student_id BIGINT, plan_id BIGINT, amount DECIMAL(12,2), currency VARCHAR(12), status VARCHAR(30), provider_ref VARCHAR(190), created_at TIMESTAMP NULL);
CREATE TABLE notifications (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT, channel VARCHAR(30), type VARCHAR(40), title VARCHAR(190), message TEXT, status VARCHAR(30), created_at TIMESTAMP NULL);
CREATE TABLE message_threads (id BIGINT PRIMARY KEY AUTO_INCREMENT, company_id BIGINT, student_id BIGINT, created_at TIMESTAMP NULL);
CREATE TABLE messages (id BIGINT PRIMARY KEY AUTO_INCREMENT, thread_id BIGINT, sender_user_id BIGINT, body TEXT, created_at TIMESTAMP NULL);
CREATE TABLE audit_logs (id BIGINT PRIMARY KEY AUTO_INCREMENT, actor_user_id BIGINT, action VARCHAR(120), entity_type VARCHAR(80), entity_id VARCHAR(120), metadata JSON, created_at TIMESTAMP NULL);
CREATE TABLE password_reset_tokens (id BIGINT PRIMARY KEY AUTO_INCREMENT, user_id BIGINT, token_hash VARCHAR(128), expires_at TIMESTAMP NULL, used BOOLEAN);
CREATE TABLE templates (id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(120), content TEXT, channel VARCHAR(30));
CREATE TABLE bursary_view_events (id BIGINT PRIMARY KEY AUTO_INCREMENT, bursary_id BIGINT, student_id BIGINT, viewed_at TIMESTAMP NULL);
CREATE TABLE company_shortlists (id BIGINT PRIMARY KEY AUTO_INCREMENT, company_id BIGINT, student_id BIGINT);
CREATE TABLE invitation_links (id BIGINT PRIMARY KEY AUTO_INCREMENT, company_id BIGINT, bursary_id BIGINT, token VARCHAR(190), created_at TIMESTAMP NULL);

-- V2__seed.sql
INSERT INTO users(id,email,password_hash,role,status,phone,created_at) VALUES
(1,'admin@edurite.com','$2a$10$h6zjQFFZ4kaA6ER2Q2O7xOnOcY5I7Xu6QxP4ET4MN5r2Dzy5UmfP6','ADMIN','ACTIVE','+10000000001',NOW()),
(2,'company@edurite.com','$2a$10$h6zjQFFZ4kaA6ER2Q2O7xOnOcY5I7Xu6QxP4ET4MN5r2Dzy5UmfP6','COMPANY','ACTIVE','+10000000002',NOW()),
(3,'student@edurite.com','$2a$10$h6zjQFFZ4kaA6ER2Q2O7xOnOcY5I7Xu6QxP4ET4MN5r2Dzy5UmfP6','STUDENT','ACTIVE','+10000000003',NOW());
INSERT INTO student_profiles(user_id,personal_details,qualifications,experience,location,preferences) VALUES(3,'{"name":"Demo Student"}','BSc Computer Science','Internship at Labs','Cape Town','{"interests":["AI","Software"]}');
INSERT INTO company_profiles(user_id,company_name,registration_number,industry,official_email,verification_status,submitted_at) VALUES(2,'FutureTech Ltd','REG-100','Technology','company@edurite.com','APPROVED',NOW());
INSERT INTO careers(title,field,demand_level,salary_range_min,salary_range_max,description) VALUES
('Software Engineer','Technology','HIGH',30000,90000,'Build software systems'),('Data Scientist','Technology','HIGH',35000,100000,'Analyze data and build models');
INSERT INTO bursaries(company_id,name,description,field_of_study,academic_level,start_date,end_date,funding_amount,benefits,status,eligibility_criteria,location_filters,required_subjects,created_at,updated_at) VALUES
(2,'STEM Future Fund','Support for STEM students','Technology','Undergraduate',CURDATE(),DATE_ADD(CURDATE(), INTERVAL 180 DAY),50000,'Laptop + mentorship','ACTIVE','{"minGPA":3.0}','{"regions":["Cape Town","Johannesburg"]}','{"subjects":["Math","Science"]}',NOW(),NOW());
INSERT INTO subscription_plans(id,name,price,billing_cycle,features) VALUES(1,'BASIC',9.99,'MONTHLY','{"aiDailyLimit":2}'),(2,'PREMIUM',29.99,'MONTHLY','{"aiDailyLimit":50}');
INSERT INTO student_subscriptions(student_id,plan_id,status,started_at,ends_at,last_payment_status) VALUES(3,2,'ACTIVE',NOW(),DATE_ADD(NOW(), INTERVAL 30 DAY),'SUCCESS');
INSERT INTO payment_transactions(student_id,plan_id,amount,currency,status,provider_ref,created_at) VALUES(3,2,29.99,'USD','SUCCESS','seed-payment',NOW());
INSERT INTO templates(name,content,channel) VALUES('pending_approval','Your item is pending approval.','EMAIL');
