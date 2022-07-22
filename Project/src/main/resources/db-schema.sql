create table if not exists Sample(
    id serial PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data text,
    value int default 0
);

create table Stops (
	id   	       int				not null auto_increment,
    name    	   varchar(64)		not null unique,
    primary key(id)
);

create table Trains (
	id   	       int				not null auto_increment,
    name    	   varchar(64)		not null unique,
    primary key(id)
);

create table Routes (
	id   	       int				not null auto_increment,
    name    	   varchar(64)		not null unique,
    primary key(id)
);
create table RoutesStops (
 	Rid   	       int				not null,
    Sid    	       int      		not null,
    foreign key(Rid) references Routes(id) ON DELETE CASCADE,
    foreign key(Sid) references Stops(id) ON DELETE CASCADE
 );
 create table Schedules (
 	id   	       int				not null auto_increment,
    Rid   	       int				not null,
    Tid            int              not null,
    time           time (0)         not null,
    primary key(id),
    foreign key(Rid) references Routes(id) ON DELETE CASCADE,
    foreign key(Tid) references Trains(id) ON DELETE CASCADE
 );
 create table BackupTrains (
    sid   	       int				not null,
    Tid    	       int      		not null,
    foreign key(sid) references Schedules(id) ON DELETE CASCADE,
    foreign key(Tid) references Trains(id) ON DELETE CASCADE
  );



