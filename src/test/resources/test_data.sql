SET search_path TO public;

-- This creates a batch
INSERT INTO "Batch" ("Barcode") values ('4001');

-- This creates an event attached to the just created Batch using LASTVAL() to get the BatchId
INSERT INTO "BatchEvent" ("StatusId", "BatchId") SELECT "Id", LASTVAL() from "Status" WHERE "Name"='Sendt';


--Now add two more batches. One in status "new" and one being unpacked.

INSERT INTO "Batch" ("Barcode") values ('4002');
INSERT INTO "BatchEvent" ("StatusId", "BatchId") SELECT "Id", LASTVAL() from "Status" WHERE "Name"='Batch tilføjet';


INSERT INTO "Batch" ("Barcode") values ('4003');
INSERT INTO "BatchEvent" ("StatusId", "BatchId") SELECT "Id", LASTVAL() from "Status" WHERE "Name"='Under udpakning';

-- Now the slightly more realistic case of a Batch with three successive states
INSERT INTO "Batch" ("Barcode") values ('4004');
INSERT INTO "BatchEvent" ("StatusId", "BatchId") SELECT "Id", LASTVAL() from "Status" WHERE "Name"='Batch tilføjet';
INSERT INTO "BatchEvent" ("BatchId") SELECT "BatchId" from "BatchEvent" WHERE "Id" = LASTVAL();
UPDATE "BatchEvent" SET "StatusId" = (SELECT "Id" from "Status" WHERE "Name"='Sendt') WHERE "Id" = LASTVAL();
INSERT INTO "BatchEvent" ("BatchId") SELECT "BatchId" from "BatchEvent" WHERE "Id" = LASTVAL();
UPDATE "BatchEvent" SET "StatusId" = (SELECT "Id" from "Status" WHERE "Name"='Under udpakning') WHERE "Id" = LASTVAL();

