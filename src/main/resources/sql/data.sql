insert into employee(employee_id, name, surname, middle_name, post) values
(101,'Petr','Smirnov','Petrovich','Middle'),
(102,'Alexey', 'Petrov','Yegorovich','Junior'),
(103,'Ivan', 'Markov','Vyacheslavovich','Middle');

insert into project(project_id, title, abbreviation, description) values
(101,'Java','AAN','description 1'),
(102,'Kotlin','AB','description 3'),
(103,'Spring','AC','description 5');

insert into task(task_id, title, hours, start_date, end_date, status, project_id) values
(101,'write code',4, '2021-04-13', '2025-04-15','POSTPONED',101),
(102,'read article', 5, '2020-04-30', '2025-04-23','NOT_STARTED',102),
(103,'add validation', 12, '2021-09-19','2025-05-27','COMPLETED',103),
(104,'add config', 48,'2020-01-01','2025-04-14','NOT_STARTED',103);

insert into task_employee(task_id, employee_id) values
(101,101),
(101,102),
(102,103),
(103,103);
