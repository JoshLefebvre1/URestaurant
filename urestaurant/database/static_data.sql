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
INSERT INTO contact_types (name_en, name_fr)
VALUES ('deleted', 'supprimé');