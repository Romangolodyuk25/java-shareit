CREATE TABLE IF NOT EXISTS USERS(
user_id INTEGER PRIMARY KEY AUTO_INCREMENT,
name varchar (15) not null,
email varchar (30) not null UNIQUE
);

CREATE TABLE IF NOT EXISTS ITEMS(
item_id INTEGER PRIMARY KEY AUTO_INCREMENT,
name varchar(50) not null,
description varchar(200) not null,
available boolean not null,
user_id INTEGER REFERENCES USERS(user_id)
);

CREATE TABLE IF NOT EXISTS BOOKINGS(
booking_id INTEGER PRIMARY KEY AUTO_INCREMENT,
start_date timestamp WITHOUT TIME ZONE,
end_date timestamp WITHOUT TIME ZONE,
item_id INTEGER REFERENCES ITEMS(item_id) not null,
booker_id INTEGER REFERENCES USERS(user_id) not null
);

CREATE TABLE IF NOT EXISTS REQUESTS(
request_id INTEGER PRIMARY KEY AUTO_INCREMENT,
description varchar(200) not null,
requestor_id INTEGER REFERENCES USERS(user_id)
);

CREATE TABLE IF NOT EXISTS COMMENTS(
comment_id INTEGER PRIMARY KEY AUTO_INCREMENT,
text varchar(255) not null,
item_id INTEGER REFERENCES ITEMS(item_id) not null,
author_id INTEGER REFERENCES USERS(user_id) not null
);