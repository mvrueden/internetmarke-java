package de.marskuh;

import java.time.ZoneId;

public interface Settings {
    String KEY_PHASE = "1";
    ZoneId ZONE_ID = ZoneId.of("Europe/Berlin");
    String DATE_FORMAT = "ddMMyyyy-HHmmss";
}
