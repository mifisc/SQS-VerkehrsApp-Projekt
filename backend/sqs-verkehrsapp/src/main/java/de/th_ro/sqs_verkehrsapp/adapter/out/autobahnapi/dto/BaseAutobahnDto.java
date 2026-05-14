package de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class BaseAutobahnDto {
    private String identifier;
    private String title;
    private String subtitle;
    private List<String> description;
    private CoordinateDto coordinate;
}
