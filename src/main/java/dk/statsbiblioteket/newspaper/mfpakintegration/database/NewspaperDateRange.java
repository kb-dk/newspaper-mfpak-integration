package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.util.Date;


/**
 * Class to represent the date range of newspapers.
 * Dates are full inclusive (contrary to joda-time), so that the from date and to date is both included.  
 */
public class NewspaperDateRange {

    private final Date fromDate;
    private final Date toDate;
    
    /**
     * Constructor.
     * @param from, the start date in the range (inclusive)
     * @param to, the end date in the range (inclusive) 
     */
    public NewspaperDateRange(Date from, Date to) {
        this.fromDate = from;
        this.toDate = to;
    }
    
    /**
     * Gets the from date
     * @return Date, the first date in the range 
     */
    public Date getFromDate() {
        return fromDate;
    }
    
    /**
     * Gets the to date
     * @return Date, the last date in the range 
     */
    public Date getToDate() {
        return toDate;
    }
    
    /**
     * Determine if a date is included in the range. 
     * The range is inclusive meaning that if date is either the fromDate or toDate, the date is included.
     * @param date The date to check against the range.  
     * @return true if the date is included, false otherwise  
     */
    public boolean isIncluded(Date date) {
        boolean included = true;
        
        if((date.compareTo(fromDate) < 0) || (date.compareTo(toDate) > 0)) {
            included = false;
        }
        
        return included;
    }
    
}
