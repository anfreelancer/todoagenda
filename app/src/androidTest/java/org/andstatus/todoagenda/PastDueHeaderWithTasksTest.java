package com.luteapp.todoagenda;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.util.MyClock;
import com.luteapp.todoagenda.widget.CalendarEntry;
import com.luteapp.todoagenda.widget.LastEntry;
import com.luteapp.todoagenda.widget.TaskEntry;
import com.luteapp.todoagenda.widget.WidgetEntryPosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yvolk@yurivolkov.com
 */
public class PastDueHeaderWithTasksTest extends BaseWidgetTest {

    /**
     * https://github.com/plusonelabs/calendar-widget/issues/205
     */
    @Test
    public void testPastDueHeaderWithTasks() {
        final String method = "testPastDueHeaderWithTasks";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.past_due_header_with_tasks);
        provider.addResults(inputs);

        playResults(method);

        assertEquals("Past and Due header", MyClock.DATETIME_MIN, getFactory().getWidgetEntries().get(0).entryDate);
        assertEquals(WidgetEntryPosition.PAST_AND_DUE_HEADER, getFactory().getWidgetEntries().get(0).entryPosition);

        assertEquals("Past Calendar Entry", CalendarEntry.class, getFactory().getWidgetEntries().get(1).getClass());
        assertEquals("Due task Entry", TaskEntry.class, getFactory().getWidgetEntries().get(2).getClass());
        assertEquals("Due task Entry", dateTime(2019, 8, 1, 9, 0),
                (getFactory().getWidgetEntries().get(2)).entryDate);
        assertEquals("Tomorrow header", dateTime(2019, 8, 5),
                (getFactory().getWidgetEntries().get(3)).entryDate);

        assertEquals("Future task Entry", TaskEntry.class, getFactory().getWidgetEntries().get(6).getClass());
        assertEquals("Future task Entry", dateTime(2019, 8, 8, 21, 0),
                (getFactory().getWidgetEntries().get(6)).entryDate);

        assertEquals("End of list header", MyClock.DATETIME_MAX, getFactory().getWidgetEntries().get(7).entryDate);
        assertEquals(WidgetEntryPosition.END_OF_LIST_HEADER, getFactory().getWidgetEntries().get(7).entryPosition);

        assertEquals(WidgetEntryPosition.END_OF_LIST, getFactory().getWidgetEntries().get(8).entryPosition);

        assertEquals("Last Entry", LastEntry.LastEntryType.LAST,
                ((LastEntry) getFactory().getWidgetEntries().get(getFactory().getWidgetEntries().size() - 1)).type);
        assertEquals("Number of entries", 10, getFactory().getWidgetEntries().size());
    }
}
