-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
insert into Person (id, name, birthdate) values(1, 'Gérard', '1990-06-01');
insert into Person (id, name, birthdate) values(2, 'Didier', '1992-04-12');
insert into Person (id, name, birthdate) values(3, 'Régis', '1993-10-17');
alter sequence Person_seq restart with 4;