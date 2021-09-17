package com.luteapp.todoagenda;

import com.luteapp.todoagenda.prefs.ApplicationPreferences;
import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.widget.DayHeader;
import com.luteapp.todoagenda.widget.LastEntry;
import com.luteapp.todoagenda.widget.WidgetEntry;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author yvolk@yurivolkov.com
 */
public class MultidayAllDayEventTest extends BaseWidgetTest {

    @Test
    public void testInsidePeriod() {
        final String method = "testInsidePeriod";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.multi_day);
        DateTime now = new DateTime(2015, 8, 30, 0, 0, 1, 0, getSettings().clock().getZone());
        inputs.setExecutedAt(now);
        provider.addResults(inputs);

        int dateRange = 30;
        provider.startEditingPreferences();
        ApplicationPreferences.setEventRange(provider.getContext(), dateRange);
        provider.savePreferences();
        playResults(method);

        DateTime today = now.withTimeAtStartOfDay();
        DateTime endOfRangeTime = today.plusDays(dateRange);
        int dayOfEventEntryPrev = 0;
        int dayOfHeaderPrev = 0;
        for (int ind = 0; ind < getFactory().getWidgetEntries().size(); ind++) {
            WidgetEntry entry = getFactory().getWidgetEntries().get(ind);
            String logMsg = method + "; " + String.format("%02d ", ind) + entry.toString();
            if (entry.entryDay.isBefore(today)) {
                fail("Is present before today " + logMsg);
            }
            if (entry.entryDay.isAfter(endOfRangeTime)) {
                fail("After end of range " + logMsg);
            }
            int dayOfEntry = entry.entryDay.getDayOfYear();
            if (entry instanceof DayHeader) {
                if (dayOfHeaderPrev == 0) {
                    if (entry.entryDate.withTimeAtStartOfDay().isAfter(today)) {
                        fail("No today's header " + logMsg);
                    }
                } else {
                    assertEquals("No header " + logMsg, dayOfHeaderPrev + 1, dayOfEntry);
                }
                dayOfHeaderPrev = dayOfEntry;
            } else if (entry instanceof LastEntry) {
                assertEquals(LastEntry.LastEntryType.LAST, ((LastEntry) entry).type);
            } else {
                if (dayOfEventEntryPrev == 0) {
                    if (entry.entryDate.withTimeAtStartOfDay().isAfter(today)) {
                        fail("Today not filled " + logMsg);
                    }
                } else {
                    assertEquals("Day not filled " + logMsg, dayOfEventEntryPrev + 1, dayOfEntry);
                }
                dayOfEventEntryPrev = dayOfEntry;
            }
        }
        assertEquals("Wrong last day header " + method, endOfRangeTime.getDayOfYear(), dayOfHeaderPrev);
        assertEquals("Wrong last filled day " + method, endOfRangeTime.getDayOfYear(), dayOfEventEntryPrev);
    }
}
