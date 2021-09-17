package com.luteapp.todoagenda;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yvolk@yurivolkov.com
 */
public class DuplicateEventsTest extends BaseWidgetTest {

    /**
     * https://github.com/plusonelabs/calendar-widget/issues/354
     */
    @Test
    public void testIssue354() {
        final String method = "testIssue354";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.duplicates);
        provider.addResults(inputs);

        playResults(method);
        assertEquals("Number of entries", 40, getFactory().getWidgetEntries().size());
    }
}
