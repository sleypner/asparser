package com.sleypner.parserarticles.parsing.raw;

import com.sleypner.parserarticles.model.source.entityes.OnlineStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@Data
public class OnlineParser implements EntitiesParser {
    private String url = "https://asterios.tm/index.php?js=1";
    private OnlineStatus onlineStatus;
    private String name = "online";

    @Override
    public List<URI> getUris() {
        return List.of(URI.create(url));
    }
}
