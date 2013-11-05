package dk.statsbiblioteket.newspaper.mfpakintegration.database;


/**
 * Class to represent the notion of a newspaper title, this includes the date range in which it was valid.
 */
public class NewspaperTitle {

    private String title;
    private NewspaperDateRange dateRange;

    /**
     * Gets the title of the newspaper
     * @return String the title of the newspaper 
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the title of the newspaper 
     * @param title The title of the newspapers
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gets the NewspaperDateRange for which the newspaper title is valid
     * @return {@link NewspaperDateRange} the NewspaperDateRange for the title.  
     */
    public NewspaperDateRange getDateRange() {
        return dateRange;
    }
    
    /**
     * Sets the NewspaperDateRange for which the newspaper title is valid.
     * @param dateRange The {@link NewspaperDateRange} for the title  
     */
    public void setDateRange(NewspaperDateRange dateRange) {
        this.dateRange = dateRange;
    }

        
}
