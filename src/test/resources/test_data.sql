SET search_path TO public;

INSERT INTO newspaper (NewsPaperId) VALUES ('boersen');

INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (4001, 0, LASTVAL());
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
--INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() FROM status WHERE name='Batch shipped to supplier';
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO batchstatus (statusrowId, batchrowId, Created) VALUES ((SELECT rowid FROM status WHERE name ='Batch approved'), (SELECT rowid FROM batch WHERE batchid = '400022028241'), '2013-11-14');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'adresseavisen1759'), 
    'Kiøbenhavns Kongelig alene priviligerede Adresse-Contoirs Efterretninger',
'1759-05-04', '1854-12-30', '1-35', 'København');

INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028241), '1795-06-01', '1795-06-15');


-- Database content for supporting the single bad film of the first pilot batch
INSERT INTO newspaper (NewsPaperId) VALUES ('morgenavisenjyllandsposten');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'morgenavisenjyllandsposten'), 
    'Jyllandsposten',
'1871-09-16', '1937-12-21', '59-14', 'Århus');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'morgenavisenjyllandsposten'), 
    'Jyllands-Posten',
'1937-12-22', '1969-09-17', '59-14', 'Århus');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'morgenavisenjyllandsposten'), 
    'Morgenavisen Jyllands-Posten',
'1969-09-18', NULL, '59-14', 'Århus');

-- This creates a batch
INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400026952148, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'morgenavisenjyllandsposten'));
-- Creates the shipped status
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';

-- Add the single film that's needed for the check
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400026952148), '2002-01-21', '2002-01-31');

INSERT INTO Order_ (OrderId, status) VALUES (3, 'Packing completed');
INSERT INTO OrderLine (OrderRowId, NewsPaperRowId, OptionB1, OptionB2, OptionB3, OptionB4, 
    OptionB5, OptionB6, OptionB7, OptionB8, OptionB9) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 3), 
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'morgenavisenjyllandsposten'),
    true, true, true, true, true, true, true, true, true);
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 3), 
    (SELECT RowId FROM OrderLine LIMIT 1), 
    (SELECT RowId FROM Batch WHERE BatchId = 400026952148));

-- Add test data for batch date interval
INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'),
    'Børsen', '1899-10-01', '1970-08-28', '1-177', 'København'); 

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'),
    'Det nye Børsen', '1970-09-01', '1972-01-31', '1-177', 'København');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'),
    'Børsen', '1972-02-01', '1-177', 'København');

INSERT INTO Batch (BatchId, CartonNumber, NewsPaperRowId) VALUES (400022028242, 123,
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'));
-- Yes deliberately mangle the date ordering. 
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028242), '1920-10-01', '1940-10-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028242), '1899-10-01', '1900-10-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028242), '1910-10-01', '1919-10-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028242), '1971-10-01', '1971-12-17');
INSERT INTO Order_ (OrderId, status) VALUES (1, 'Packing completed');
INSERT INTO OrderLine (OrderRowId, NewsPaperRowId, OptionB1, OptionB2, OptionB3, OptionB4, 
    OptionB5, OptionB6, OptionB7, OptionB8, OptionB9) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 1), 
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'boersen'),
    true, true, true, true, true, true, true, true, true);
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 1), 
    (SELECT RowId FROM OrderLine LIMIT 1), 
    (SELECT RowId FROM Batch WHERE BatchId = 400022028242));


-- Order information for batch number **41
INSERT INTO Order_ (OrderId, status) VALUES (2, 'Packing completed');

INSERT INTO OrderLine (OrderRowId, NewsPaperRowId, OptionB1, OptionB2, OptionB3, OptionB4,
    OptionB5, OptionB6, OptionB7, OptionB8, OptionB9) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 2),
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'adresseavisen1759'),
    true, true, true, true, true, true, true, true, true);

INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 2),
    (SELECT RowId FROM OrderLine LIMIT 1),
    (SELECT RowId FROM Batch WHERE BatchId = 400022028241));

-- Performance test full batch
INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028245, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
  (SELECT RowId FROM Order_ WHERE OrderId = 2),
  (SELECT RowId FROM OrderLine LIMIT 1),
  (SELECT RowId FROM Batch WHERE BatchId = 400022028245));
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1795-06-16', '1796-05-30');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1796-05-30', '1797-05-14');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1797-05-14', '1798-04-28');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1798-04-28', '1799-04-12');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1799-04-12', '1800-03-27');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1800-03-27', '1801-03-11');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1801-03-11', '1802-02-23');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1802-02-23', '1803-02-07');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1803-02-07', '1804-01-22');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1804-01-22', '1805-01-05');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1805-01-05', '1805-12-20');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1805-12-20', '1806-12-04');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1806-12-04', '1807-11-18');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028245), '1807-11-18', '1808-11-01');

INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028243, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-12' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028243), '1795-06-16', '1795-06-17');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028243), '1795-06-18', '1795-06-19');

-- Component test batches
INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028250, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     (SELECT RowId FROM OrderLine LIMIT 1),
     (SELECT RowId FROM Batch WHERE BatchId = 400022028250));
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-06-16', '1795-07-10');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-07-10', '1795-08-03');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-08-03', '1795-08-27');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-08-27', '1795-09-20');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-09-20', '1795-10-14');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-10-14', '1795-11-07');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-11-07', '1795-12-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-12-01', '1795-12-25');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1795-12-25', '1796-01-18');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028250), '1796-01-18', '1796-02-11');

INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028251, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     (SELECT RowId FROM OrderLine LIMIT 1),
     (SELECT RowId FROM Batch WHERE BatchId = 400022028251));
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-06-16', '1795-07-10');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-07-10', '1795-08-03');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-08-03', '1795-08-27');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-08-27', '1795-09-20');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-09-20', '1795-10-14');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-10-14', '1795-11-07');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-11-07', '1795-12-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-12-01', '1795-12-25');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1795-12-25', '1796-01-18');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028251), '1796-01-18', '1796-02-11');

INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028252, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     (SELECT RowId FROM OrderLine LIMIT 1),
     (SELECT RowId FROM Batch WHERE BatchId = 400022028252));
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-06-16', '1795-07-10');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-07-10', '1795-08-03');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-08-03', '1795-08-27');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-08-27', '1795-09-20');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-09-20', '1795-10-14');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-10-14', '1795-11-07');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-11-07', '1795-12-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-12-01', '1795-12-25');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1795-12-25', '1796-01-18');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028252), '1796-01-18', '1796-02-11');

INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028253, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     (SELECT RowId FROM OrderLine LIMIT 1),
     (SELECT RowId FROM Batch WHERE BatchId = 400022028253));
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-06-16', '1795-07-10');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-07-10', '1795-08-03');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-08-03', '1795-08-27');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-08-27', '1795-09-20');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-09-20', '1795-10-14');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-10-14', '1795-11-07');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-11-07', '1795-12-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-12-01', '1795-12-25');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1795-12-25', '1796-01-18');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028253), '1796-01-18', '1796-02-11');

INSERT INTO batch (batchid, cartonnumber, NewsPaperRowId) values (400022028254, 0, (SELECT rowid FROM newspaper WHERE NewsPaperId = 'adresseavisen1759'));
INSERT INTO batchstatus (statusrowId, batchrowId, Created) SELECT rowId, LASTVAL(), '2013-11-11' FROM status WHERE name='Batch shipped to supplier';
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     (SELECT RowId FROM OrderLine LIMIT 1),
     (SELECT RowId FROM Batch WHERE BatchId = 400022028254));
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-06-16', '1795-07-10');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-07-10', '1795-08-03');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-08-03', '1795-08-27');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-08-27', '1795-09-20');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-09-20', '1795-10-14');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-10-14', '1795-11-07');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-11-07', '1795-12-01');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-12-01', '1795-12-25');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1795-12-25', '1796-01-18');
INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES ((SELECT RowId FROM Batch WHERE BatchId = 400022028254), '1796-01-18', '1796-02-11');

-- Test batches for trigger tests
INSERT INTO batch (batchid, cartonnumber) values (1001, 201);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';

INSERT INTO batch (batchid, cartonnumber) values (1002, 202);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch added to shipping container') WHERE rowId = LASTVAL();

INSERT INTO batch (batchId, cartonnumber) values (1003, 203);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch added to shipping container') WHERE rowId = LASTVAL();
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch shipped to supplier') WHERE rowId = LASTVAL();

INSERT INTO batch (batchId, cartonnumber) values (1004, 204);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch added to shipping container') WHERE rowId = LASTVAL();
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch shipped to supplier') WHERE rowId = LASTVAL();
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch received from supplier') WHERE rowId = LASTVAL();

INSERT INTO batch (batchId, cartonnumber) values (321123, 205);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch added to shipping container') WHERE rowId = LASTVAL();
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch shipped to supplier') WHERE rowId = LASTVAL();

INSERT INTO batch (batchId, cartonnumber) values (1337, 206);
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT rowId, LASTVAL() from status WHERE name='Initial';
INSERT INTO batchstatus (statusrowId, batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch added to shipping container') WHERE rowId = LASTVAL();
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch shipped to supplier') WHERE rowId = LASTVAL();
INSERT INTO batchstatus (statusrowId,batchrowId) SELECT 1,batchrowId from batchstatus WHERE rowId = LASTVAL();
UPDATE batchstatus SET statusrowId = (SELECT rowId from status WHERE "name"='Batch approved') WHERE rowId = LASTVAL();

-- Data for batches used for tests in Mediestream aviser
INSERT INTO newspaper (newspaperid) VALUES ('aktuelt');
INSERT INTO newspaper (newspaperid) VALUES ('berlingsketidende');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'), 'Kiøbenhavnske Danske Post-Tidender', '1749-01-03', '1762-01-01', '1-30', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'), 'De til Forsendelse med Posten allene privilegerede Kiøbenhavnske Tidender', '1762-01-04', '1808-09-30', '1-30', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'), 'Den til Forsendelse med de Kongelige Rideposter privilegerede Danske Statstidende', '1808-10-03', '1832-12-31', '1-30', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'), 'Den til Forsendelse med de Kongelige Brevposter privilegerede Berlingske Politiske og Avertissementstidende', '1833-01-01', '1935-12-31', '1-30', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'), 'Berlingske Tidende', '1936-01-01', '2011-01-25', '1-30', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'), 'Berlingske', '2011-01-26', NULL, '1-30', 'København');

INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Socialisten', '1871-07-21', '1874-05-09', '1-137', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Social-Demokraten', '1874-05-10', '1959-03-31', '1-137', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Aktuelt', '1959-04-01', '1978-01-16', '1-137', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Lands-avisen Aktuelt', '1978-01-17', '1982-12-31', '1-137', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Aktuelt', '1983-01-01', '1987-05-02', '1-137', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Det Fri Aktuelt', '1987-05-02', '1997-08-07', '1-137', 'København'),
((SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'), 'Aktuelt', '1997-08-08', '2001-04-06', '1-137', 'København');

INSERT INTO batch (batchid, cartonnumber, newspaperrowid) VALUES (400026951974, 1, (SELECT rowid FROM newspaper WHERE newspaperid = 'berlingsketidende'));
INSERT INTO batch (batchid, cartonnumber, newspaperrowid) VALUES (400026952091, 1, (SELECT rowid FROM newspaper WHERE newspaperid = 'aktuelt'));

INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES 
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1749-01-03', '1749-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1750-01-01', '1750-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1751-01-01', '1751-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1752-01-01', '1752-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1753-01-01', '1753-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1754-01-01', '1754-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1755-01-01', '1755-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1756-01-01', '1756-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1757-01-01', '1757-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1758-01-01', '1758-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1759-01-01', '1759-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1760-01-01', '1760-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1761-01-01', '1761-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1762-01-01', '1762-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1763-01-01', '1763-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1764-01-01', '1764-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1765-01-01', '1765-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1766-01-01', '1766-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026951974), '1767-01-01', '1767-12-31');

INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES 
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-07-01', '1974-07-15'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-07-16', '1974-07-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-08-01', '1974-08-15'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-08-16', '1974-08-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-09-01', '1974-09-15'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-09-16', '1974-09-30'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-10-01', '1974-10-15'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-10-16', '1974-10-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-11-01', '1974-11-15'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-11-16', '1974-11-30'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-12-01', '1974-12-15'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1974-12-16', '1974-12-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1975-01-01', '1975-01-31'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1975-02-01', '1975-02-28'),
((SELECT RowId FROM Batch WHERE BatchId = 400026952091), '1975-03-01', '1975-03-31');

INSERT INTO OrderLine (OrderRowId, NewsPaperRowId, OptionB1, OptionB2, OptionB3, OptionB4,
    OptionB5, OptionB6, OptionB7, OptionB8, OptionB9) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 2),
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'berlingsketidende'),
    false, true, true, false, false, false, false, false, false);
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     CURRVAL('orderline_rowid_seq'),
     (SELECT RowId FROM Batch WHERE BatchId = 400026951974));


INSERT INTO OrderLine (OrderRowId, NewsPaperRowId, OptionB1, OptionB2, OptionB3, OptionB4,
    OptionB5, OptionB6, OptionB7, OptionB8, OptionB9) VALUES (
    (SELECT RowId FROM Order_ WHERE OrderId = 2),
    (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'aktuelt'),
    true, false, true, false, false, false, true, false, false);
INSERT INTO OrderBatch (OrderRowId, OrderLineRowId, BatchRowId) VALUES (
     (SELECT RowId FROM Order_ WHERE OrderId = 2),
     CURRVAL('orderline_rowid_seq'),
     (SELECT RowId FROM Batch WHERE BatchId = 400026952091));

INSERT INTO batchstatus (batchrowid, statusrowid) VALUES 
((SELECT rowid FROM batch where batchid = 400026951974), 2),
((SELECT rowid FROM batch where batchid = 400026951974), 3),
((SELECT rowid FROM batch where batchid = 400026951974), 7),
((SELECT rowid FROM batch where batchid = 400026951974), 5);

INSERT INTO batchstatus (batchrowid, statusrowid) VALUES 
((SELECT rowid FROM batch where batchid = 400026952091), 2),
((SELECT rowid FROM batch where batchid = 400026952091), 3),
((SELECT rowid FROM batch where batchid = 400026952091), 7),
((SELECT rowid FROM batch where batchid = 400026952091), 5);
