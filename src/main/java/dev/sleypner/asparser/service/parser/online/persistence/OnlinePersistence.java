package dev.sleypner.asparser.service.parser.online.persistence;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.dto.OnlineChart;

import java.time.LocalDateTime;
import java.util.List;

public interface OnlinePersistence {

    OnlineStatus save(OnlineStatus status);

    List<OnlineStatus> getAll();

    List<OnlineChart> getByTimePeriod(LocalDateTime periodStart, LocalDateTime periodEnd, Integer interval);

}
