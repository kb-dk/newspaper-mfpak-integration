SET search_path TO public;

-- This creates a batch
INSERT INTO batch (batchid, cartonnumber) values (4001, 0);

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
