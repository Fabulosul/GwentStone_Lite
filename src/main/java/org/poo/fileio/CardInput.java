package org.poo.fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.ArrayList;

public final class CardInput {
    public static final int INITIAL_HERO_HEALTH = 30;
    private int mana;
    private int attackDamage;
    private int health;
    private boolean isFrozen;
    public boolean hasAttacked;
    private boolean hasUsedAbility;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public CardInput() {
        isFrozen = false;
        hasAttacked = false;
        hasUsedAbility = false;
    }

    /**
     * Copy constructor used to create a deep copy of a card.
     * It copies the mana, attack damage, health, isFrozen status, hasAttacked status,
     * hasUsedAbility status, description, colors and name of the card given as parameter.
     *
     * @param card - the card that will be copied
     */
    public CardInput(final CardInput card) {
        this.mana = card.mana;
        this.attackDamage = card.attackDamage;
        this.health = card.health;
        this.isFrozen = card.isFrozen;
        this.hasAttacked = card.hasAttacked;
        this.hasUsedAbility = card.hasUsedAbility;
        this.description = card.description;
        this.colors = new ArrayList<>(card.colors);
        this.name = card.name;
    }


    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public ArrayList<String> getColors() {
        return colors;
    }

    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean getHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(final boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean getHasUsedAbility() {
        return hasUsedAbility;
    }

    public void setHasUsedAbility(final boolean hasUsedAbility) {
        this.hasUsedAbility = hasUsedAbility;
    }

    @Override
    public String toString() {
        return "CardInput{"
                +  "mana="
                + mana
                +  ", attackDamage="
                + attackDamage
                + ", health="
                + health
                +  ", description='"
                + description
                + '\''
                + ", colors="
                + colors
                + ", name='"
                +  ""
                + name
                + '\''
                + '}';
    }
}
