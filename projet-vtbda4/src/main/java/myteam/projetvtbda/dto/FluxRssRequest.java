package myteam.projetvtbda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FluxRssRequest {
    private String nomFlux;
    private String urlFlux;
    private String description;
}