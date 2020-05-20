create table APP_USERS
(
  ID_PERSON  integer,
  USERNAME   varchar2( 50 ),
  PASSWORD   varchar2( 50 ),
  SALT       varchar2( 50 ),
  FIRSTNAME  varchar2( 50 ),
  LASTNAME   varchar2( 50 ),
  IS_ADMIN   varchar2( 1 ),
  constraint APP_USERS_PK primary key( ID_PERSON )
);

create table APP_STUDENTS
(
  ID_PERSON  integer,
  FIRSTNAME  varchar2( 50 ),
  LASTNAME   varchar2( 50 ),
  PHONE      varchar2( 20 ),
  RATE       number,
  constraint APP_STUDENTS_PK primary key( ID_PERSON )
);

create table APP_LESSONS
(
  ID_LESSON      integer,
  ID_STUDENT     integer,
  LANGUAGE_TYPE  varchar2( 10 ),
  LESSON_DATE    varchar2( 255 ),
  START_TIME     varchar2( 255 ),
  END_TIME       varchar2( 255 ),
  constraint APP_LESSONS_PK primary key( ID_LESSON ),
  constraint FK_STUDENT foreign key( ID_STUDENT ) references APP_STUDENTS( ID_PERSON )
);

insert into APP_USERS(
              ID_PERSON,
              USERNAME,
              PASSWORD,
              SALT,
              FIRSTNAME,
              LASTNAME,
              IS_ADMIN
            )
     values ( 1,
              'pakri',
              '8VoeSn7gIjWB+/espWp2we4CCkEzzd5yua0QEj3jQhY=',
              '6fhUbVwn7b5EjmOjrA0UFvMU1nTH9L',
              'Кристина',
              'Паскулова',
              'T'
             );
			 
CREATE SEQUENCE student_seq START WITH 1;
CREATE SEQUENCE lesson_seq START WITH 1;
CREATE SEQUENCE user_seq START WITH 1;			 