package com.uimirror.framework.ed;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jayaram
 *         3/7/16.
 */
public class TestMapCollectors {

    @Test
    public void testCollectMap() throws Exception {
        List<String> ts = new ArrayList<>();
        ts.add("Test");
        ts.add("Test1");
        Map<String, String> collect = ts.stream()
                .collect(Collectors.toMap(o -> o, s -> s));
        assertThat(collect).hasSize(2);

    }
}
