package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Player;
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
        cardUseAction(cardAttackerCoordinates, cardAttackedCoordinates, message, objectNode, mapper);
    }


       public boolean cardUsesAttack(CardInput cardAttacked, Coordinates cardAttackerCoordinates,
                                     Coordinates cardAttackedCoordinates,
                               Table table, ObjectNode objectNode,
                               ObjectMapper mapper) {

        int cardAttackerId = Utility.getPlayerId(cardAttackerCoordinates.getX());
        int cardAttackedId = Utility.getPlayerId(cardAttackedCoordinates.getX());

        if(cardAttackerId == cardAttackedId) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card does not belong to the enemy.", objectNode, mapper);
            return false;
        }

        if(hasUsedAbility || hasAttacked) {
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
                        "Attacked card is not of type 'Tank'.", objectNode, mapper);
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


    private void cardUseAction(Coordinates cardAttackerCoordinates, Coordinates cardAttackedCoordinates, String message, ObjectNode objectNode, ObjectMapper mapper) {
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

    public void cardUsesAbilityFailed(Coordinates cardAttackerCoordinates, Coordinates cardAttackedCoordinates,
                                      String message, ObjectNode objectNode, ObjectMapper mapper) {
        objectNode.put("command", "cardUsesAbility");
        cardUseAction(cardAttackerCoordinates, cardAttackedCoordinates, message, objectNode, mapper);
    }

    public void cardUsesAbilitySuccess(CardInput cardAttacked,
                                       Coordinates cardAttackedCoordinates,
                                       Table table) {
        hasUsedAbility = true;
        if(getName().equals("The Ripper")) {
            if(cardAttacked.getAttackDamage() < 2) {
                cardAttacked.setAttackDamage(0);
            } else {
                cardAttacked.setAttackDamage(cardAttacked.getAttackDamage() - 2);
            }
        }
        if(getName().equals("Miraj")) {
            int cardAttackerHealth = getHealth();
            setHealth(cardAttacked.getHealth());
            cardAttacked.setHealth(cardAttackerHealth);
        }
        if(getName().equals("The Cursed One")) {
            if(cardAttacked.getAttackDamage() == 0) {
                table.getTableCards().get(cardAttackedCoordinates.getX()).remove(cardAttackedCoordinates.getY());
            } else {
                int cardAttackedHealth = cardAttacked.getHealth();
                cardAttacked.setHealth(cardAttacked.getAttackDamage());
                cardAttacked.setAttackDamage(cardAttackedHealth);
            }
        }
        if(getName().equals("Disciple")) {
                int cardHealth = cardAttacked.getHealth();
                cardAttacked.setHealth(cardHealth + 2);
        }
    }

    public boolean cardUsesAbility(CardInput cardAttacked, Coordinates cardAttackerCoordinates,
                                   Coordinates cardAttackedCoordinates,
                                   Table table, ObjectNode objectNode,
                                   ObjectMapper mapper) {

        int cardAttackerId = Utility.getPlayerId(cardAttackerCoordinates.getX());
        int cardAttackedId = Utility.getPlayerId(cardAttackedCoordinates.getX());

        if(isFrozen) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if(hasUsedAbility || hasAttacked) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }

        if(getName().equals("Disciple") && cardAttackerId != cardAttackedId) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card does not belong to the current player.", objectNode, mapper);
            return false;
        }

        if((getName().equals("The Ripper") || getName().equals("Miraj") || getName().equals("The Cursed One"))) {
            if(cardAttackerId == cardAttackedId) {
                cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card does not belong to the enemy.", objectNode, mapper);
                return false;
            }
            if(table.checkForTankCards(cardAttackedId) && !cardAttacked.getName().equals("Goliath") &&
                !cardAttacked.getName().equals("Warden")) {
                cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card is not of type 'Tank'.", objectNode, mapper);
                return false;
            }
        }
        cardUsesAbilitySuccess(cardAttacked, cardAttackedCoordinates, table);

        return true;
    }

    public void useAttackHeroFailed(Coordinates cardAttackerCoordinates, String message,
                                    ObjectNode objectNode, ObjectMapper mapper) {
        objectNode.put("command", "useAttackHero");
        ObjectNode attackerCoordinates = mapper.createObjectNode();
        attackerCoordinates.put("x", cardAttackerCoordinates.getX());
        attackerCoordinates.put("y", cardAttackerCoordinates.getY());
        objectNode.set("cardAttacker", attackerCoordinates);
        objectNode.put("error", message);
    }

    public void useAttackHeroSuccess(CardInput heroAttacked, int cardAttackerId, ObjectNode objectNode) {
        hasAttacked = true;
        if(getAttackDamage() >= heroAttacked.getHealth()) {
            heroAttacked.setHealth(0);
            if(cardAttackerId == 1) {
                objectNode.put("gameEnded", "Player one killed the enemy hero.");
            } else {
                objectNode.put("gameEnded", "Player two killed the enemy hero.");
            }
        } else {
            heroAttacked.setHealth(heroAttacked.getHealth() - getAttackDamage());
        }
    }

    public boolean useAttackHero(Coordinates cardAttackerCoordinates, CardInput heroAttacked,
                                   Table table, ObjectNode objectNode,
                                   ObjectMapper mapper) {
        int cardAttackerId = Utility.getPlayerId(cardAttackerCoordinates.getX());
        int cardAttackedId = cardAttackerId == 1 ? 2 : 1;

        if(isFrozen) {
            useAttackHeroFailed(cardAttackerCoordinates, "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if(hasAttacked || hasUsedAbility) {
            useAttackHeroFailed(cardAttackerCoordinates, "Attacker card has already attacked this turn.",
                    objectNode, mapper);
            return false;
        }

        if(table.checkForTankCards(cardAttackedId) && !getName().equals("Goliath") &&
                !getName().equals("Warden")) {
            useAttackHeroFailed(cardAttackerCoordinates, "Attacked card is not of type 'Tank'.",
                    objectNode, mapper);
            return false;
        }

        useAttackHeroSuccess(heroAttacked, cardAttackerId, objectNode);
        return true;
    }

    public boolean isOpponentRow(int affectedRow, int currentPlayerTurn) {
        if(currentPlayerTurn == 1 && affectedRow != 0 && affectedRow != 1) {
            return false;
        }
        if(currentPlayerTurn == 2 && affectedRow != 2 && affectedRow != 3) {
            return false;
        }
        return true;
    }

    public void useHeroAbilityFailed(int affectedRow, String message,
                                    ObjectNode objectNode) {
        objectNode.put("command", "useHeroAbility");
        objectNode.put("affectedRow", affectedRow);
        objectNode.put("error", message);
    }

    public void useHeroAbilitySuccess(int affectedRow, int currentPlayerTurn,
                                      Player player,
                                      Table table) {
        player.setMana(player.getMana() - getMana());
        if(getName().equals("Lord Royce")) {
            for(int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                CardInput currentCard = table.getTableCards().get(affectedRow).get(i);
                currentCard.setIsFrozen(true);
            }
        }
        if(getName().equals(("Empress Thorina"))) {
            int highestHealth = -1;
            int highestHealthIdx = -1;
            System.out.println("size= " + table.getTableCards().get(affectedRow).size());
            for(int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                int currentCardHealth = table.getTableCards().get(affectedRow).get(i).getHealth();
                if(currentCardHealth > highestHealth) {
                    highestHealth = currentCardHealth;
                    highestHealthIdx = i;
                    System.out.println(highestHealthIdx);
                }
            }
            System.out.println(highestHealthIdx + "\n");
            table.getTableCards().get(affectedRow).remove(highestHealthIdx);
        }
        if(getName().equals("King Mudface")) {
            for(int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                CardInput currentCard = table.getTableCards().get(affectedRow).get(i);
                currentCard.setHealth(currentCard.getHealth() + 1);
            }
        }
        if(getName().equals("General Kocioraw")) {
            for(int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                CardInput currentCard = table.getTableCards().get(affectedRow).get(i);
                currentCard.setAttackDamage(currentCard.getAttackDamage() + 1);
            }
        }

    }

    public boolean useHeroAbility(int affectedRow, int currentPlayerTurn,
                                  Player player,
                                  Table table, ObjectNode objectNode,
                                  ObjectMapper mapper) {

        if(player.getMana() < getMana()) {
            useHeroAbilityFailed(affectedRow, "Not enough mana to use hero's ability.",
                    objectNode);
            return false;
        }

        if(hasAttacked) {
            useHeroAbilityFailed(affectedRow, "Hero has already attacked this turn.",
                    objectNode);
            return false;
        }

        if((getName().equals("Lord Royce") || getName().equals("Empress Thorina")) &&
            !isOpponentRow(affectedRow, currentPlayerTurn)) {
            useHeroAbilityFailed(affectedRow, "Selected row does not belong to the enemy.",
                    objectNode);
            return false;
        }

        if((getName().equals("General Kocioraw") || getName().equals("King Mudface")) &&
                isOpponentRow(affectedRow, currentPlayerTurn)) {
            useHeroAbilityFailed(affectedRow, "Selected row does not belong to the current player.",
                    objectNode);
            return false;
        }

        useHeroAbilitySuccess(affectedRow, currentPlayerTurn, player, table);
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
