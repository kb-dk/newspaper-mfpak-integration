package dk.statsbiblioteket.newspaper.mfpakintegration.database;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;

import java.util.Date;

public class NewspaperDateRangeTest {

    @Test
    public void testDataRange() {
        Long t1 = 1234567890L;
        Date fromDate = new Date(t1);
        Date toDate = new Date(t1 + 100);
        Date inBetweenDate = new Date(t1 + 50);
        Date priorDate = new Date(t1 - 5);
        Date afterDate = new Date(t1 + 110);
        
        NewspaperDateRange testRange = new NewspaperDateRange(fromDate, toDate);
        assertEquals(fromDate, testRange.getFromDate());
        assertEquals(toDate, testRange.getToDate());
        assertTrue(testRange.isIncluded(inBetweenDate));
        assertTrue(testRange.isIncluded(fromDate));
        assertTrue(testRange.isIncluded(toDate));
        assertFalse(testRange.isIncluded(priorDate));
        assertFalse(testRange.isIncluded(afterDate));
    }
    
}
