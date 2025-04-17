CREATE TABLE Cars (
    carid BIGSERIAL PRIMARY KEY,
    maker VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    segment VARCHAR(255) ,
    motorType VARCHAR(255) 
);