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


INSERT INTO NewsPaperTitle (NewsPaperRowId, Name, FromDate, ToDate, DDA, PublicationLocation) VALUES 
( (SELECT RowId FROM NewsPaper WHERE NewsPaperId = 'adresseavisen1759'), 
    'Kiøbenhavns Kongelig alene priviligerede Adresse-Contoirs Efterretninger',
'1759-05-04', '1854-12-30', '1-35', 'København');

INSERT INTO Film (BatchRowId, FromDate, ToDate) VALUES (
    (SELECT RowId FROM Batch WHERE BatchId = 400022028241), '1795-06-13', '1795-06-15');


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