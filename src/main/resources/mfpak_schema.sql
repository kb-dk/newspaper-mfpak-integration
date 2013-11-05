drop table if exists SpreadSheetNewsPaper cascade;
create table SpreadSheetNewsPaper
(
	RowId SERIAL PRIMARY KEY , 	
	NewsPaperId VARCHAR (255),
	Created TIMESTAMP default NOW()
);

drop table if exists SpreadSheetNewsPaperTitle cascade;
create table SpreadSheetNewsPaperTitle
(
	RowId SERIAL PRIMARY KEY , 	
	SpreadSheetNewsPaperRowId int references SpreadSheetNewsPaper(RowId),
	Name VARCHAR (255) NOT NULL,
	FromDate DATE,
	ToDate DATE,
	DDA VARCHAR(20),
	PublicationLocation VARCHAR(255),
	Created TIMESTAMP default NOW()
);

drop table if exists SpreadSheetBatch cascade;
create table SpreadSheetBatch
(
	RowId SERIAL PRIMARY KEY, 
	BatchId BIGINT,          -- this is the SB barcode
	CartonNumber INT NOT NULL,
	MultiFilm boolean,  -- this is a summary of the batch content. True if a Batch has more than 15 entries in Film, else false 
	SpreadSheetNewsPaperRowId INT references SpreadSheetNewsPaper(RowId),
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

drop table if exists SpreadSheetFilm cascade;
create table SpreadSheetFilm
(
	RowId SERIAL PRIMARY KEY, 
	SpreadSheetBatchRowId INT references SpreadSheetBatch(RowId),
	FromDate DATE,
	ToDate DATE,
	Exposures INT,
	TypeFace TYPEFACE,
	Note TEXT,
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

drop table if exists SpreadSheetStrike cascade;
create table SpreadSheetStrike
(
	RowId SERIAL PRIMARY KEY, 
	SpreadSheetFilmRowId INT references SpreadSheetFilm(RowId) NOT NULL,
	FromDate DATE,
	ToDate DATE,
	Created TIMESTAMP default NOW()
);

drop table if exists NewsPaper cascade;
create table NewsPaper
(
	RowId SERIAL PRIMARY KEY , 	
	NewsPaperId VARCHAR (255),
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE NewsPaper IS 'This is SB newspaper identification table';
COMMENT ON COLUMN NewsPaper.NewsPaperId IS '1. sheet in the spreadsheet column AvisID';

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

COMMENT ON COLUMN NewsPaperTitle.Name IS 'Name/title of the NewPaper in a given period';

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

drop table if exists ConsignmentContent cascade;
create table ConsignmentContent 
(
	RowId SERIAL PRIMARY KEY, 
	ConsignmentRowId int references Consignment(RowId),
	ShippingContainerRowId int references ShippingContainer(RowId) NOT NULL,
	BatchRowId int references Batch(RowId) NOT NULL,
	Created TIMESTAMP default NOW()
);

drop table if exists Batch cascade;
create table Batch
(
	RowId SERIAL PRIMARY KEY, 
	BatchId BIGINT,          -- this is the SB barcode
	CartonNumber INT NOT NULL,
	Picture BYTEA,
	Weight NUMERIC(9,3),
	MultiFilm boolean,  -- this is a summary of the batch content. True if a Batch has more than 15 entries in Film, else false 
	NewsPaperRowId INT references NewsPaper(RowId),
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

drop type if exists TYPEFACE cascade;
create type TYPEFACE AS ENUM ('fraktur', 'mixed', 'latin');

drop table if exists Film cascade;
create table Film
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

drop table if exists Strike cascade;
create table Strike
(
	RowId SERIAL PRIMARY KEY, 
	FilmRowId INT references Film(RowId) NOT NULL,
	FromDate DATE,
	ToDate DATE,
	Created TIMESTAMP default NOW()
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
	BatchId BIGINT,          -- this is the SB barcode
	CartonNumber INT NOT NULL,
	Weight NUMERIC(9,3),
	MultiFilm boolean,
	NewsPaperRowId INT  references NewsPaper(RowId),
	PcIp inet,
	CreatedBy VARCHAR (255),
	Created TIMESTAMP default NOW()
);


drop table if exists SystemSetting cascade;
create table SystemSetting 
(	
	RowId SERIAL PRIMARY KEY,
	Key VARCHAR (50) UNIQUE NOT NULL,
	Value TEXT,
	Modified TIMESTAMP default NOW(),
	Created TIMESTAMP default NOW()
);


insert into SystemSetting (key, value) values ('Spreadsheet load version', '1');

drop type if exists ORDERSTATUS cascade;
create type ORDERSTATUS AS ENUM ('Forecast', 'Ordered and packing', 'Packing completed', 'Order closed');

drop table if exists Order_ cascade;
create table Order_ 
(
	RowId SERIAL PRIMARY KEY, 
	OrderId INT UNIQUE NOT NULL,
	ORDERSTATUS status NOT NULL,
	ApprovedBy VARCHAR(255),
    ApprovedDate TIMESTAMP,	
	Modified TIMESTAMP, 
	Created TIMESTAMP  default NOW()
);

drop table if exists OrderLine cascade;
create table OrderLine 
(
	RowId SERIAL PRIMARY KEY, 
	OrderRowId INT references Order_(RowId),
	Priority INT, 
	NewsPaperRowId INT references NewsPaper(RowId),
	FromDate DATE,
	ToDate DATE,
	Note TEXT,
	OptionB1 boolean,
	OptionB2 boolean,
	OptionB3 boolean,
	OptionB4 boolean,
	OptionB5 boolean,
	OptionB6 boolean,
	OptionB7 boolean,
	OptionB8 boolean,
	OptionB9 boolean,
	Modified TIMESTAMP, 
	Created TIMESTAMP default NOW()
);

drop table if exists OrderBatch cascade;
create table OrderBatch 
(
	RowId SERIAL PRIMARY KEY, 
	OrderRowId INT references Order_(RowId),
	OrderLineRowId INT references OrderLine(RowId),
	BatchRowId INT references Batch(RowId),	
	Modified TIMESTAMP, 
	Created TIMESTAMP  default NOW()
);

DROP FUNCTION if exists shipped(VARCHAR(50), VARCHAR(50), INTEGER);
DROP LANGUAGE plpgsql;
CREATE  LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION shipped(consignment_id VARCHAR(50), shippingcontainer_id VARCHAR(50), batch_id INTEGER ) 
RETURNS INT LANGUAGE plpgsql  AS $$

DECLARE 
	consignment_row_id INTEGER; 
	shipping_container_row_id INTEGER; 
	batch_row_id INTEGER; 

BEGIN

SELECT INTO consignment_row_id rowid FROM consignment WHERE consignmentid = consignment_id;
IF NOT FOUND THEN
        INSERT INTO consignment (consignmentid, destinationissb) VALUES (consignment_id, 't');
        SELECT INTO consignment_row_id lastval();
END IF;

SELECT INTO shipping_container_row_id rowid FROM shipping_container WHERE shippingcontainerid = shippingcontainer_id;
IF NOT FOUND THEN
         INSERT INTO shippingcontainer (shippingcontainerid, createdby) VALUES (shippingcontainer_id,'ninestars');
         SELECT INTO shipping_container_row_id lastval();
END IF;

SELECT INTO batch_row_id rowid FROM batch WHERE batchid = batch_id;
IF NOT FOUND THEN
      RETURN 1;
END IF;

INSERT INTO consignmentcontent (consignmentrowid, shippingcontainerrowid, batchrowid) VALUES (consignment_row_id, shipping_container_row_id, batch_row_id);
INSERT INTO batchstatus(batchrowid, statusrowid) VALUES (batch_row_id,4);
RETURN 0;
END; 
$$ ;

