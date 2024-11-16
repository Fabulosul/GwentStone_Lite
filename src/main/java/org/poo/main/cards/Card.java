package org.poo.main.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Player;
import org.poo.main.Table;


import java.util.ArrayList;

public class Card {
    public static final int INITIAL_HERO_HEALTH = 30;
    public enum Position {
        FRONT,
        BACK,
        NONE
    }
    private Position allowedPosition;
    private int mana;
    private int attackDamage;
    private int health;
    private boolean isFrozen;
    private boolean hasAttacked;
    private boolean hasSpecialAbility;
    private String description;
    private ArrayList<String> colors;
    private String name;

    public Card() {
        isFrozen = false;
        hasAttacked = false;
    }

    /**
     * Copy constructor used to create a deep copy of a card.
     * It copies the mana, attack damage, health, isFrozen status, hasAttacked status,
     * hasUsedAbility status, description, colors and name of the card given as parameter.
     *
     * @param card - the card that will be copied
     */
    public Card(final CardInput card) {
        this.mana = card.getMana();
        this.attackDamage = card.getAttackDamage();
        this.health = card.getHealth();
        this.isFrozen = card.isFrozen();
        this.hasAttacked = card.getHasAttacked();
        this.description = card.getDescription();
        this.colors = new ArrayList<>(card.getColors());
        this.name = card.getName();
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
    public void cardUseAction(final Coordinates cardAttackerCoordinates,
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
    public boolean cardUsesAttack(final Card cardAttacked,
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

        if (getHasAttacked() || (hasSpecialAbility() && ((SpecialAbilityCard)this).hasUsedAbility())) {
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
            ArrayList<Card> row = table.getTableCards().get(cardAttackedCoordinates.getX());
            row.remove(cardAttackedCoordinates.getY());
        }
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
    public void useAttackHeroSucceeded(final HeroCard heroAttacked, final int cardAttackerId,
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
                                 final HeroCard heroAttacked,
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

        if (getHasAttacked() || (hasSpecialAbility() && ((SpecialAbilityCard)this).hasUsedAbility())) {
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

    public boolean hasSpecialAbility() {
        return hasSpecialAbility;
    }

    public void setHasSpecialAbility(boolean hasSpecialAbility) {
        this.hasSpecialAbility = hasSpecialAbility;
    }

    public Position getAllowedPosition() {
        return allowedPosition;
    }

    public void setAllowedPosition(Position allowedPosition) {
        this.allowedPosition = allowedPosition;
    }
}
