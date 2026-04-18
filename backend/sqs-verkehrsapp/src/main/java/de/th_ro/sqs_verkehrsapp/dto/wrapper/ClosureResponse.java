package de.th_ro.sqs_verkehrsapp.dto.wrapper;

import de.th_ro.sqs_verkehrsapp.dto.ClosureDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClosureResponse {

    private List<ClosureDto> closures;
}
