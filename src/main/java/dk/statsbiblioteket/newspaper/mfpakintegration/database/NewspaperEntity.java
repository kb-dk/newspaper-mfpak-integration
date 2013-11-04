package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.util.Date;

/**
 * Class to encapsulate the information about a newspaper entity.
 * This should not be understood as a newspaper on a given date, but a series of newspapers with the same title
 * The information encapsulated is: <br>
 * - newspaperID, the ID of the newspaper <br> 
 * - newspaperTitle, the title of the newspaperID in the period <br> 
 * - publicationLocation, the location where the newspaper were published <br>
 * - startDate, the date that the first newspaper were released <br>
 * - endDate, the last that the last newspaper were released <br>
 */
public class NewspaperEntity {

    private String newspaperID;
    private String newspaperTitle;
    private String publicationLocation;
    private NewspaperDateRange newspaperDateRange;
    
    public String getNewspaperID() {
        return newspaperID;
    }
    
    public void setNewspaperID(String newspaperID) {
        this.newspaperID = newspaperID;
    }
    
    public String getNewspaperTitle() {
        return newspaperTitle;
    }
    
    public void setNewspaperTitle(String newspaperTitle) {
        this.newspaperTitle = newspaperTitle;
    }
    
    public String getPublicationLocation() {
        return publicationLocation;
    }

    public void setPublicationLocation(String publicationLocation) {
        this.publicationLocation = publicationLocation;
    }
    
    public NewspaperDateRange getNewspaperDateRange() {
        return newspaperDateRange;
    }

    public void setNewspaperDateRange(NewspaperDateRange newspaperDateRange) {
        this.newspaperDateRange = newspaperDateRange;
    }
}
