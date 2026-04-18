package de.th_ro.sqs_verkehrsapp.external;

public enum DataSourceType {
    LIVE,
    CACHE,
    MIXED;

    public static DataSourceType combine(boolean hasLive, boolean hasCache) {
        if (hasLive && hasCache) {
            return MIXED;
        }
        if (hasLive) {
            return LIVE;
        }
        return CACHE;
    }
}
