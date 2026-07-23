package com.oasis25.pomodoro.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum WeatherCondition {
    CLEAR("맑음"),
    CLOUDY("흐림"),
    RAIN("비"),
    SNOW("눈"),
    THUNDERSTORM("천둥번개"),
    FOG("안개"),
    ETC("기타");

    private final String label;

    private static final Map<String, WeatherCondition> BY_LABEL = Arrays.stream(values())
            .collect(Collectors.toMap(WeatherCondition::getLabel, c -> c));

    WeatherCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static WeatherCondition fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        return BY_LABEL.get(label);
    }
}
