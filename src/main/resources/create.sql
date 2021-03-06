create table Horse (
  id bigint generated by default as identity not null primary key,
  name varchar(255) not null,
  price decimal(10,2) not null,
  picture blob not null,
  therapy int not null,
  gender int not null,
  deleted boolean default false not null,
--  check (trim(name) <> ''), -- not working
  check (price >= 0),
  check (therapy between 0 and 2) -- Hippo, HPV, HPR
);

create table Invoice (
  id bigint generated by default as identity not null primary key,
  date timestamp default current_timestamp not null,
  receiver varchar(255) not null,
  insurancerate int not null,
  state int not null,
--  check (trim(name) <> ''), -- not working
  check (insurancerate = 0 or insurancerate between 5 and 15), -- tax percent
  check (state between 0 and 2) -- created, canceled, paid
);

create table consumed (
  invoice_id bigint not null,
  horse_id bigint not null,
  amount int default 1 not null,
  check (amount > 0),
  foreign key (invoice_id) references Invoice (id),
  foreign key (horse_id) references Horse (id)
);
