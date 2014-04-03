package dk.statsbiblioteket.newspaper.mfpakintegration;

public enum EventID {
    INITIAL("Initial", "Initial"),
    ADDED_TO_SHIPPING_CONTAINER("Added_to_shipping_container", "Batch added to shipping container"),
    SHIPPED_TO_SUPPLIER("Shipped_to_supplier","Batch shipped to supplier"),
    SHIPPED_FROM_SUPPLIER("Shipped_from_supplier","Batch shipped from supplier"),
    RECEIVED_FROM_SUPPLIER("Received_from_supplier","Batch received from supplier"),
    FOLLOW_UP("FollowUp","Batch follow-up"),
    APPROVED("Approved","Batch approved");


    private final String formal;
    private final String mfpak;

    EventID(String formal, String mfpak) {

        this.formal = formal;
        this.mfpak = mfpak;
    }

    public static EventID fromMfPak(String mfpakEventID){
        for (EventID eventID : EventID.values()) {
            if (eventID.mfpak.equals(mfpakEventID)){
                return eventID;
            }
        }
        return null;
    }

    public static EventID fromFormal(String formalEventID) {
        for (EventID eventID : EventID.values()) {
            if (eventID.formal.equals(formalEventID)) {
                return eventID;
            }
        }
        return null;
    }

    public String getFormal() {
        return formal;
    }

    public String getMfpak() {
        return mfpak;
    }
}
