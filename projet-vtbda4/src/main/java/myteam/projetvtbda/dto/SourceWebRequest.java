package myteam.projetvtbda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SourceWebRequest {
    private String nomSource;
    private String urlSite;
    private String description;
    private Map<String, String> selecteursCss;
    private String frequenceScraping;
}