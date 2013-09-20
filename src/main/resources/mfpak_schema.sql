drop table if exists NewsPaper cascade;
create table NewsPaper
(
	RowId SERIAL PRIMARY KEY ,
	NewsPaperId VARCHAR (255),
	Created TIMESTAMP default NOW()
);

drop table if exists NewsPaperTitle cascade;
create table NewsPaperTitle
(
	RowId SERIAL PRIMARY KEY ,
	NewsPaperRowId int references NewsPaper(RowId),
	Name VARCHAR (255) NOT NULL,
	FromDate DATE,
	ToDate DATE,
	DDA VARCHAR(20),
	PublicationLocation VARCHAR(255),
	Created TIMESTAMP default NOW()
);

drop table if exists Consignment  cascade;
create table Consignment
(
	RowId SERIAL PRIMARY KEY ,
	ConsignmentId VARCHAR (50) UNIQUE NOT NULL,
	DriverName VARCHAR (255),  --name of the person who handled the container
	DestinationIsSB boolean NOT NULL,
	Created TIMESTAMP default NOW()
);

drop table if exists ShippingContainer cascade;
create table ShippingContainer
(
	RowId SERIAL PRIMARY KEY,
	CreatedBy VARCHAR (255),  --name of the person who handled the container
	ShippingContainerId VARCHAR (50) UNIQUE NOT NULL,
	Created TIMESTAMP default NOW()
);

drop table if exists Batch cascade;
create table Batch
(
	RowId SERIAL PRIMARY KEY,
	BatchId INT,          -- this is the SB barcode
	CartonNumber INT NOT NULL,
	Picture BYTEA,
	Weight NUMERIC(9,3),
	MultiFilm boolean,  -- this is a summary of the batch content. True if a Batch has more than 15 entries in BatchContent, else false
	NewsPaperRowId INT  references NewsPaper(RowId),
	ShippingContainerRowId INT references ShippingContainer(RowId),
	ConsignmentRowId INT references Consignment(RowId),
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

drop type if exists TYPEFACE cascade;
create type TYPEFACE AS ENUM ('fracture', 'mixed', 'latin');

drop table if exists BatchContent cascade;
create table BatchContent
(
	RowId SERIAL PRIMARY KEY,
	BatchRowId INT references Batch(RowId),
	FromDate DATE,
	ToDate DATE,
	Exposures INT,
	TypeFace TYPEFACE,
	Note TEXT,
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

drop table if exists Status cascade;
create table Status
(
	RowId SERIAL PRIMARY KEY ,
	Name VARCHAR (255),
	Created TIMESTAMP default NOW()
);

insert into Status (Name) values ('Initial');
insert into Status (Name) values ('Batch added to shipping container');
insert into Status (Name) values ('Batch shipped to supplier');  --this is the state when the consigment id is added at SB.
insert into Status (Name) values ('Batch shipped from supplier'); --supplier call procedure with consigmentid,
insert into Status (Name) values ('Batch received from supplier');
insert into Status (Name) values ('Batch follow-up');
insert into Status (Name) values ('Batch approved');

drop table if exists BatchStatus cascade;
create table BatchStatus
(
	RowId SERIAL PRIMARY KEY,
	BatchRowId int NOT NULL references Batch(RowId),
	StatusRowId int NOT NULL references Status(RowId),
	Created TIMESTAMP  default NOW()
);

--copy of the batch if changed occur who, where and what, picture not included
drop table if exists BatchHistory;
create table BatchHistory
(
	RowId SERIAL PRIMARY KEY,
	BatchRowId INT  references Batch(RowId),
	BatchId INT,          -- this is the SB barcode
	CartonNumber INT NOT NULL,
	Weight NUMERIC(9,3),
	NewsPaperRowId INT  references NewsPaper(RowId),
	ShippingContainerRowId INT references ShippingContainer(RowId),
	ConsignmentRowId INT references Consignment(RowId),
	PcIp inet,
	CreatedBy VARCHAR (255),
	Created TIMESTAMP default NOW()
);


--keep track of last update from spreadsheet
drop table if exists System cascade;
create table System
(
	Key VARCHAR (50) PRIMARY KEY,
	Value VARCHAR (255),
	Modified TIMESTAMP default NOW(),
	Created TIMESTAMP default NOW()
);

insert into system (key, value) values ('Last spreadsheet update', 'YYYYMMDDHHMMSS');
--
--drop table if exists Order cascade;
--create table Order
--(
--	RowId SERIAL PRIMARY KEY,
--	OrderId INT UNIQUE NOT NULL ,
--	Created TIMESTAMP  default NOW(),
--);
