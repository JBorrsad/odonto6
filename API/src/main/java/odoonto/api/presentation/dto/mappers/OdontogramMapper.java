package odoonto.api.presentation.dto.mappers;

import org.springframework.stereotype.Component;
import odoonto.api.domain.models.Odontogram;
import odoonto.api.presentation.dto.OdontogramDto;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OdontogramMapper {

    public OdontogramDto toDto(Odontogram od) {
        if (od == null) return null;

        // convert Map<String, ToothRecord> ➔ Map<Integer, Map<String, String>>
        Map<Integer, Map<String, String>> map = od.getTeeth().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> Integer.valueOf(e.getKey()),
                        e -> e.getValue().getFaces().entrySet().stream()
                                .collect(Collectors.toMap(
                                        f -> f.getKey(), // Ya es String, no necesita name()
                                        f -> f.getValue().name()
                                ))
                ));

        return new OdontogramDto(map);
    }
}
