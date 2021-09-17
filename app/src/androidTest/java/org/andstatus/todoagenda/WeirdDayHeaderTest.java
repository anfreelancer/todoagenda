package com.luteapp.todoagenda;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.widget.WidgetEntryPosition;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yvolk@yurivolkov.com
 */
public class WeirdDayHeaderTest extends BaseWidgetTest {

    @Test
    public void testWeirdDayHeader() {
        final String method = "testWeirdDayHeader";
        QueryResultsStorage inputs = provider.loadResultsAndSettings(
                com.luteapp.todoagenda.tests.R.raw.weird_day_header);
        provider.addResults(inputs);
        playResults(method);

        assertPosition(0, WidgetEntryPosition.END_OF_LIST_HEADER);
        assertPosition(1, WidgetEntryPosition.END_OF_LIST);
        assertPosition(2, WidgetEntryPosition.END_OF_LIST);
        assertPosition(3, WidgetEntryPosition.LIST_FOOTER);

    }

}
