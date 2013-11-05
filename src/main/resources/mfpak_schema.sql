drop table if exists SpreadSheetNewsPaper cascade;
create table SpreadSheetNewsPaper
(
	RowId SERIAL PRIMARY KEY , 	
	NewsPaperId VARCHAR (255),
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE SpreadSheetNewsPaper IS 'This table contains the daily spreed sheet load. All entries are deleted during load';
COMMENT ON COLUMN NewsPaper.NewsPaperId IS '1. sheet in the spreadsheet - column Avis ID';


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

COMMENT ON TABLE SpreadSheetNewsPaperTitle IS 'This table contains the daily spreed sheet load. All entries are deleted during load';

drop table if exists SpreadSheetBatch cascade;
create table SpreadSheetBatch
(
	RowId SERIAL PRIMARY KEY, 
	BatchId BIGINT,          
	CartonNumber INT NOT NULL,
	MultiFilm boolean,  
	SpreadSheetNewsPaperRowId INT references SpreadSheetNewsPaper(RowId),
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

COMMENT ON TABLE SpreadSheetBatch IS 'This table contains the daily spreed sheet load. All entries are deleted during load';
COMMENT ON COLUMN SpreadSheetBatch.BatchId IS 'This field contains the batch identier/barcode';
COMMENT ON COLUMN SpreadSheetBatch.MultiFilm IS 'Has value True if there are reals known to contain more than one film.';


drop type if exists TYPEFACE cascade;
create type TYPEFACE AS ENUM ('fraktur', 'mixed', 'latin');

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

COMMENT ON TABLE SpreadSheetFilm IS 'This table contains the daily spreed sheet load. All entries are deleted during load';

drop table if exists SpreadSheetStrike cascade;
create table SpreadSheetStrike
(
	RowId SERIAL PRIMARY KEY, 
	SpreadSheetFilmRowId INT references SpreadSheetFilm(RowId) NOT NULL,
	FromDate DATE,
	ToDate DATE,
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE SpreadSheetStrike IS 'This table contains the daily spreed sheet load. All entries are deleted during load';

drop table if exists NewsPaper cascade;
create table NewsPaper
(
	RowId SERIAL PRIMARY KEY , 	
	NewsPaperId VARCHAR (255),
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE NewsPaper IS 'This is SB newspaper identification table on newspapers ids in order';
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

COMMENT ON TABLE NewsPaperTitle IS 'Contains newspaper titles of titles in order';
COMMENT ON COLUMN NewsPaperTitle.Name IS 'Name/title of the NewPaper in a given period';

drop table if exists Consignment  cascade;
create table Consignment 
(
	RowId SERIAL PRIMARY KEY , 
	ConsignmentId VARCHAR (50) UNIQUE NOT NULL, 
	DriverName VARCHAR (255),  
	DestinationIsSB boolean NOT NULL,
	Created TIMESTAMP default NOW()	
);

COMMENT ON TABLE Consignment IS 'Contains the transport indentification for a consignment';
COMMENT ON COLUMN Consignment.DriverName IS 'Name of the person who handled the transport/consignment';

drop table if exists ShippingContainer cascade;
create table ShippingContainer 
(
	RowId SERIAL PRIMARY KEY, 
	CreatedBy VARCHAR (255),  
	ShippingContainerId VARCHAR (50) UNIQUE NOT NULL, 
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE ShippingContainer IS 'Contains the transport indentification for a shippingcontainer';
COMMENT ON COLUMN ShippingContainer.CreatedBy IS 'Name of the person logged into MFPak when the ShippingContainer registration was created. Holds the value ninestars when called from FUNCTION shipped';

drop table if exists ConsignmentContent cascade;
create table ConsignmentContent 
(
	RowId SERIAL PRIMARY KEY, 
	ConsignmentRowId int references Consignment(RowId),
	ShippingContainerRowId int references ShippingContainer(RowId) NOT NULL,
	BatchRowId int references Batch(RowId) NOT NULL,
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE ShippingContainer IS 'ConsignmentContent creates the link between consignment, shippingcontainer and a batch';

drop table if exists Batch cascade;
create table Batch
(
	RowId SERIAL PRIMARY KEY, 
	BatchId BIGINT,         
	CartonNumber INT NOT NULL,
	Picture BYTEA,
	Weight NUMERIC(9,3),
	MultiFilm boolean,  
	NewsPaperRowId INT references NewsPaper(RowId),
	Created TIMESTAMP default NOW(),
	Modified TIMESTAMP
);

COMMENT ON TABLE Batch IS 'Contains batches.';
COMMENT ON COLUMN Batch.BatchId IS 'This field contains the batch identier/barcode';
COMMENT ON COLUMN Batch.MultiFilm IS 'Has value True if there are reals known to contain more than one film.';



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
COMMENT ON TABLE Film IS 'Contains film information on batches.';


drop table if exists Strike cascade;
create table Strike
(
	RowId SERIAL PRIMARY KEY, 
	FilmRowId INT references Film(RowId) NOT NULL,
	FromDate DATE,
	ToDate DATE,
	Created TIMESTAMP default NOW()
);
COMMENT ON TABLE Film IS 'Contains strike information on films.';

drop table if exists Status cascade;
create table Status 
(
	RowId SERIAL PRIMARY KEY , 	
	Name VARCHAR (255),
	Created TIMESTAMP default NOW()
);
COMMENT ON TABLE Status IS 'Contains status description for a batch.';

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

COMMENT ON TABLE Status IS 'Newest record holds the current status of a batch.';


drop table if exists BatchHistory;
create table BatchHistory 
(
	RowId SERIAL PRIMARY KEY, 
	BatchRowId INT  references Batch(RowId),
	BatchId BIGINT,          
	CartonNumber INT NOT NULL,
	Weight NUMERIC(9,3),
	MultiFilm boolean,
	NewsPaperRowId INT  references NewsPaper(RowId),
	PcIp inet,
	CreatedBy VARCHAR (255),
	Created TIMESTAMP default NOW()
);
COMMENT ON TABLE BatchHistory IS 'Copy of the batch if changes occur.';

drop table if exists SystemSetting cascade;
create table SystemSetting 
(	
	RowId SERIAL PRIMARY KEY,
	Key VARCHAR (50) UNIQUE NOT NULL,
	Value TEXT,
	Modified TIMESTAMP default NOW(),
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE SystemSetting IS 'Contains application settings.';

insert into SystemSetting (key, value) values ('Spreadsheet load version', '1');

drop type if exists ORDERSTATUS cascade;
create type ORDERSTATUS AS ENUM ('Forecast', 'Ordered and packing', 'Packing completed', 'Order closed');

drop table if exists Order_ cascade;
create table Order_ 
(
	RowId SERIAL PRIMARY KEY, 
	OrderId INT UNIQUE NOT NULL,
	status ORDERSTATUS NOT NULL,
	ApprovedBy VARCHAR(255),
    ApprovedDate TIMESTAMP,	
	Modified TIMESTAMP, 
	Created TIMESTAMP  default NOW()
);

COMMENT ON TABLE Order_ IS 'Contains orders.';

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
	OptionB1 boolean NOT NULL,
	OptionB2 boolean NOT NULL,
	OptionB3 boolean NOT NULL,
	OptionB4 boolean NOT NULL,
	OptionB5 boolean NOT NULL,
	OptionB6 boolean NOT NULL,
	OptionB7 boolean NOT NULL,
	OptionB8 boolean NOT NULL,
	OptionB9 boolean NOT NULL,
	Modified TIMESTAMP, 
	Created TIMESTAMP default NOW()
);

COMMENT ON TABLE Order_ IS 'Contains order lines for a given order.';

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

COMMENT ON TABLE OrderBatch IS 'Links a batch to an orderline (and order).';

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

