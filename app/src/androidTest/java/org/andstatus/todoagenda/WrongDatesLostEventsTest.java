package com.luteapp.todoagenda;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.widget.CalendarEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yvolk@yurivolkov.com
 */
public class WrongDatesLostEventsTest extends BaseWidgetTest {

    /**
     * https://github.com/plusonelabs/calendar-widget/issues/205
     */
    @Test
    public void testIssue205() {
        final String method = "testIssue205";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.wrong_dates_lost_events);
        provider.addResults(inputs);

        playResults(method);
        assertEquals("Number of entries", 11, getFactory().getWidgetEntries().size());
        assertEquals("On Saturday", "Maker Fair", ((CalendarEntry) getFactory().getWidgetEntries().get(4)).getEvent().getTitle());
        assertEquals("On Saturday", 6, getFactory().getWidgetEntries().get(4).entryDate.getDayOfWeek());
        assertEquals("On Sunday", "Ribakovs", ((CalendarEntry) getFactory().getWidgetEntries().get(7)).getEvent().getTitle());
        assertEquals("On Sunday", 7, getFactory().getWidgetEntries().get(7).entryDate.getDayOfWeek());
    }
}
