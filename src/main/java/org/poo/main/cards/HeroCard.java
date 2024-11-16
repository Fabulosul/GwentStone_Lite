package org.poo.main.cards;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CardInput;
import org.poo.main.Player;
import org.poo.main.Table;


import java.util.ArrayList;

public class HeroCard extends Card {
    public static final int INITIAL_HERO_HEALTH = 30;
    private boolean hasUsedHeroAbility;


    public HeroCard() {
        super();
        setHealth(INITIAL_HERO_HEALTH);
        setHasUsedHeroAbility(false);
        setHasSpecialAbility(true);
        setAllowedPosition(Position.NONE);
    }

    public HeroCard(final CardInput card) {
        super(card);
        setHealth(INITIAL_HERO_HEALTH);
        setHasUsedHeroAbility(false);
        setHasSpecialAbility(true);
        setAllowedPosition(Position.NONE);
    }

    public static HeroCard createHeroCard(final CardInput heroCard) {
        return switch (heroCard.getName()) {
            case "Lord Royce" -> new LordRoyce(heroCard);
            case "Empress Thorina" -> new EmpressThorina(heroCard);
            case "King Mudface" -> new KingMudface(heroCard);
            case "General Kocioraw" -> new GeneralKocioraw(heroCard);
            default -> null;
        };
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
     */
    public void useHeroAbilitySucceeded(final ArrayList<Card> affectedRow,
                                        final Player player) {
        setHasUsedHeroAbility(true);
        player.setMana(player.getMana() - getMana());
        useHeroAbility(affectedRow);
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

        if (hasUsedHeroAbility()) {
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

        useHeroAbilitySucceeded(table.getTableCards().get(affectedRow), player);
        return true;
    }


    public void useHeroAbility(ArrayList<Card> row) {
        hasUsedHeroAbility = true;
    }

    public boolean hasUsedHeroAbility() {
        return hasUsedHeroAbility;
    }

    public void setHasUsedHeroAbility(boolean hasUsedHeroAbility) {
        this.hasUsedHeroAbility = hasUsedHeroAbility;
    }
}
