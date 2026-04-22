package de.sesu8642.feudaltactics.menu.achievements.model;

import lombok.Getter;

/**
 * Represents a historic person or event that is referenced by an achievement.
 */
public enum HistoricPersonOrEvent {
    CHARLEMAGNE("Charlemagne", "Charlemagne (747-814) was King of the Franks and Lombards, Emperor of the Romans, winning many battles and uniting much of Western Europe during the early Middle Ages."),
    JEANNE_DARC("Jeanne d'Arc", "Jeanne d'Arc (1412-1431) was a French heroine and saint. She led the French army to important victories while she was still a teenager."),
    THIRTY_YEARS_WAR("Thirty Years' War", "The Thirty Years' War was a series of wars in Central Europe between 1618 and 1648."),
    HUNDRED_YEARS_WAR("Hundred Years' War", "The Hundred Year's War was a series of conflicts waged from 1337 to 1453."),
    ROAD_TO_CANOSSA("Road to Canossa", "Holy Roman Emperor Henry IV went to Canossa to seek absolution from Pope Gregory VII. This was a humiliating event for the emperor."),
    WILLIAM_THE_CONQUEROR("William the Conqueror", "William I. (1028-1087) was the first Norman King of England. He won many battles in a row, including the famous Battle of Hastings in 1066."),
    RICHARD_THE_LIONHEART("Richard the Lionheart", "Richard I. (1157-1199) was King of England. He fought many battles, especially in the Third Crusade, far away from his home."),
    FREDERICK_THE_GREAT("Frederick the Great", "Frederick II. (1712-1786) was King of Prussia. While winning many battles, he is also known as a patron for education and the arts."),
    LOUIS_XI("Louis XI", "Louis XI (1423-1483) was King of France and had the nickname 'The Universal Spider', as he carried out intrigues to play out his enemies against each other. He is known for his cunning and deviousness, but also for uniting France after the Hundred Years' War."),
    HENRY_VIII("Henry VIII", "Henry VIII (1491-1547) was King of England. He is known for his six marriages, but also for building much military infrastructure."), // Reserved for BuyNCastlesAchievement
    HRE_HENRY_VI("Holy Roman Emperor Henry VI", "Henry VI (1165-1197) was Holy Roman Emperor. He was a travelling king who had no fixed castle, but still won many battles."), // Reserved for WinWithOnlyNCastlesAchievement
    TOKUGAWA_IEYASU("Tokugawa Ieyasu", "Tokugawa Ieyasu (1543-1616) was born in a Samurai family, not a noble. Although he was not initially in a position of power, he eventually became the Shogun of Japan in 1603 when he was 60 years old."), 
    JOHN_THE_POSTHUMOUS("John the Posthumous", "John the Posthumous (1316-1316) was King of France for only five days, from his birth to his early death."), 
    ;

    private HistoricPersonOrEvent(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Name of the historic person or event. Shouldn't be too long, as it needs to fit in the achievement overview.
     */
    @Getter
    private final String name;

    /**
     * Description of the historic person or event. It is shown in the achievement details. For secret achievements,
     * it hints at what needs to be done, because the exact requirements are not revealed until the achievement is unlocked.
     */
    @Getter
    private final String description;

}