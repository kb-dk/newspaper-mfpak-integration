newspaper-mfpak-integration
===========================

This component allows the newspaper-digitisation-process-monitor to pull information on the shipping status of batches directly from
the mfpak database.

### Architecture

The architecture is in two layers:
 1. The DAO layer which communicates with the database and is implemented in the class `MfPakDAO`
 2. The API layer which implements `DataSource` and is implemented in the class `MfPakDataSource`

### Configuration

Configuration is provided via the helper class `MfPakConfiguration`. An instance of this class should be configured to contain the credentials for
talking with the mfpak database, and passed to the `MfPakDataSource` constructor.