package de.th_ro.sqs_verkehrsapp.application.port.out;

import java.util.List;

public interface AvailableRoadCachePort {

    void saveAll(List<String> roadIds);

    List<String> findAll();
}
