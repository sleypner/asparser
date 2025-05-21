package com.sleypner.parserarticles.parsing.raw;

import com.sleypner.parserarticles.model.source.entityes.Events;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class EventsParser implements EntitiesParser {
    private String url = "https://asterios.tm/index.php?cmd=rss";
    private String baseUrl = "https://asterios.tm/index.php?cmd=rss&serv={x}&filter={y}";
    private String name = "events";
    private Events events;

    @Override
    public List<URI> getUris() {
        List<String> filterList = new ArrayList<>(List.of(
                "epic", "keyboss", "siege", "tw"
        ));
        List<Integer> serverList = new ArrayList<>(List.of(
                3, 8, 0, 2, 6
        ));
        List<URI> listUri = new ArrayList<>();
        for (Integer serv : serverList) {
            String url = baseUrl.replace("{x}", serv.toString());
            for(String filter : filterList){
                String urlWithFilter = url.replace("{y}", filter);
                URI uri = URI.create(urlWithFilter);
                listUri.add(uri);
            }
        }
        return listUri;
    }
}
