package dev.sleypner.asparser.service.core.auth.log;

import dev.sleypner.asparser.domain.model.UserActionLog;

public interface UserActionLogsService {

    UserActionLog save(UserActionLog userActionLog);

}
