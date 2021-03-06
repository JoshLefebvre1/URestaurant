-- MySQL Script generated by MySQL Workbench
-- 03/22/16 13:14:44
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema urestaurant
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema urestaurant
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `urestaurant` DEFAULT CHARACTER SET utf8 ;
USE `urestaurant` ;

-- -----------------------------------------------------
-- Table `urestaurant`.`documents`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`documents` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`documents` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `document` LONGBLOB NOT NULL,
  `content_type` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`contacts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`contacts` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`contacts` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `photo_id` INT NULL,
  `firstname` VARCHAR(80) NULL,
  `lastname` VARCHAR(80) NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_contacts_documents1_idx` (`photo_id` ASC),
  INDEX `fk_contacts_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_contacts_documents1`
    FOREIGN KEY (`photo_id`)
    REFERENCES `urestaurant`.`documents` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contacts_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `urestaurant`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`users` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(300) NOT NULL,
  `contact_id` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_users_contacts1_idx` (`contact_id` ASC),
  CONSTRAINT `fk_users_contacts1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `urestaurant`.`contacts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`events` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `description` VARCHAR(250) NULL,
  `location` VARCHAR(85) NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NULL,
  `capacity` INT NULL,
  `is_public` TINYINT(1) NOT NULL,
  `document_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `event_documents_id_idx` (`document_id` ASC),
  CONSTRAINT `event_documents_id`
    FOREIGN KEY (`document_id`)
    REFERENCES `urestaurant`.`documents` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`reviews`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`reviews` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`reviews` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `event_id` INT NOT NULL,
  `like` INT NULL,
  `description` VARCHAR(250) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_reviews_users1_idx` (`user_id` ASC),
  INDEX `fk_reviews_events1_idx` (`event_id` ASC),
  CONSTRAINT `fk_reviews_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `urestaurant`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reviews_events1`
    FOREIGN KEY (`event_id`)
    REFERENCES `urestaurant`.`events` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`messages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`messages` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`messages` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `message` VARCHAR(450) NOT NULL,
  `sender_id` INT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_messages_users1_idx` (`sender_id` ASC),
  CONSTRAINT `fk_messages_users1`
    FOREIGN KEY (`sender_id`)
    REFERENCES `urestaurant`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`user_messages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`user_messages` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`user_messages` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `message_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_to_users_messages1_idx` (`message_id` ASC),
  INDEX `fk_to_users_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_to_users_messages1`
    FOREIGN KEY (`message_id`)
    REFERENCES `urestaurant`.`messages` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_to_users_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `urestaurant`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`reservations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`reservations` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`reservations` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `events_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_reservations_events1_idx` (`events_id` ASC),
  CONSTRAINT `fk_reservations_events1`
    FOREIGN KEY (`events_id`)
    REFERENCES `urestaurant`.`events` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`user_event_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`user_event_types` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`user_event_types` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name_en` VARCHAR(45) NOT NULL,
  `name_fr` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`user_event_statuses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`user_event_statuses` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`user_event_statuses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name_en` VARCHAR(45) NOT NULL,
  `name_fr` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`user_events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`user_events` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`user_events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `event_id` INT NOT NULL,
  `user_event_type_id` INT NOT NULL,
  `user_event_status_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_events_users1_idx` (`user_id` ASC),
  INDEX `fk_user_events_events1_idx` (`event_id` ASC),
  INDEX `fk_user_events_user_event_types1_idx` (`user_event_type_id` ASC),
  INDEX `fk_user_events_user_event_statuses1_idx` (`user_event_status_id` ASC),
  CONSTRAINT `fk_user_events_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `urestaurant`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_events_events1`
    FOREIGN KEY (`event_id`)
    REFERENCES `urestaurant`.`events` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_events_user_event_types1`
    FOREIGN KEY (`user_event_type_id`)
    REFERENCES `urestaurant`.`user_event_types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_user_events_user_event_statuses1`
    FOREIGN KEY (`user_event_status_id`)
    REFERENCES `urestaurant`.`user_event_statuses` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`groups` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`groups` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_groups_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_groups_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `urestaurant`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`contact_groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`contact_groups` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`contact_groups` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `group_id` INT NOT NULL,
  `contact_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_contact_groups_groups1_idx` (`group_id` ASC),
  INDEX `fk_contact_groups_contacts1_idx` (`contact_id` ASC),
  CONSTRAINT `fk_contact_groups_groups1`
    FOREIGN KEY (`group_id`)
    REFERENCES `urestaurant`.`groups` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_contact_groups_contacts1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `urestaurant`.`contacts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`contact_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`contact_types` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`contact_types` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name_en` VARCHAR(45) NOT NULL,
  `name_fr` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`emails`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`emails` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`emails` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `contact_id` INT NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `is_primary` TINYINT(1) NOT NULL,
  `contact_type_id` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_emails_contacts1_idx` (`contact_id` ASC),
  INDEX `fk_emails_contact_types1_idx` (`contact_type_id` ASC),
  CONSTRAINT `fk_emails_contacts1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `urestaurant`.`contacts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_emails_contact_types1`
    FOREIGN KEY (`contact_type_id`)
    REFERENCES `urestaurant`.`contact_types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`phone_numbers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`phone_numbers` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`phone_numbers` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `phone_number` VARCHAR(45) NOT NULL,
  `contact_id` INT NOT NULL,
  `contact_type_id` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_phone_numbers_contacts1_idx` (`contact_id` ASC),
  INDEX `fk_phone_numbers_contact_types1_idx` (`contact_type_id` ASC),
  CONSTRAINT `fk_phone_numbers_contacts1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `urestaurant`.`contacts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_phone_numbers_contact_types1`
    FOREIGN KEY (`contact_type_id`)
    REFERENCES `urestaurant`.`contact_types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `urestaurant`.`addresses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `urestaurant`.`addresses` ;

CREATE TABLE IF NOT EXISTS `urestaurant`.`addresses` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `address_line` VARCHAR(250) NULL,
  `city` VARCHAR(45) NULL,
  `province` VARCHAR(45) NULL,
  `postal_code` VARCHAR(20) NULL,
  `country` VARCHAR(45) NULL,
  `event_id` INT NULL,
  `contact_id` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_addresses_events1_idx` (`event_id` ASC),
  INDEX `fk_addresses_contacts1_idx` (`contact_id` ASC),
  CONSTRAINT `fk_addresses_events1`
    FOREIGN KEY (`event_id`)
    REFERENCES `urestaurant`.`events` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_addresses_contacts1`
    FOREIGN KEY (`contact_id`)
    REFERENCES `urestaurant`.`contacts` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Insert static data into lookups

-- user_event_types table
INSERT INTO user_event_types (name_en, name_fr)
VALUES ('organizer', 'organisateur');
INSERT INTO user_event_types (name_en, name_fr)
VALUES ('attendee', 'participant');

-- user_event_statuses table
INSERT INTO user_event_statuses (name_en, name_fr)
VALUES ('going', 'aller');
INSERT INTO user_event_statuses (name_en, name_fr)
VALUES ('interested', 'intéressé');
INSERT INTO user_event_statuses (name_en, name_fr)
VALUES ('not going', 'pas aller');
INSERT INTO user_event_statuses (name_en, name_fr)
VALUES ('invited', 'invité');

-- contact_types table
INSERT INTO contact_types (name_en, name_fr)
VALUES ('primary', 'primaire');
INSERT INTO contact_types (name_en, name_fr)
VALUES ('work', 'travail');
INSERT INTO contact_types (name_en, name_fr)
VALUES ('home', 'domicile');
INSERT INTO contact_types (name_en, name_fr)
VALUES ('mobile', 'mobile');
INSERT INTO contact_types (name_en, name_fr)
VALUES ('personal', 'personnel');
