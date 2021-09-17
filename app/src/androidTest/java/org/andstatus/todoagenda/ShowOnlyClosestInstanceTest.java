package com.luteapp.todoagenda;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.widget.CalendarEntry;
import com.luteapp.todoagenda.widget.WidgetEntry;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author yvolk@yurivolkov.com
 */
public class ShowOnlyClosestInstanceTest extends BaseWidgetTest {

    @Test
    public void testShowOnlyClosestInstance() {
        final String method = "testShowOnlyClosestInstance";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.closest_event);
        provider.addResults(inputs);

        playResults(method);

        assertEquals("SnaphotDate", dateTime(2020, 2, 15),
                getSettings().clock().now().withTimeAtStartOfDay());

        List<? extends WidgetEntry> entries = getFactory().getWidgetEntries().stream()
                .filter(e -> e.getTitle().startsWith("Test event 2 that")).collect(Collectors.toList());
        assertEquals("Number of entries of the test event " + entries, 2, entries.size());
        assertNotEquals("Entries should have different IDs\n" + entries + "\n",
                ((CalendarEntry) entries.get(0)).getEvent().getEventId(),
                ((CalendarEntry) entries.get(1)).getEvent().getEventId());
    }
}