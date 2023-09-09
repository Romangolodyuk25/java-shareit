DROP TABLE USERS CASCADE;
DROP TABLE ITEMS CASCADE;
DROP TABLE BOOKINGS CASCADE;
DROP TABLE REQUESTS CASCADE;
DROP TABLE COMMENTS CASCADE;

CREATE TABLE IF NOT EXISTS USERS(
user_id INTEGER generated by default as identity primary key,
name varchar (100) not null,
email varchar (100) not null UNIQUE
);

CREATE TABLE IF NOT EXISTS ITEMS(
item_id INTEGER generated by default as identity primary key,
name varchar(100) not null,
description varchar(200) not null,
available boolean not null,
user_id INTEGER REFERENCES USERS(user_id)
);

CREATE TABLE IF NOT EXISTS BOOKINGS(
booking_id INTEGER generated by default as identity primary key,
start_date timestamp WITHOUT TIME ZONE,
end_date timestamp WITHOUT TIME ZONE,
status varchar(100) not null,
item_id INTEGER REFERENCES ITEMS(item_id) not null,
booker_id INTEGER REFERENCES USERS(user_id) not null
);

CREATE TABLE IF NOT EXISTS REQUESTS(
request_id INTEGER generated by default as identity primary key,
description varchar(200) not null,
requestor_id INTEGER REFERENCES USERS(user_id)
);

CREATE TABLE IF NOT EXISTS COMMENTS(
comment_id INTEGER generated by default as identity primary key,
text varchar(255) not null,
item_id INTEGER REFERENCES ITEMS(item_id) not null,
author_id INTEGER REFERENCES USERS(user_id) not null,
created timestamp WITHOUT TIME ZONE
);