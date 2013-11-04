SET search_path TO public;

INSERT INTO newspaper (NewsPaperId) VALUES ('boersen');

-- This creates a batch
INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (4001, 0, LASTVAL());

-- This creates an event attached to the just created Batch using LASTVAL() to get the BatchId
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Batch shipped to supplier';


--Now add two more batches. One in status "new" and one being added.

INSERT INTO batch (batchid, cartonnumber) values (4002, 1);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';


INSERT INTO batch (batchid, cartonnumber) values (4003, 74);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Batch added to shipping container';

-- Now the slightly more realistic case of a Batch with three successive states
INSERT INTO batch (batchId, cartonnumber) values (4004, 103);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';
-- Create another status with the same batchrowId as that just created, then set its status to "adding"
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch added to shipping container') WHERE rowId = LASTVAL();
-- and again but for "shipping"
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch shipped to supplier') WHERE rowId = LASTVAL();
-- and again bur for "received from supplier"
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch received from supplier') WHERE rowId = LASTVAL();


--A Batch wtih no events
INSERT INTO batch (batchid, cartonnumber) values (4005, 87);


-- Entry for the batch ID in our testdata   
INSERT INTO newspaper (NewsPaperId) VALUES ('adresseavisen1759');

-- This creates a batch
INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028241, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));

-- This creates an event attached to the just created Batch using LASTVAL() to get the BatchId
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() FROM status WHERE name='Batch shipped to supplier';

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'adresseavisen1759'), 
    'Kiøbenhavns Kongelig alene priviligerede Adresse-Contoirs Efterretninger',
'1759-05-04', '1854-12-30', '1-35', 'København');

-- Add test data for batch date interval
INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'),
    'Børsen', '1899-10-01', '1970-08-28', '1-177', 'København'); 

INSERT INTO Batch (BatchId, CartonNumber, NewsPaperRowId) VALUES (400022028245, 123, 
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'));

-- Yes deliberately mangle the date ordering. 
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1920-10-01', '1940-10-01');

INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1899-10-01', '1900-10-01');

INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1910-10-01', '1919-10-01');




