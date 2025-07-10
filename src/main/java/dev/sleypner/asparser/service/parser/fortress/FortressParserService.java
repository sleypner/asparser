package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.*;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.HtmlDocument;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.sleypner.asparser.util.StringExtension.trimAll;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FortressParserService implements Parser<FortressData> {

    private final Environment environment;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Set<FortressData> parse(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        Elements elements = document.select("center");

        String fullServer = document.select("#serv > option[selected]").text();
        String targetServerName = trimAll(fullServer);
        Server server = htmlDocument.getServers().stream()
                .filter(s -> trimAll(s.getName() + s.getRates()).equalsIgnoreCase(trimAll(targetServerName)))
                .findFirst()
                .orElse(null);


        return elements.stream()
                .map(element -> parseFortress(element, server))
                .collect(Collectors.toSet());
    }

    private FortressData parseFortress(Element element, Server server) {

        String fortressName = Optional.ofNullable(element.selectFirst("tr > td"))
                .map(Element::text)
                .orElse("");

        String fortressImageUri = Optional.ofNullable(element.selectFirst("img[src]"))
                .map(imgEl -> FortressParserConfig.pullHost() + imgEl.attr("src"))
                .orElse("");

        Elements rows = Optional.ofNullable(element.select("tbody").last())
                .map(tbody -> tbody.select("tr"))
                .orElse(new Elements());

        String clanUri = parseClanUri(rows.get(0).select("a").attr("onclick"));
        int holdTime = extractFirstInt(rows.get(2).select("td:last-of-type").text());
        long coffer = extractLong(rows.get(3).select("td:last-of-type").text());

        Elements skillImages = rows.get(6).select("img");
        Set<FortressSkill> fortressSkills = parseSkills(skillImages);

        Image image = createUri(fortressImageUri)
                .map(this::parseImage)
                .orElse(null);

        Fortress fortress = Fortress.builder()
                .name(fortressName)
                .server(server)
                .image(image)
                .build();

        FortressHistory history = FortressHistory.builder()
                .holdTime(holdTime)
                .coffer(coffer)
                .build();

        return new FortressData()
                .setFortress(fortress)
                .setFortressHistory(history)
                .setFortressSkills(fortressSkills)
                .setClanUrl(clanUri);
    }

    public Clan parseClan(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        String name = document.select("h3").text().replace("Clan", "").trim();

        Elements images = document.select("img");
        String clanImageUri = null;
        String allyImageUri = null;

        if (images.size() == 1) {
            clanImageUri = FortressParserConfig.pullHost() + images.getFirst().attr("src");
        } else if (images.size() == 2) {
            allyImageUri = FortressParserConfig.pullHost() + images.get(0).attr("src");
            clanImageUri = FortressParserConfig.pullHost() + images.get(1).attr("src");
        }

        Image clanImage = createUri(clanImageUri)
                .map(this::parseImage)
                .orElse(null);

        Elements contentElementsClan = document.select("body > b");

        String fullServer = stringAfter(contentElementsClan.getFirst().text());
        String trimmedFullServer = trimAll(fullServer).toLowerCase();

        Server server = htmlDocument.getServers().stream()
                .filter(saved -> trimAll(saved.getName() + saved.getRates())
                        .toLowerCase()
                        .equals(trimmedFullServer))
                .findFirst()
                .orElse(null);

        return Clan.builder()
                .name(name)
                .server(server)
                .level(parseShortField(contentElementsClan, 1))
                .leader(fieldText(contentElementsClan, 2))
                .playersCount(parseShortField(contentElementsClan, 3))
                .castle(fieldText(contentElementsClan, 4))
                .reputation(parseIntField(contentElementsClan, 5))
                .alliance(fieldText(contentElementsClan, 6))
                .image(clanImage)
                .build();
    }

    public Set<FortressSkill> parseSkills(Elements skills) {
        return skills.stream().map(skill -> {
            String[] skillTitle = skill.attr("title").split(": ");
            String skillName = skillTitle[0];
            String skillEffect = skillTitle[1];
            String imageUri = skill.attr("src");

            Image skillImage = createUri(imageUri)
                    .map(this::parseImage)
                    .orElse(null);

            return FortressSkill.builder()
                    .name(skillName)
                    .effect(skillEffect)
                    .image(skillImage)
                    .fortress(null)
                    .build();
        }).collect(Collectors.toSet());
    }

    public Image parseImage(URI uri) {
        UUID uuid = UUID.randomUUID();
        String externalName = Paths.get(uri.getPath()).getFileName().toString();
        String fileName = uuid + "-" + externalName;
        String systemPath = environment.getProperty("file.upload-dir");
        Path dir = Paths.get(systemPath != null ? systemPath : "uploads/images");
        Path path = dir.resolve(fileName);

        return Image.builder()
                .name(fileName)
                .path(path.toString())
                .uuid(uuid)
                .dir(dir.toString())
                .externalName(externalName)
                .extension(getExtension(fileName))
                .externalUri(uri.toString())
                .build();
    }

    public static String stringAfter(String string) {
        if (string == null) {
            return null;
        }
        int index = string.indexOf(':');
        return string.substring(index + 1).trim();
    }

    private String parseClanUri(String onclick) {
        if (onclick == null || onclick.isBlank()) return "NPC";

        Matcher matcher = Pattern.compile("'([^']+\\.html)'").matcher(onclick);
        if (matcher.find()) {
            return FortressParserConfig.pullHost() + "/" + matcher.group(1);
        }
        return "NPC";
    }

    private int extractFirstInt(String text) {
        Matcher matcher = Pattern.compile("-?\\d+").matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group()) : 0;
    }

    private long extractLong(String text) {
        Matcher matcher = Pattern.compile("-?\\d+").matcher(text);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            builder.append(matcher.group());
        }
        return !builder.isEmpty() ? Long.parseLong(builder.toString()) : 0;
    }

    public static String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }

    public Optional<URI> createUri(String uriString) {
        try {
            if (uriString == null || uriString.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(URI.create(uriString));
        } catch (IllegalArgumentException e) {
            log.warn("Could not parse URI: {}", uriString, e);
            return Optional.empty();
        }
    }

    private String fieldText(Elements elems, int index) {
        return stringAfter(elems.get(index).text());
    }

    private short parseShortField(Elements elems, int index) {
        return Short.parseShort(fieldText(elems, index));
    }

    private int parseIntField(Elements elems, int index) {
        return Integer.parseInt(fieldText(elems, index));
    }
}
