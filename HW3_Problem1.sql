create database homework3;
use homework3;

create table Students (first_name varchar(20), last_name varchar(20), id int(9), primary key(id));
create table Departments(name varchar(30), campus varchar(5), primary key(name));
create table Classes(name varchar(35), credits int, primary key(name));
create table Majors(sid int(9), dname varchar(30), primary key(sid, dname), 
												   foreign key(sid) references Students(id), 
                                                   foreign key(dname) references Departments(name));
create table Minors(sid int(9), dname varchar(30), primary key(sid, dname), 
												   foreign key(sid) references Students(id), 
                                                   foreign key(dname) references Departments(name));
create table IsTaking(sid int(9), name varchar(35), primary key(sid, name), 
												   foreign key(sid) references Students(id), 
                                                   foreign key(name) references Classes(name));
create table HasTaken(sid int(9), name varchar(35), grade char(1), 
												   primary key(sid, name), 
												   foreign key(sid) references Students(id), 
                                                   foreign key(name) references Classes(name));
                                                   

