package data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataStorage implements Serializable {
    private static final long serialVersionUID = 6412795632171837903L;

    // PlayerStatus
    int level;
    int maxLife;
    int life;
    int maxMana;
    int mana;
    int strength;
    int dexterity;
    int exp;
    int nextLevelExp;
    int coin;

    // respawn point
    int respawnMap;
    int respawnWorldX;
    int respawnWorldY;
    int respawnArea;

    // inventory
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<Integer> itemAmounts = new ArrayList<>();
    int currentWeaponSlot;
    int currentShieldSlot;

    // object on map
    String mapObjectNames[][];
    int mapObjectWorldX[][];
    int mapObjectWorldY[][];
    String mapObjectLootNames[][];
    boolean mapObjectOpened[][];
}
