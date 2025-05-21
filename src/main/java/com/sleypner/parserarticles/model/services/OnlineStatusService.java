package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.OnlineStatus;
import com.sleypner.parserarticles.model.source.other.OnlineChart;

import java.time.LocalDateTime;
import java.util.List;

public interface OnlineStatusService {

    OnlineStatus save(OnlineStatus status);

    List<OnlineStatus> getAll();

    List<OnlineChart> getByTimePeriod(LocalDateTime periodStart, LocalDateTime periodEnd, Integer interval);

    List<String> getServers();
}
