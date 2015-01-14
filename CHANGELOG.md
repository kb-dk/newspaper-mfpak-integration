2.1
* Use newest version of framework
  * Retry talking to DOMS on 409 errors
  * Use POST not GET for Summa queries

2.0
* Update to newest version of item event framework. Note that MFPAK integration only works with batches, not general items.
* Configuration has been extended and changed and example config has been updated. Please update your configuration files.

1.10
* Update batch event framework dependencies to 1.10

1.9
* Update batch event framework dependencies to 1.9, fixing not finding work when events from MF-PAK is the first

1.8
1.7

* Cache batch context read from database.

1.6

* Use newest version (1.7) of batch event framework, which supports alternative triggers
* Implement MFPAK-based triggers
* Code and test cleanup

1.5

* Use version 1.6 of batch event framework
* Add checks of BatchContext objects
* Make sure that Batch objects delivered is marked as roundtrip 0
* Updated to newspaper-parent 1.2

1.4

* Updated to newspaper-parent 1.1, supporting a new test strategy
* Support reading the entire batch context only once from MFPAK
* Use newer batch event framework 1.5

1.3

* Updated to newspaper-batch-event-framework 1.4
* Fixed bug in getBatchNewspaperEntities

1.2

* Use newer newspaper-batch-event-framework
* Updated tests

1.1

* Use newer newspaper-batch-event-framework
* Read connection strings from configuration

1.0

* Added getBatchShipmentDate
* Change getNewspaperTitles to getNewspaperEntities
* Updated dependencies for process-monitor-datasource modules
* Update parent-pom version

0.2

* Added getBatchNewspaperTitles, getBatchDateRanges, getBatchNewspaperTitles and getBatchOptions DAO operations.

0.1

* Initial release
