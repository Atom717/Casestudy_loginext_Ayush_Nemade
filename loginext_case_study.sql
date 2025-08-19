create database loginext_case_study;
use loginext_case_study;

create table drivers(
    d_id int primary key,
    free_at int not null default 0,
    status int default 0
);

create table customers(
    c_id int primary key auto_increment,
    order_time int not null,
    travel_time int not null
);

create table assignments(
    a_id int primary key auto_increment,
    customer int,
    driver int,
    foreign key (customer) references customers(c_id),
    foreign key (driver) references drivers(d_id)
);

SELECT USER();