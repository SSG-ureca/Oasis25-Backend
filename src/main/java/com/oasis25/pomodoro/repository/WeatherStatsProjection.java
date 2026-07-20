package com.oasis25.pomodoro.repository;

import com.oasis25.pomodoro.entity.WeatherCondition;

public interface WeatherStatsProjection {

    WeatherCondition getWeatherCondition();

    Long getTotalCount();

    Long getCompletedCount();

    Double getAvgFocusMinutes();
}
