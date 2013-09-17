-- This is a draft schema for the mfpak postgres database

drop table if exists "NewsPaper" cascade;

create table "NewsPaper"

(

                              "Id" SERIAL PRIMARY KEY ,

                              "Name" VARCHAR (255),

                              "Created" TIMESTAMP default NOW()

);

insert into "NewsPaper" ("Name") values ('Adresseavisen');

insert into "NewsPaper" ("Name") values ('Berlingsketidende');

insert into "NewsPaper" ("Name") values ('Jyllandsposten');

drop table if exists "Status" cascade;

create table "Status"

(

                              "Id" SERIAL PRIMARY KEY ,

                              "Name" VARCHAR (255),

                              "Created" TIMESTAMP default NOW()

);

insert into "Status" ("Name") values ('Batch tilføjet');

insert into "Status" ("Name") values ('Batch slettet');

insert into "Status" ("Name") values ('Batch godkendt');

insert into "Status" ("Name") values ('Batch opfølgning');

insert into "Status" ("Name") values ('Login');

insert into "Status" ("Name") values ('Under pakning');

insert into "Status" ("Name") values ('Sendt');

insert into "Status" ("Name") values ('Under udpakning');

insert into "Status" ("Name") values ('Modtaget retur');

drop table if exists "Shipment" cascade;

create table "Shipment"

(

                              "Id" SERIAL PRIMARY KEY,

                              "CreatedBy" VARCHAR (255),

                              "StatusId" INT references "Status"("Id"),

                              "Barcode" VARCHAR (50) NOT NULL,

                              "Created" TIMESTAMP default NOW()

);

drop table if exists "Batch" cascade;

create table "Batch"

(

                              "Id" SERIAL PRIMARY KEY,

                              "Barcode" VARCHAR (50) NOT NULL,

                              "Picture" BYTEA,

                              "Weight" NUMERIC(9,3),

                              "NewsPaperId" INT  references "NewsPaper"("Id"),

                              "ShipmentId" INT references "Shipment"("Id"),

                              "SupplierShipmentReference" VARCHAR (100),

                              "StatusId" INT references "Status"("Id"),

                              "Created" TIMESTAMP  default NOW(),

                              "Modified" TIMESTAMP

);

drop table if exists "BatchEvent";

create table "BatchEvent"

(

                              "Id" SERIAL NOT NULL,

                              "StatusId" INT references "Status"("Id"),

                              "CreatedBy" VARCHAR (255),

                              "PcIp" inet,

                              "BatchId" INT references "Batch"("Id"),

                              "Barcode" VARCHAR (50),

                              "ShipmentId" INT references "Shipment"("Id"),

                              "SupplierShipmentReference" VARCHAR (100),

                              "Created" TIMESTAMP default NOW()

);