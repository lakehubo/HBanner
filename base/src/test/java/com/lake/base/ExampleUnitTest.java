package com.lake.base;

import com.lake.base.utils.SharedPreferencesUtils;
import com.lake.base.utils.TimeUtils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        long millis = TimeUtils.string2Millis("20210630230505", TimeUtils.getLogDataFormat());
        assertTrue("", TimeUtils.getTimeDistance(millis) > 30);
    }
}