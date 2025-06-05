package com.sleypner.parserarticles.parsing;

import com.sleypner.parserarticles.model.source.entityes.*;
import com.sleypner.parserarticles.parsing.raw.FortressParser;
import com.sleypner.parserarticles.special.DateFormat;
import com.sleypner.parserarticles.special.HttpAction;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ParserImpl implements Parser {
    Logger logger = LoggerFactory.getLogger(ParserImpl.class);
    String host = "https://asterios.tm";
    HttpAction httpAction;

    @Autowired
    public ParserImpl(HttpAction httpAction) {
        this.httpAction = httpAction;
    }

    @Override
    public List<Article> parseArticles(Document jsoupDocument) {
        List<Article> articleList = new ArrayList<>();
        var contentElements = jsoupDocument.getElementsByClass("text");

        contentElements.forEach(s -> {
            String link = s.select("a").first().attr("href");
            String title = s.select("a").first().text();
            String subtitle = s.select("div div p").first().text();
            String description = s.select("div div").first().toString();
            LocalDateTime createOn = null;
            createOn = LocalDateTime.from(DateFormat.format(s.getElementsByClass("col").text()));

            Article elArticle = Article.builder()
                    .link(link)
                    .title(title)
                    .subtitle(subtitle)
                    .description(description)
                    .createOn(createOn)
                    .build();
            articleList.add(elArticle);
        });
        return articleList;
    }

    @Override
    public List<OnlineStatus> parseStatus(Document jsoupDocument) {
        List<OnlineStatus> statusList = new ArrayList<>();

        Elements contentElements = jsoupDocument.select("#OnlineStatus [class*=block]");

        contentElements.forEach(s -> {
            String serverName = s.select("tr").last().select("td").last().text();
            short online = Short.parseShort(s.select("tr").first().selectFirst("b").text());
            short onTrade = Short.parseShort(s.select("tr").next().first().select("td").last().text());

            statusList.add(OnlineStatus.builder()
                    .serverName(serverName)
                    .online(online)
                    .onTrade(onTrade)
                    .build());
        });
        return statusList;
    }

    @Override
    public List<FortressParser> parseFortress(Document jsoupDocument) {
        List<FortressParser> fortressParser = new ArrayList<>();
        var contentElements = jsoupDocument.getElementsByTag("center");
        var server = jsoupDocument.select("#serv > option[selected]").text();
        for (Element el : contentElements) {
            String fortressName = el.select("tr").first().text();
            Elements otherElements = el.select("tbody").last().select("tr");

            String onclick = otherElements.get(0).select("a").attr("onclick");
            String clanUrl = null;
            if (onclick.isEmpty()) {
                clanUrl = "NPC";
            } else {
                String regex = "\'([^']*\\.html)\'";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(onclick);
                if (matcher.find()) {
                    clanUrl = host + "/" + matcher.group().replace("\'", "");
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
            long coffer = 0;
            while (matcher.find()) {
                temp.append(matcher.group());
            }
            coffer = Long.parseLong(temp.toString());
            Elements skills = otherElements.get(6).select("img");
            Set<FortressSkills> skillsListParse = parseSkills(skills);

            FortressHistory fortressHistory = FortressHistory.builder()
                    .coffer(coffer)
                    .holdTime(holdTime)
                    .build();
            Fortress fortress = Fortress.builder()
                    .name(fortressName)
                    .server(server)
                    .build();
            fortress.setSkills(skillsListParse);

            FortressParser fp = new FortressParser(fortressHistory, fortress, skillsListParse);
            fp.setClanUrl(clanUrl);

            fortressParser.add(fp);
        }
        return fortressParser;
    }

    public String stringAfter(String string) {
        char ch = ':';
        int index = string.indexOf(ch);
        return string.substring(index + 1).trim();
    }

    @Override
    public Clan parseClan(Document jsoupDocument) {
        String clanName = jsoupDocument.select("h3").text().replace("Clan", "").trim();
        Elements images = jsoupDocument.select("img");
        byte[] clanByte = null;
        if (images.size() == 1) {
            String clanLink = images.get(0).attr("src");
            clanByte = httpAction.getImage(host + clanLink);
        } else if (images.size() == 2) {
            String clanLink = images.get(1).attr("src");
            clanByte = httpAction.getImage(host + clanLink);
        }
        var contentElementsClan = jsoupDocument.select("body > b");

        String serverClan = stringAfter(contentElementsClan.get(0).text());
        short clanLevel = Short.parseShort(stringAfter(contentElementsClan.get(1).text()));
        String clanLeader = stringAfter(contentElementsClan.get(2).text());
        short playersCount = Short.parseShort(stringAfter(contentElementsClan.get(3).text()));
        String castle = stringAfter(contentElementsClan.get(4).text());
        int reputation = Integer.parseInt(stringAfter(contentElementsClan.get(5).text()));
        String alliance = stringAfter(contentElementsClan.get(6).text());

        return Clan.builder()
                .name(clanName)
                .image(clanByte)
                .server(serverClan)
                .level(clanLevel)
                .leader(clanLeader)
                .playersCount(playersCount)
                .castle(castle)
                .reputation(reputation)
                .alliance(alliance)
                .build();
    }

    @Override
    public Set<FortressSkills> parseSkills(Elements skills) {
        Set<FortressSkills> skillsList = new HashSet<>();
        skills.forEach(skill -> {
            String[] skillTitle = skill.attr("title").split(": ");
            String skillName = skillTitle[0];
            String skillEffect = skillTitle[1];
            String skillImgUrl = skill.attr("src");
            byte[] skillByte = httpAction.getImage(skillImgUrl);
            skillsList.add(FortressSkills.builder()
                    .name(skillName)
                    .effect(skillEffect)
                    .image(skillByte)
                    .fortress(null)
                    .build());
        });
        return skillsList;
    }

    public List<Events> parseEvents(Document jsoupDocument, LocalDateTime lastDate) {

        List<Events> eventsList = new ArrayList<>();

        String server = jsoupDocument.select("select#serv option[selected]").text();
        String type = jsoupDocument.select("select#filter option[selected]").text();
        Elements contentElements = jsoupDocument.select("center table");
        for (Element element : contentElements) {

            String fullTitle = element.select("tr").first().text();
            String description = element.select("tr").last().text();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss");
            String[] splitFullTitle = fullTitle.split(": ");
            LocalDateTime date = LocalDateTime.parse(splitFullTitle[0], formatter);
            String title = splitFullTitle[1];

            if (lastDate == null || lastDate.isBefore(date)) {

                Events event = Events.builder()
                        .title(title)
                        .description(description)
                        .date(date)
                        .server(server)
                        .type(type)
                        .build();

                eventsList.add(event);
            } else {
                break;
            }
        }
        return eventsList;
    }
}
