package com.luteapp.todoagenda;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yvolk@yurivolkov.com
 */
public class TodaysColorsEventsTest extends BaseWidgetTest {

    /**
     * https://github.com/plusonelabs/calendar-widget/issues/327
     */
    @Test
    public void testIssue327() {
        final String method = "testIssue327";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.todays_colors);
        provider.addResults(inputs);

        playResults(method);
        assertEquals("Number of entries", 43, getFactory().getWidgetEntries().size());
    }
}
