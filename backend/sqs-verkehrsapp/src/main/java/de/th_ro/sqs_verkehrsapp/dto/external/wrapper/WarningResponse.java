package de.th_ro.sqs_verkehrsapp.dto.external.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.th_ro.sqs_verkehrsapp.dto.external.WarningDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WarningResponse {

    @JsonProperty("warning")
    private List<WarningDto> warnings;
}
