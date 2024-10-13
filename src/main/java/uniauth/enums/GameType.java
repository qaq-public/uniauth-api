package uniauth.enums;

import lombok.Getter;

@Getter
public enum GameType {
    ARPG("ARPG"), MMO("MMO"), Moba("Moba"), RPG("RPG"), FPS("FPS"), TPS("TPS"),
    ACT("ACT"), SLG("SLG"), STG("STG"),
    养成("养成"), CAG("卡牌"), LVG("恋爱"), TD("塔防"), 动漫游戏("漫改IP"),
    沙盒("沙盒"), SIM("模拟游戏"), NotGame("非游戏"), 其他("其他");

    private final String value;

    GameType(String value) {
        this.value = value;
    }

}
