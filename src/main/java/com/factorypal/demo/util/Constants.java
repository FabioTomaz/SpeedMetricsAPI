package com.factorypal.demo.util;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {
    public static Set<Long> KNOWN_IDS = Stream.of(10L,11L).collect(Collectors.toSet());

}
