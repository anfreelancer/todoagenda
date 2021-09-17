package com.luteapp.todoagenda;

import com.luteapp.todoagenda.prefs.FilterMode;
import com.luteapp.todoagenda.prefs.TasksWithoutDates;
import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.widget.WidgetEntryPosition;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yvolk@yurivolkov.com
 */
public class RealTasksTest extends BaseWidgetTest {

    @Test
    public void testTasksWithoutStartAndDueDates() {
        final String method = "testTasksWithoutStartAndDueDates";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.real_tasks);
        provider.addResults(inputs);
        playResults(method);

        assertTrue("No End of list entries although filters are off",
                getFactory().getWidgetEntries().stream()
                .anyMatch(entry -> entry.entryPosition == WidgetEntryPosition.END_OF_LIST));

        getSettings().setFilterMode(FilterMode.DEBUG_FILTER)
        .setTaskWithoutDates(TasksWithoutDates.HIDE);

        playResults(method);

        assertFalse("End of list entries although filters are on",
                getFactory().getWidgetEntries().stream()
                        .anyMatch(entry -> entry.entryPosition == WidgetEntryPosition.END_OF_LIST));
    }
}
