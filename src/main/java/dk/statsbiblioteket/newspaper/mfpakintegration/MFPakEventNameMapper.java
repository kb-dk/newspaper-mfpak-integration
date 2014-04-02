package dk.statsbiblioteket.newspaper.mfpakintegration;

public class MFPakEventNameMapper {

    private static final String INITIAL_EVENTID = "Initial";
    private static final String INITIAL_STATUS = "Initial";

    private static final String ADDED_TO_SHIPPING_CONTAINER_EVENTID = "Added_to_shipping_container";
    private static final String BATCH_ADDED_TO_SHIPPING_CONTAINER_STATUS = "Batch added to shipping container";

    private static final String SHIPPED_TO_SUPPLIER_EVENTID = "Shipped_to_supplier";
    private static final String BATCH_SHIPPED_TO_SUPPLIER_STATUS = "Batch shipped to supplier";

    private static final String SHIPPED_FROM_SUPPLIER_EVENTID = "Shipped_from_supplier";
    private static final String BATCH_SHIPPED_FROM_SUPPLIER_STATUS = "Batch shipped from supplier";

    private static final String RECEIVED_FROM_SUPPLIER_EVENTID = "Received_from_supplier";
    private static final String BATCH_RECEIVED_FROM_SUPPLIER_STATUS = "Batch received from supplier";

    private static final String FOLLOW_UP_EVENTID = "FollowUp";
    private static final String BATCH_FOLLOW_UP_STATUS = "Batch follow-up";

    private static final String APPROVED_EVENTID = "Approved";
    private static final String BATCH_APPROVED_STATUS = "Batch approved";

    public static boolean isEventId(String event) {
        switch (event) {
            case INITIAL_EVENTID:
            case ADDED_TO_SHIPPING_CONTAINER_EVENTID:
            case SHIPPED_TO_SUPPLIER_EVENTID:
            case SHIPPED_FROM_SUPPLIER_EVENTID:
            case RECEIVED_FROM_SUPPLIER_EVENTID:
            case FOLLOW_UP_EVENTID:
            case APPROVED_EVENTID:
                return true;
            default:
                return false;
        }
    }

    public static String eventIdToMFPakStatus(String eventID) {
        switch (eventID) {
            case INITIAL_EVENTID:
                return INITIAL_EVENTID;
            case ADDED_TO_SHIPPING_CONTAINER_EVENTID:
                return BATCH_ADDED_TO_SHIPPING_CONTAINER_STATUS;
            case SHIPPED_TO_SUPPLIER_EVENTID:
                return BATCH_SHIPPED_TO_SUPPLIER_STATUS;
            case SHIPPED_FROM_SUPPLIER_EVENTID:
                return BATCH_SHIPPED_FROM_SUPPLIER_STATUS;
            case RECEIVED_FROM_SUPPLIER_EVENTID:
                return BATCH_RECEIVED_FROM_SUPPLIER_STATUS;
            case FOLLOW_UP_EVENTID:
                return BATCH_FOLLOW_UP_STATUS;
            case APPROVED_EVENTID:
                return BATCH_APPROVED_STATUS;
            default:
                throw new IllegalArgumentException("Unknown eventID " + eventID);
        }
    }

    public static String mfPakStatusToEventId(String status) {
        String eventID;
        switch (status) {
            case INITIAL_STATUS:
                eventID = INITIAL_EVENTID;
                break;
            case BATCH_ADDED_TO_SHIPPING_CONTAINER_STATUS:
                eventID = ADDED_TO_SHIPPING_CONTAINER_EVENTID;
                break;
            case BATCH_SHIPPED_TO_SUPPLIER_STATUS:
                eventID = SHIPPED_TO_SUPPLIER_EVENTID;
                break;
            case BATCH_SHIPPED_FROM_SUPPLIER_STATUS:
                eventID = SHIPPED_FROM_SUPPLIER_EVENTID;
                break;
            case BATCH_RECEIVED_FROM_SUPPLIER_STATUS:
                eventID = RECEIVED_FROM_SUPPLIER_EVENTID;
                break;
            case BATCH_FOLLOW_UP_STATUS:
                eventID = FOLLOW_UP_EVENTID;
                break;
            case BATCH_APPROVED_STATUS:
                eventID = APPROVED_EVENTID;
                break;
            default:
                throw new IllegalArgumentException("Unknown batch status " + status);
        }
        return eventID;
    }
}
