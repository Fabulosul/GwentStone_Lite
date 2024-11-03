package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Player;
import main.Table;

import java.util.ArrayList;

public final class CardInput {
    public static final int INITIAL_HERO_HEALTH = 30;
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

    /**
     * Method that handles the case when a card tries to attack or use an ability
     * on another card and fails.
     * It adds the command, the attacker and attacked card coordinates and the error message
     * to the objectNode to be sent to the output.
     *
     * @param cardAttackerCoordinates - the coordinates of the card that tries to
     *                                attack or use ability
     * @param cardAttackedCoordinates - the coordinates of the card that is affected
     * @param message - the message that will be displayed(in the error section)
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     */
    private void cardUseAction(final Coordinates cardAttackerCoordinates,
                               final Coordinates cardAttackedCoordinates,
                               final String message,
                               final ObjectNode objectNode,
                               final ObjectMapper mapper) {
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

    /**
     * Method that handles the case when a card tries to attack another card and fails.
     * It adds the command, the attacker and attacked card coordinates and the error message
     * to the objectNode using the cardUseAction method.
     *
     * @param cardAttackerCoordinates - the coordinates of the card that tries to attack
     * @param cardAttackedCoordinates - the coordinates of the card that is attacked
     * @param message - the message that will be displayed(in the error section)
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     *
     * @see #cardUseAction method that adds the information to the objectNode in a more
     * general manner
     */
    public void cardUsesAttackFailed(final Coordinates cardAttackerCoordinates,
                                     final Coordinates cardAttackedCoordinates,
                                     final String message,
                                     final ObjectNode objectNode,
                                     final ObjectMapper mapper) {
        objectNode.put("command", "cardUsesAttack");
        cardUseAction(cardAttackerCoordinates, cardAttackedCoordinates,
                message, objectNode, mapper);
    }

    /**
     * Method that handles the case when a card tries to attack another card.
     * It checks if the attack is possible and if it is, it decreases the health of the
     * attacked card with the same amount of points as the attack damage of the attacker card.
     * After this reduction, if the card has a health <= 0, the card is removed from the table.
     * On the other hand, if the attack fails, it uses the cardUsesAttackFailed method to
     * add the necessary information to the objectNode for the output.
     *
     * @param cardAttacked - the card that is attacked
     * @param cardAttackerCoordinates - the coordinates of the card that attacks
     * @param cardAttackedCoordinates - the coordinates of the card that is attacked
     * @param table - the table(game board) where the cards are placed in two rows for each player
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     * @return true if the attack on the card was successful, false if the attack failed
     * (the card that is attacked doesn't belong to the enemy, the card that attacks has already
     * attacked this turn, the card that attacks is frozen or there are tank cards on the
     * enemy side)
     */
    public boolean cardUsesAttack(final CardInput cardAttacked,
                                     final Coordinates cardAttackerCoordinates,
                                     final Coordinates cardAttackedCoordinates,
                                     final Table table, final ObjectNode objectNode,
                                     final ObjectMapper mapper) {

        int cardAttackerId = Player.getPlayerByRow(cardAttackerCoordinates.getX());
        int cardAttackedId = Player.getPlayerByRow(cardAttackedCoordinates.getX());

        if (cardAttackerId == cardAttackedId) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card does not belong to the enemy.", objectNode, mapper);
            return false;
        }

        if (getHasUsedAbility() || getHasAttacked()) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }

        if (isFrozen()) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if (table.hasTankCards(cardAttackedId)) {
            if (cardAttackedCoordinates.getX() != Table.PLAYER_TWO_FRONT_ROW
                    && cardAttackedCoordinates.getX() != Table.PLAYER_ONE_FRONT_ROW) {
                cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card is not of type 'Tank'.", objectNode, mapper);
                return false;
            }
        }
        setHasAttacked(true);
        if (cardAttacked.getHealth() > this.getAttackDamage()) {
            cardAttacked.setHealth(cardAttacked.getHealth() - this.getAttackDamage());
        } else {
            ArrayList<CardInput> row = table.getTableCards().get(cardAttackedCoordinates.getX());
            row.remove(cardAttackedCoordinates.getY());
       }
       return true;
    }

    /**
     * Method that handles the case when a card tries to use an ability on another card and fails.
     * It adds the command, the attacker and attacked card coordinates and the error message
     * to the objectNode to be sent to the output using the cardUseAction method (which was
     * designed to avoid code duplication).
     *
     * @param cardAttackerCoordinates - the coordinates of the card that tries to use an ability
     * @param cardAttackedCoordinates - the coordinates of the card that is affected
     * @param message - the message that will be displayed(in the error section)
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     *
     * @see #cardUseAction method used to add the information to the objectNode and reduce code
     * duplication
     */
    public void cardUsesAbilityFailed(final Coordinates cardAttackerCoordinates,
                                      final Coordinates cardAttackedCoordinates,
                                      final String message, final ObjectNode objectNode,
                                      final ObjectMapper mapper) {
        objectNode.put("command", "cardUsesAbility");
        cardUseAction(cardAttackerCoordinates, cardAttackedCoordinates,
                message, objectNode, mapper);
    }


    /**
     * Method that handles the case when a card tries to use an ability on another card
     * and succeeds.
     * It sets the hasUsedAbility field to true to indicate that that specific card has used its
     * ability this turn.
     * It also checks the name of card attacker to apply the correct feature.
     * The Ripper - reduces the attack damage of the attacked card by 2 points(the attack damage
     * can't be negative so it truncates to 0 if the attack damage is less than 2)
     * Miraj - swaps the health of the attacker card with the health of the attacked card
     * The Cursed One - swaps the health of the attacked card with its attack damage(if the attack
     * damage is 0, the card is removed from the table)
     * Disciple - increases the health of the attacked card by 2 points
     *
     * @param cardAttacked - the card that is attacked
     * @param cardAttackedCoordinates - the coordinates of the card that is attacked
     * @param table - the table(game board) where the cards are placed in two rows for each player
     */
    public void cardUsesAbilitySucceeded(final CardInput cardAttacked,
                                       final Coordinates cardAttackedCoordinates,
                                       final Table table) {
        setHasUsedAbility(true);
        if (getName().equals("The Ripper")) {
            if (cardAttacked.getAttackDamage() < 2) {
                cardAttacked.setAttackDamage(0);
            } else {
                cardAttacked.setAttackDamage(cardAttacked.getAttackDamage() - 2);
            }
        }
        if (getName().equals("Miraj")) {
            int cardAttackerHealth = getHealth();
            setHealth(cardAttacked.getHealth());
            cardAttacked.setHealth(cardAttackerHealth);
        }
        if (getName().equals("The Cursed One")) {
            if (cardAttacked.getAttackDamage() == 0) {
                ArrayList<CardInput> cardAttackedRow
                        = table.getTableCards().get(cardAttackedCoordinates.getX());
                cardAttackedRow.remove(cardAttackedCoordinates.getY());
            } else {
                int cardAttackedHealth = cardAttacked.getHealth();
                cardAttacked.setHealth(cardAttacked.getAttackDamage());
                cardAttacked.setAttackDamage(cardAttackedHealth);
            }
        }
        if (getName().equals("Disciple")) {
                int cardHealth = cardAttacked.getHealth();
                cardAttacked.setHealth(cardHealth + 2);
        }
    }

    /**
     * Method that handles the case when a card tries to use an ability on another card.
     * It checks if the card can use its ability and if it's possible the cardUsesAbilitySucceeded
     * method is called to handle this case.
     * On the other hand, if the card can't use its feature, the cardUsesAbilityFailed is used
     * to add information to the objectNode for the output(possible scenarios are when the
     * card that attacker is frozen, has already attacked/used ability this turn, the enemy has
     * tank cards, the attacker is a Disciple card and the attacked card doesn't belong to
     * the current player, the attacker is a Ripper/Miraj/The Cursed One and the attacked card
     * doesn't belong to the enemy player).
     *
     * @param cardAttacked - the card that is affected
     * @param cardAttackerCoordinates - the coordinates of the card that uses the ability
     * @param cardAttackedCoordinates - the coordinates of the card that is affected
     * @param table - the game board where the cards that can be played are stored
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     * @return true if the card can use its ability, false if the card can't use its ability
     */
    public boolean cardUsesAbility(final CardInput cardAttacked,
                                   final Coordinates cardAttackerCoordinates,
                                   final Coordinates cardAttackedCoordinates,
                                   final Table table, final ObjectNode objectNode,
                                   final ObjectMapper mapper) {

        int cardAttackerId = Player.getPlayerByRow(cardAttackerCoordinates.getX());
        int cardAttackedId = Player.getPlayerByRow(cardAttackedCoordinates.getX());

        if (isFrozen()) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if (getHasUsedAbility() || getHasAttacked()) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }

        if (getName().equals("Disciple") && cardAttackerId != cardAttackedId) {
            cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card does not belong to the current player.", objectNode, mapper);
            return false;
        }

        if ((getName().equals("The Ripper") || getName().equals("Miraj")
                || getName().equals("The Cursed One"))) {
            if (cardAttackerId == cardAttackedId) {
                cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card does not belong to the enemy.", objectNode, mapper);
                return false;
            }
            if (table.hasTankCards(cardAttackedId)
                    && !cardAttacked.getName().equals("Goliath")
                    && !cardAttacked.getName().equals("Warden")) {
                cardUsesAbilityFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                        "Attacked card is not of type 'Tank'.", objectNode, mapper);
                return false;
            }
        }
        cardUsesAbilitySucceeded(cardAttacked, cardAttackedCoordinates, table);

        return true;
    }

    /**
     * Method used when a card tries to attack the enemy hero and fails.
     * It adds the command, the attacker card coordinates and the error message given as
     * parameter to the objectNode.
     *
     * @param cardAttackerCoordinates - the coordinates of the card that tries to attack the
     *                                enemy hero
     * @param message - the message that will be displayed(in the error section)
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     */
    public void useAttackHeroFailed(final Coordinates cardAttackerCoordinates,
                                    final String message,
                                    final ObjectNode objectNode,
                                    final ObjectMapper mapper) {
        objectNode.put("command", "useAttackHero");
        ObjectNode attackerCoordinates = mapper.createObjectNode();
        attackerCoordinates.put("x", cardAttackerCoordinates.getX());
        attackerCoordinates.put("y", cardAttackerCoordinates.getY());
        objectNode.set("cardAttacker", attackerCoordinates);
        objectNode.put("error", message);
    }

    /**
     * Method that handles the case when a card tries to attack the enemy hero and succeeds.
     * It sets the hasAttacked field to true, then checks if the health of the hero is below
     * the attack damage of the attacker card. If it is, the health of the hero is set to 0 and
     * the game ends with a message stating that the attacker won that particular match. In the
     * other case, the hero remain alive, its health is decreased by the attack damage of the
     * attacker card and the game continues
     *
     * @param heroAttacked - the hero that is attacked
     * @param cardAttackerId - the id of the player that attacks the hero(player one or player two)
     * @param objectNode - the object node that will be sent to the output in the case the hero
     *                   dies after the action
     */
    public void useAttackHeroSucceeded(final CardInput heroAttacked, final int cardAttackerId,
                                     final ObjectNode objectNode) {
        setHasAttacked(true);
        if (getAttackDamage() >= heroAttacked.getHealth()) {
            heroAttacked.setHealth(0);
            if (cardAttackerId == Player.PLAYER_ONE_ID) {
                objectNode.put("gameEnded", "Player one killed the enemy hero.");
            } else {
                objectNode.put("gameEnded", "Player two killed the enemy hero.");
            }
        } else {
            heroAttacked.setHealth(heroAttacked.getHealth() - getAttackDamage());
        }
    }

    /**
     * Method used to handle the case when a card tries to attack the enemy hero.
     * It starts by checking if the attack is valid and if it is, it calls the
     * useAttackHeroSucceeded method, otherwise it uses the useAttackHeroFailed method to
     * add the necessary information to the objectNode for the output.
     *
     * @param cardAttackerCoordinates - the coordinates of the card that tries to attack the hero
     * @param heroAttacked - the hero that is attacked
     * @param table - the game board where the cards that can be played are stored
     * @param objectNode - the object node that will be sent to the output
     * @param mapper - the object mapper used to create the objectNode
     * @return true if the attack on the hero was successful, false otherwise
     *
     * @see #useAttackHeroFailed method used when the attack on the hero fails
     * @see #useAttackHeroSucceeded method used when the attack on the hero succeeds
     */
    public boolean useAttackHero(final Coordinates cardAttackerCoordinates,
                                 final CardInput heroAttacked,
                                 final Table table, final ObjectNode objectNode,
                                 final ObjectMapper mapper) {
        int cardAttackerId = Player.getPlayerByRow(cardAttackerCoordinates.getX());
        int cardAttackedId = cardAttackerId
                == Player.PLAYER_ONE_ID ? Player.PLAYER_TWO_ID : Player.PLAYER_ONE_ID;

        if (isFrozen()) {
            useAttackHeroFailed(cardAttackerCoordinates, "Attacker card is frozen.",
                    objectNode, mapper);
            return false;
        }

        if (getHasAttacked() || getHasUsedAbility()) {
            useAttackHeroFailed(cardAttackerCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }

        if (table.hasTankCards(cardAttackedId)) {
            useAttackHeroFailed(cardAttackerCoordinates, "Attacked card is not of type 'Tank'.",
                    objectNode, mapper);
            return false;
        }

        useAttackHeroSucceeded(heroAttacked, cardAttackerId, objectNode);
        return true;
    }

    /**
     * Method that checks if the row given as parameter belongs to the player whose
     * turn isn't in progress.
     *
     * @param affectedRow - the row that is checked
     * @param currentPlayerTurn - the id of the player with the turn in progress
     * @return true if the row belongs to the opponent, false if the row belongs to the
     * current player
     */
    public boolean isOpponentRow(final int affectedRow, final int currentPlayerTurn) {
        if (currentPlayerTurn == Player.PLAYER_ONE_ID && affectedRow != Table.PLAYER_TWO_BACK_ROW
                && affectedRow != Table.PLAYER_TWO_FRONT_ROW) {
            return false;
        }
        return currentPlayerTurn != Player.PLAYER_TWO_ID
                || affectedRow == Table.PLAYER_ONE_FRONT_ROW
                || affectedRow == Table.PLAYER_ONE_BACK_ROW;
    }

    /**
     * Method that handles the case when a player tries to use the ability of his hero and fails.
     * It adds the command, the affected row and the error message given as parameter to the
     * objectNode to be sent to the output.
     *
     * @param affectedRow - the row that is affected by the hero's ability
     * @param message - the message displayed in the error section in the output
     * @param objectNode - the object node that will be sent to the output
     */
    public void useHeroAbilityFailed(final int affectedRow, final String message,
                                    final ObjectNode objectNode) {
        objectNode.put("command", "useHeroAbility");
        objectNode.put("affectedRow", affectedRow);
        objectNode.put("error", message);
    }

    /**
     * Method that handles the case when a player tries to use the ability of his hero
     * and succeeds.
     * It sets the hasAttacked field to true and reduces the mana of the current player.
     * Then, based on the hero, on of the following actions were performed:
     * Lord Royce - freezes all the cards placed in the affected row
     * Empress Thorina - removes the card with the highest health from the affected row
     * King Mudface - increases the health of all the cards in the affected row by 1 point
     * General Kocioraw - increases the attack damage of all the cards in the affected row
     * by 1 point
     *
     * @param affectedRow - the row that is affected by the hero's ability
     * @param player - the player that uses the hero's ability
     * @param table - the game board where the cards that can be played are stored
     */
    public void useHeroAbilitySucceeded(final int affectedRow,
                                      final Player player,
                                      final Table table) {
        setHasAttacked(true);
        player.setMana(player.getMana() - getMana());
        if (getName().equals("Lord Royce")) {
            for (int j = 0; j < table.getTableCards().get(affectedRow).size(); j++) {
                CardInput currentCard = table.getTableCards().get(affectedRow).get(j);
                currentCard.setIsFrozen(true);
            }
        }
        if (getName().equals(("Empress Thorina"))) {
            int highestHealth = -1;
            int highestHealthIdx = -1;
            for (int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                int currentCardHealth = table.getTableCards().get(affectedRow).get(i).getHealth();
                if (currentCardHealth > highestHealth) {
                    highestHealth = currentCardHealth;
                    highestHealthIdx = i;
                }
            }
            table.getTableCards().get(affectedRow).remove(highestHealthIdx);
        }
        if (getName().equals("King Mudface")) {
            for (int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                CardInput currentCard = table.getTableCards().get(affectedRow).get(i);
                currentCard.setHealth(currentCard.getHealth() + 1);
            }
        }
        if (getName().equals("General Kocioraw")) {
            for (int i = 0; i < table.getTableCards().get(affectedRow).size(); i++) {
                CardInput currentCard = table.getTableCards().get(affectedRow).get(i);
                currentCard.setAttackDamage(currentCard.getAttackDamage() + 1);
            }
        }

    }

    /**
     * Method that handles the case when a player tries to use the ability of his hero.
     * Firstly, it checks if the player and his hero meet the requirements to use the ability.
     * If it's possible to use the ability, the useHeroAbilitySucceeded method is called to
     * handle the case when the ability is used successfully.
     * Otherwise, the useHeroAbilityFailed method is used to add the necessary information to
     * the objectNode for the output in case the requirements are not met(the player doesn't have
     * enough mana to use the ability, the hero has already attacked this turn, the hero is
     * Lord Royce/Empress Thorina and the affected row doesn't belong to the enemy player or
     * the hero is General Kocioraw/King Mudface and the affected row doesn't belong to the
     * current player).
     *
     * @param affectedRow - the row that is affected by the hero's ability
     * @param currentPlayerTurn - the id of the player with the turn in progress
     * @param player - the player that uses the hero's ability
     * @param table - the game board where the cards that can be played are stored
     * @param objectNode - the object node that will be sent to the output
     * @return true if the hero's ability was used successfully, false otherwise
     *
     * @see #useHeroAbilityFailed method used when the player can't use the hero's ability
     * @see #useHeroAbilitySucceeded method used when the player used the hero's
     * ability successfully
     */
    public boolean useHeroAbility(final int affectedRow, final int currentPlayerTurn,
                                  final Player player, final Table table,
                                  final ObjectNode objectNode) {

        if (player.getMana() < getMana()) {
            useHeroAbilityFailed(affectedRow, "Not enough mana to use hero's ability.",
                    objectNode);
            return false;
        }

        if (getHasAttacked()) {
            useHeroAbilityFailed(affectedRow, "Hero has already attacked this turn.",
                    objectNode);
            return false;
        }

        if ((getName().equals("Lord Royce") || getName().equals("Empress Thorina"))
                && !isOpponentRow(affectedRow, currentPlayerTurn)) {
            useHeroAbilityFailed(affectedRow, "Selected row does not belong to the enemy.",
                    objectNode);
            return false;
        }

        if ((getName().equals("General Kocioraw") || getName().equals("King Mudface"))
                && isOpponentRow(affectedRow, currentPlayerTurn)) {
            useHeroAbilityFailed(affectedRow,
                    "Selected row does not belong to the current player.", objectNode);
            return false;
        }

        useHeroAbilitySucceeded(affectedRow, player, table);
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
