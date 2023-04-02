create database homework3;
use homework3;

#drop table Students;
#drop table Departments;
#drop table Majors;
#drop table Minors;
#drop table IsTaking;
#drop table HasTaken;

create table Students (first_name varchar(20), last_name varchar(20), id int(9), primary key(id));
create table Departments(name varchar(30), campus varchar(5), primary key(name));
create table Classes(name varchar(35), credits int, primary key(name));
create table Majors(sid int(9), dname varchar(30), foreign key(sid) references Students(id), 
                                                   foreign key(dname) references Departments(name),
                                                   primary key(sid, dname));
create table Minors(sid int(9), dname varchar(30), foreign key(sid) references Students(id), 
                                                   foreign key(dname) references Departments(name),
                                                   primary key(sid, dname));
create table IsTaking(sid int(9), name varchar(35),foreign key(sid) references Students(id), 
                                                   foreign key(name) references Classes(name),
                                                   primary key(sid, name));
create table HasTaken(sid int(9), name varchar(35), grade char(1), 
												   foreign key(sid) references Students(id), 
                                                   foreign key(name) references Classes(name),
                                                   primary key(sid, name));