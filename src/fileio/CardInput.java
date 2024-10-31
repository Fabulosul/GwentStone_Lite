package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Table;
import main.Utility;

import java.util.ArrayList;

public final class CardInput {
    private int mana;
    private int attackDamage;
    private int health;
    private boolean isFrozen;
    private boolean hasAttacked;
    private boolean hasUsedAbility;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public CardInput() {
        isFrozen = false;
        hasAttacked = false;
        hasUsedAbility = false;
    }

    public CardInput(CardInput card) {
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


    public void cardUsesAttackFailed(Coordinates cardAttackerCoordinates, Coordinates cardAttackedCoordinates,
                                           String message, ObjectNode objectNode, ObjectMapper mapper) {
        objectNode.put("command", "cardUsesAttack");

        ObjectNode attackerCoordinates = mapper.createObjectNode();
        attackerCoordinates.put("x", cardAttackerCoordinates.getX());
        attackerCoordinates.put("y", cardAttackerCoordinates.getY());
        objectNode.set("cardAttacker", attackerCoordinates);

        ObjectNode attackedCoordinates = mapper.createObjectNode();
        attackedCoordinates.put("x", cardAttackedCoordinates.getX());
        attackedCoordinates.put("y", cardAttackedCoordinates.getY());
        objectNode.set("cardAttacked", attackedCoordinates);
        objectNode.put("error", message);
    }


       public boolean cardUsesAttack(CardInput cardAttacked, Coordinates cardAttackerCoordinates, Coordinates cardAttackedCoordinates,
                               Table table, ObjectNode objectNode,
                               ObjectMapper mapper) {

        int cardAttackerId = Utility.getPlayerId(cardAttackerCoordinates.getX());
        int cardAttackedId = Utility.getPlayerId(cardAttackedCoordinates.getX());

        if(cardAttackerId == cardAttackedId) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card does not belong to the enemy.", objectNode, mapper);
            return false;
        }

        if(hasAttacked) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }

        if(isFrozen) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if(table.checkForTankCards(cardAttackedId)) { // check if there is any tank card
            if(cardAttackedCoordinates.getX() != 1 && cardAttackedCoordinates.getX() != 2) { // the card is not tank
                cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card is not of type 'Tank’.", objectNode, mapper);
                return false;
            }
        }
        this.hasAttacked = true;
        if (cardAttacked.getHealth() > this.getAttackDamage()) { // the card is not destroyed
            cardAttacked.setHealth(cardAttacked.getHealth() - this.getAttackDamage());
        } else {
            ArrayList<CardInput> row = table.getTableCards().get(cardAttackedCoordinates.getX());
            row.remove(cardAttackedCoordinates.getY());
       }
       return true;
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

    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public boolean getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    public boolean isHasUsedAbility() {
        return hasUsedAbility;
    }

    public void setHasUsedAbility(boolean hasUsedAbility) {
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
