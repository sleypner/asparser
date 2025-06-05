package com.sleypner.parserarticles.parsing;

import com.sleypner.parserarticles.model.services.*;
import com.sleypner.parserarticles.model.source.entityes.*;
import com.sleypner.parserarticles.parsing.raw.FortressParser;
import com.sleypner.parserarticles.special.Special;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class Output {

    private final Logger logger = LoggerFactory.getLogger(Output.class);
    private final String url = "https://asterios.tm/index.php?js=1";
    private final String dirFiles = "src/main/java/com/sleypner/parserarticles/doc/";
    private final OnlineStatusService onlineStatusService;
    private final ArticleService articleService;
    private final FortressService fortressService;
    private final FortressSkillsService fortressSkillsService;
    private final ClanService clanService;
    private final EventsService eventsService;
    private final RaidBossesService raidBossesService;
    private final Parser parser;
    private final FortressHistoryService fortressHistoryService;

    public void saveToFile(String filePath, String content) {
        try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8, false)) {
            fileWriter.write(content);
        } catch (IOException e) {
            logger.atError()
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
        }
    }

    public void saveToBase(List<Article> list) {
        for (Article elem : list) {
            articleService.save(elem);
        }
    }

    public Map<String, Integer> saveArticles(List<Article> parseList) {
        Map<String, Integer> result = new HashMap<>();
        LocalDateTime parseLastDate = parseList.getFirst().getCreateOn();
        List<Article> lastInBaseList = articleService.getByDate(parseLastDate, LocalDateTime.now());
        List<Article> newArticles = new ArrayList<>();
        if (!lastInBaseList.isEmpty()) {
            for (Article elem : parseList) {
                Optional<Article> optionalSavedArticle = lastInBaseList.stream().filter(o -> o.equals(elem)).findFirst();
                if (optionalSavedArticle.isEmpty()) {
                    newArticles.add(articleService.save(elem));
                }
            }
        } else {
            for (Article elem : parseList) {
                newArticles.add(articleService.save(elem));
            }
        }
        result.put("articles", newArticles.size());
        return result;
    }

    public Map<String, Integer> saveOnline(List<OnlineStatus> statusList) {
        Map<String, Integer> result = new HashMap<>();
        List<OnlineStatus> newOnline = new ArrayList<>();

        for (OnlineStatus online : statusList) {
            newOnline.add(onlineStatusService.save(online));
        }
        result.put("online", newOnline.size());
        return result;
    }

    public Map<String, Integer> saveFortress(List<FortressParser> fortressList) {
        Map<String, Integer> result = new HashMap<>();
        List<Fortress> savedFortress = fortressService.getAll();
        List<Clan> savedClans = clanService.getAll();

        int addedFortress = 0, updatedFortress = 0;
        int addedClan = 0, updatedClan = 0;
        if (!savedFortress.isEmpty()) {

            for (FortressParser elem : fortressList) {
                Fortress fort = elem.getFortress();
                FortressHistory fortressHistory = elem.getFortressHistory();
                Clan clan = elem.getClan();

                Optional<Fortress> linkFortress = savedFortress.stream().filter(o -> (o.equals(fort))).findFirst();
                Fortress sf = null;
                if (linkFortress.isPresent()) {
                    fort.setId(linkFortress.get().getId());
                    fort.setSkills(linkFortress.get().getSkills());
                    sf = fortressService.update(fort);
                    updatedFortress++;
                } else {
                    fort.setCreatedDate(LocalDateTime.now().withNano(0));
                    sf = fortressService.save(fort);
                    addedFortress++;
                }
                Optional<Clan> linkClan = savedClans.stream().filter(o -> (o.equals(clan))).findFirst();
                Clan cl = null;
                if (linkClan.isPresent()) {
                    clan.setId(linkClan.get().getId());
                    cl = clanService.update(clan);
                    updatedClan++;
                } else {
                    clan.setCreatedDate(LocalDateTime.now().withNano(0));
                    cl = clanService.save(clan);
                    addedClan++;
                }

                if (sf != null && cl != null) {
                    fortressHistory.setCreatedDate(fortressHistory.getUpdatedDate());
                    fortressHistory.setClanId(cl.getId());
                    fortressHistory.setFortressId(sf.getId());
                    fortressHistoryService.save(fortressHistory);
                }
            }
        } else {
            int k = 0;
            for (FortressParser elem : fortressList) {
                Fortress fort = elem.getFortress();
                FortressHistory fortHistory = elem.getFortressHistory();
                Clan clan = elem.getClan();

                Fortress sf = null;
                fort.setCreatedDate(fort.getUpdatedDate());
                if (k == 0) {
                    sf = fortressService.save(fort);
                    addedFortress++;
                } else {
                    Set<FortressSkills> skills = fort.getSkills();
                    Set<FortressSkills> updatedSkills = new HashSet<>();
                    Set<FortressSkills> toSaveSkills = new HashSet<>();

                    Iterator<FortressSkills> iterator = skills.iterator();
                    while (iterator.hasNext()) {
                        FortressSkills skill = iterator.next();
                        FortressSkills savedSkill = fortressSkillsService.getByName(skill.getName());
                        if (savedSkill != null) {
                            updatedSkills.add(savedSkill);
                        } else {
                            toSaveSkills.add(skill);
                        }
                    }
                    if (!toSaveSkills.isEmpty()) {
                        fort.setSkills(toSaveSkills);
                        sf = fortressService.save(fort);
                        addedFortress++;
                        if (!updatedSkills.isEmpty()) {
                            fort.setSkillAll(updatedSkills);
                            sf = fortressService.update(fort);
                            updatedFortress++;
                        }
                    } else {
                        fort.setSkills(updatedSkills);
                        sf = fortressService.update(fort);
                        if (sf.getUpdatedDate() == sf.getCreatedDate()) {
                            addedFortress++;
                        } else {
                            updatedFortress++;
                        }
                    }
                }
                Clan cl = null;
                if (clan.getName().equals("NPC")) {
                    clan.setCreatedDate(clan.getUpdatedDate());
                    Clan npcClan = clanService.getByNameAndServer(clan.getName(), clan.getServer());
                    cl = (npcClan != null) ? npcClan : clanService.save(clan);
                    addedClan++;
                } else {
                    clan.setCreatedDate(clan.getUpdatedDate());
                    cl = clanService.save(clan);
                    addedClan++;
                }

                if (sf != null && cl != null) {
                    fortHistory.setClanId(cl.getId());
                    fortHistory.setFortressId(sf.getId());
                    fortHistory.setCreatedDate(fortHistory.getUpdatedDate());
                    fortressHistoryService.save(fortHistory);
                }
                k++;
            }
        }
        result.put("fortress_added", addedFortress);
        result.put("fortress_updated", updatedFortress);
        result.put("clan_added", addedClan);
        result.put("clan_updated", updatedClan);
        return result;
    }

    public Map<String, Integer> saveEvents(List<Events> eventList) {
        Map<String, Integer> result = new HashMap<>();
        for (Events event : eventList) {
            eventsService.save(event);
        }
        result.put("added", eventList.size());
        return result;
    }

    public Map<String, Integer> saveRaidBosses(List<Events> eventList) {
        Map<String, Integer> result = new HashMap<>();
        int bossesAdded = 0;
        int bossesUpdated = 0;

        for (Events event : eventList) {
            String bossType = event.getType();
            if (bossType.equalsIgnoreCase("epic bosses") || bossType.equalsIgnoreCase("key bosses")) {
                String[] splitName = event.getTitle().replace("Boss", "").split("was");
                String nameBoss = Special.getMatchedString("Boss ([\\s\\S]*?) was killed", event.getTitle());
                String killer = Special.getMatchedString("dealt by ([\\s\\S]*?) from clan", event.getDescription());
                String killerClan = "";
                if (killer != null) {
                    killerClan = Special.getMatchedString("clan (\\w+)\\.$", event.getDescription());
                } else {
                    killer = Special.getMatchedString("dealt by (\\w+)\\.$", event.getDescription());
                }
                String countAttackersString = Special.getMatchedString("attackers: ([\\s\\S]*?). ", event.getDescription());
                int countAttackers = 0;
                if (countAttackersString != null) {
                    countAttackers = Integer.parseInt(countAttackersString);
                }
                RaidBosses saved = raidBossesService.getByNameAndServer(nameBoss, event.getServer());
                if (saved == null) {
                    raidBossesService.save(RaidBosses.builder()
                            .name(nameBoss)
                            .type(bossType)
                            .server(event.getServer())
                            .date(event.getDate())
                            .lastKiller(killer)
                            .lastKillersClan(killerClan)
                            .attackersCount(countAttackers)
                            .build());
                    bossesAdded++;
                } else {
                    saved.setDate(event.getDate())
                            .setCountKilling(saved.getCountKilling() + 1)
                            .setLastKillersClan(killerClan)
                            .setAttackersCount(countAttackers)
                            .setLastKiller(killer);

                    raidBossesService.save(saved);
                    bossesUpdated++;
                }

            }
        }
        result.put("added", bossesAdded);
        result.put("updated", bossesUpdated);
        return result;
    }

}
