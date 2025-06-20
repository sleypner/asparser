package dev.sleypner.asparser.domain.model.metamodels;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDateTime;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OnlineStatus.class)
public class OnlineStatus_ {
    public static volatile SingularAttribute<OnlineStatus, Integer> id;
    public static volatile SingularAttribute<OnlineStatus, LocalDateTime> createdDate;
    public static volatile SingularAttribute<OnlineStatus, String> serverName;
    public static volatile SingularAttribute<OnlineStatus, Short> online;
    public static volatile SingularAttribute<OnlineStatus, Short> onTrade;

    public static final String ID = "id";
    public static final String CREATED_DATE = "createdDate";
    public static final String SERVER_NAME = "serverName";
    public static final String ONLINE = "online";
    public static final String ON_TRADE = "onTrade";

    public OnlineStatus_() {
    }

    public static SingularAttribute<OnlineStatus, Integer> getId() {
        return id;
    }

    public static void setId(SingularAttribute<OnlineStatus, Integer> id) {
        OnlineStatus_.id = id;
    }

    public static SingularAttribute<OnlineStatus, LocalDateTime> getCreateDate() {
        return createdDate;
    }

    public static void setCreateDate(SingularAttribute<OnlineStatus, LocalDateTime> createDate) {
        OnlineStatus_.createdDate = createDate;
    }

    public static SingularAttribute<OnlineStatus, String> getServerName() {
        return serverName;
    }

    public static void setServerName(SingularAttribute<OnlineStatus, String> serverName) {
        OnlineStatus_.serverName = serverName;
    }

    public static SingularAttribute<OnlineStatus, Short> getOnline() {
        return online;
    }

    public static void setOnline(SingularAttribute<OnlineStatus, Short> online) {
        OnlineStatus_.online = online;
    }

    public static SingularAttribute<OnlineStatus, Short> getOnTrade() {
        return onTrade;
    }

    public static void setOnTrade(SingularAttribute<OnlineStatus, Short> onTrade) {
        OnlineStatus_.onTrade = onTrade;
    }
}

