package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.Clan;
import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.domain.model.FortressHistory;
import dev.sleypner.asparser.domain.model.FortressSkill;
import dev.sleypner.asparser.http.HttpAction;
import dev.sleypner.asparser.service.parser.shared.Parser;
import dev.sleypner.asparser.util.HtmlDocument;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FortressParserService implements Parser<FortressData> {

    static HttpAction httpAction;

    @Override
    public Set<FortressData> parse(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();

        FortressData fortressData = new FortressData();

        Elements elements = document.select("center");
        String server = document.select("#serv > option[selected]").text();
        return elements.stream().map(element -> {
            String fortressName = element.select("tr").first().text();
            Elements otherElements = element.select("tbody").last().select("tr");

            String onclick = otherElements.getFirst().select("a").attr("onclick");
            String clanUrl = null;
            if (onclick.isEmpty()) {
                clanUrl = "NPC";
            } else {
                String regex = "'([^']*\\.html)'";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(onclick);
                if (matcher.find()) {
                    clanUrl = FortressParserConfig.pullHost() + "/" + matcher.group().replace("'", "");
                }
            }

            Pattern pattern = Pattern.compile("-?\\d+");
            Matcher matcher = pattern.matcher(otherElements.get(2).select("td").last().text());
            int holdTime = 0;
            if (matcher.find()) {
                holdTime = Integer.parseInt(matcher.group());
            }

            pattern = Pattern.compile("-?\\d+");
            matcher = pattern.matcher(otherElements.get(3).select("td").last().text());
            StringBuilder temp = new StringBuilder();
            long coffer;
            while (matcher.find()) {
                temp.append(matcher.group());
            }
            coffer = Long.parseLong(temp.toString());
            Elements skills = otherElements.get(6).select("img");
            Set<FortressSkill> fortressSkills = parseSkills(skills);

            FortressHistory fortressHistory = FortressHistory.builder()
                    .coffer(coffer)
                    .holdTime(holdTime)
                    .build();
            Fortress fortress = Fortress.builder()
                    .name(fortressName)
                    .server(server)
                    .build();
            fortress.setSkills(fortressSkills);

            return new FortressData().setFortress(fortress)
                    .setFortressHistory(fortressHistory)
                    .setFortressSkills(fortressSkills)
                    .setClanUrl(clanUrl);
        }).collect(Collectors.toSet());
    }

    public static Clan parseClan(HtmlDocument htmlDocument) {
        Document document = htmlDocument.getDocument();
        String clanName = document.select("h3").text().replace("Clan", "").trim();
        Elements images = document.select("img");
        byte[] clanByte = null;
        if (images.size() == 1) {
            String clanLink = images.get(0).attr("src");
//            clanByte = httpAction.getImage(pullHost() + clanLink);
        } else if (images.size() == 2) {
            String clanLink = images.get(1).attr("src");
//            clanByte = httpAction.getImage(pullHost() + clanLink);
        }
        var contentElementsClan = document.select("body > b");

        String serverClan = stringAfter(contentElementsClan.get(0).text());
        short clanLevel = Short.parseShort(stringAfter(contentElementsClan.get(1).text()));
        String clanLeader = stringAfter(contentElementsClan.get(2).text());
        short playersCount = Short.parseShort(stringAfter(contentElementsClan.get(3).text()));
        String castle = stringAfter(contentElementsClan.get(4).text());
        int reputation = Integer.parseInt(stringAfter(contentElementsClan.get(5).text()));
        String alliance = stringAfter(contentElementsClan.get(6).text());

        return Clan.builder()
                .name(clanName)
                .image(null)
                .server(serverClan)
                .level(clanLevel)
                .leader(clanLeader)
                .playersCount(playersCount)
                .castle(castle)
                .reputation(reputation)
                .alliance(alliance)
                .build();
    }

    public Set<FortressSkill> parseSkills(Elements skills) {
        return skills.stream().map(skill -> {
            String[] skillTitle = skill.attr("title").split(": ");
            String skillName = skillTitle[0];
            String skillEffect = skillTitle[1];
            String skillImgUrl = skill.attr("src");
//            byte[] skillByte = httpAction.getImage(skillImgUrl);
            return FortressSkill.builder()
                    .name(skillName)
                    .effect(skillEffect)
                    .image(null)
                    .fortress(null)
                    .build();
        }).collect(Collectors.toSet());
    }

    public static String stringAfter(String string) {
        char ch = ':';
        int index = string.indexOf(ch);
        return string.substring(index + 1).trim();
    }
}
