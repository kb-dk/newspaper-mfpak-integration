package dk.statsbiblioteket.newspaper.mfpakintegration.database;


/**
 * Exception class to indicate that the database's content is inconsistent.  
 */
public class InconsistentDatabaseException extends RuntimeException {

    public InconsistentDatabaseException(String message) {
        super(message);
    }
}
