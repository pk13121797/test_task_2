drop table if exists task_employee;
drop table if exists task;
drop table if exists employee;
drop table if exists project;

create table if not exists employee (
    employee_id serial not null,
    name varchar(255) not null,
    surname varchar(255) not null,
    middle_name varchar(255) not null,
    post varchar(255) not null,
    primary key (employee_id)
);

create table if not exists project (
    project_id serial not null ,
    title varchar(255) not null,
    abbreviation varchar(255) not null,
    description varchar(255) not null,
    primary key (project_id)
);

create table if not exists task (
    task_id serial not null,
    title varchar(255) not null,
    hours int not null,
    start_date date not null,
    end_date date not null,
    status varchar(255) not null check (status in ('NOT_STARTED','IN_PROCESS', 'COMPLETED', 'POSTPONED')),
    project_id int not null,
    primary key (task_id),
    foreign key (project_id) references project(project_id) on delete cascade
);

create table if not exists task_employee (
    task_id int not null,
    employee_id int not null,
    foreign key (task_id) references task(task_id) on delete cascade,
    foreign key (employee_id) references employee(employee_id) on delete cascade
);