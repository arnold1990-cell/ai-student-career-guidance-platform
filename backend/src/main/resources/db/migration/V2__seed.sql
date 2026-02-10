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
