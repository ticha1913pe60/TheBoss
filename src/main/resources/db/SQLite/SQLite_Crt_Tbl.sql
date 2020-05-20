CREATE TABLE APP_LESSONS (
       ID_LESSON integer not null,
        LESSON_DATE varchar(255),
        END_TIME varchar(255),
        LANGUAGE_TYPE varchar(255),
        START_TIME varchar(255),
        ID_STUDENT integer,
        primary key (ID_LESSON)
    );
	
CREATE TABLE APP_STUDENTS (
       ID_PERSON integer not null,
        FIRSTNAME varchar(255),
        LASTNAME varchar(255),
        PHONE varchar(255),
        RATE double precision,
        primary key (ID_PERSON)
    );
	
CREATE TABLE APP_USERS (
       ID_PERSON integer not null,
        FIRSTNAME varchar(255),
        LASTNAME varchar(255),
        IS_ADMIN varchar(255),
        PASSWORD varchar(255),
        SALT varchar(255),
        USERNAME varchar(255),
        primary key (ID_PERSON)
    );

CREATE TABLE hibernate_sequence (
       next_val bigint
    );

INSERT INTO hibernate_sequence
(
	next_val
)
VALUES
(
	1
);

INSERT INTO hibernate_sequence
(
	next_val
)
VALUES
(
	1
);

INSERT INTO hibernate_sequence
(
	next_val
)
VALUES
(
	1
);

INSERT INTO APP_USERS
(
   USERNAME,
   PASSWORD,
   SALT,
   FIRSTNAME,
   LASTNAME,
   IS_ADMIN
)
VALUES
(
   'pakri',
   '8VoeSn7gIjWB+/espWp2we4CCkEzzd5yua0QEj3jQhY=',
   '6fhUbVwn7b5EjmOjrA0UFvMU1nTH9L',
   'Кристина',
   'Паскулова',
   'T'
);
	