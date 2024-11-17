package org.poo.main.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.fileio.Coordinates;
import org.poo.main.Player;
import org.poo.main.Table;
import org.poo.main.cards.herocards.HeroCard;
import org.poo.main.cards.specialabilitycards.SpecialAbilityCard;


import java.util.ArrayList;

public class Card {
    /** Enum used to specify the position of a card on the table. */
    public enum Position {
        FRONT,
        BACK,
        NONE
    }
    /** The position where a card can be placed on the table. */
    private Position allowedPosition;
    /** A boolean field that stores whether a card is frozen or not */
    private boolean isFrozen;
    /** A boolean field that stores whether a card has already attacked this turn or not */
    private boolean hasAttacked;
    /** A boolean field that stores whether a card has a special ability or not */
    private boolean hasSpecialAbility;
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;

    /**
     * Default constructor for the Card class that initializes the isFrozen
     * and hasAttacked fields to false.
     */
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

        if (getHasAttacked() || (hasSpecialAbility()
                && ((SpecialAbilityCard) this).hasUsedAbility())) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card has already attacked this turn.", objectNode, mapper);
            return false;
        }

        if (isFrozen()) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacker card is frozen.", objectNode, mapper);
            return false;
        }

        if (table.hasTankCards(cardAttackedId)
                && cardAttackedCoordinates.getX() != Table.PLAYER_TWO_FRONT_ROW
                    && cardAttackedCoordinates.getX() != Table.PLAYER_ONE_FRONT_ROW) {
            cardUsesAttackFailed(cardAttackerCoordinates, cardAttackedCoordinates,
                    "Attacked card is not of type 'Tank'.", objectNode, mapper);
            return false;
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

        if (getHasAttacked() || (hasSpecialAbility()
                && ((SpecialAbilityCard) this).hasUsedAbility())) {
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

    public final int getMana() {
        return mana;
    }

    public final void setMana(final int mana) {
        this.mana = mana;
    }

    public final int getAttackDamage() {
        return attackDamage;
    }

    public final void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public final int getHealth() {
        return health;
    }

    public final void setHealth(final int health) {
        this.health = health;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(final String description) {
        this.description = description;
    }

    public final ArrayList<String> getColors() {
        return colors;
    }

    public final void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final boolean getHasAttacked() {
        return hasAttacked;
    }

    public final void setHasAttacked(final boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    public final boolean isFrozen() {
        return isFrozen;
    }

    public final void setIsFrozen(final boolean isFrozen) {
        this.isFrozen = isFrozen;
    }

    /**
     * Getter for the hasSpecialAbility field
     * @return true if the card has a special ability, false otherwise
     */
    public final boolean hasSpecialAbility() {
        return hasSpecialAbility;
    }

    public final void setHasSpecialAbility(final boolean hasSpecialAbility) {
        this.hasSpecialAbility = hasSpecialAbility;
    }

    public final Position getAllowedPosition() {
        return allowedPosition;
    }

    public final void setAllowedPosition(final Position allowedPosition) {
        this.allowedPosition = allowedPosition;
    }
}
