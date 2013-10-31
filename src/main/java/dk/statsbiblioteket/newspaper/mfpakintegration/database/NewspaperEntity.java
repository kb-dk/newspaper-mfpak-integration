package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import java.util.Date;

/**
 * Class to encapsulate the information about a newspaper entity.
 * This should not be understood as a newspaper on a given date, but a series of newspapers with the same title
 * The information encapsulated is: 
 * - newspaperID, the ID of the newspaper 
 * - newspaperTitle, the title of the newspaperID in the period 
 * - publicationLocation, the location where the newspaper were published
 * - startDate, the date that the first newspaper were released
 * - endDate, the last that the last newspaper were released
 */
public class NewspaperEntity {

    private String newspaperID;
    private String newspaperTitle;
    private String publicationLocation;
    private Date startDate;
    private Date endDate;
    
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
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
