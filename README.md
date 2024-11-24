**Tudor Robert-Fabian**
**Anul 2024-2025**

# GwentStone Lite

## Description:

* The purpose of this project is to make use of all the OOP concepts learned in the first 3 laboratories and also get familiar with Javadocs by implementing the basic functionality of a card game called GwentStone Lite, which is a combination between Hearthstone and Gwent. To make the idea easier to follow, the input is generated in the form of a JSON file by 
AI to simulate the game and make sure all the rules are followed. 

* The mechanic of the game is pretty straight-forward: two players compete with each over the course of several games to see who wins more matches. Each player starts with a deck of cards, which is shuffled before the match begins, and a hero card given in the input. As long as there are cards in the deck, every time a player ends its turn, a card from its deck is drawn and placed in his hand. During several turns, players are allowed to make different actions such as: place a card on the table, use a card to attack, use the ability of a card or the hero card and many more. The game ends when a player's hero(which initially had 30 health points) reaches 0 health points and, at that point, the "Game over" message is displayed and the winner is the player whose hero has more than 0 health points.

</br>

## Explanations:

### Project structure:

* src/main/java/org.poo/fileio -> contains all the classes used for reading the input from the JSON files
   </br> **Observation:** I added 2 methods in the DeckInput class: one to make a deep copy of a deck with each card representing an instance of a class from the cards package and one to shuffle the newly created deck

* src/main/java/org.poo/main -> contains all the classes used for the logic of the game 
    - cards package -> contains 3 packages: herocards, minioncards, specialabilitycards and one class, Card, which is the superclass for all the cards
        - herocards package -> contains 5 classes: HeroCard and EmpressThorina, GeneralKocioraw, KingMudface, LordRoyce which are the subclasses of the HeroCard class
            - HeroCard class -> specially designed class for the hero cards which contains the fields and methods that are common for all the hero cards
            - EmpressThorina class -> subclass of the HeroCard class which contains the fields and methods specific to the Empress Thorina card
            - GeneralKocioraw class -> subclass of the HeroCard class which contains the fields and methods specific to the General Kocioraw card
            - KingMudface class -> subclass of the HeroCard class which contains the fields and methods specific to the King Mudface card
            - LordRoyce class -> subclass of the HeroCard class which contains the fields and methods specific to the Lord Royce card

        - minion package -> contains 5 classes: MinionCard and Berserker, Goliath, Sentinel and Warden which are the subclasses of the MinionCard class
            - MinionCard class -> specially designed class for the minion cards which contains the fields and methods that are common for all the minion cards 
            - Berserker class -> subclass of the MinionCard class which contains the fields and methods specific to the Berserker card
            - Goliath class -> subclass of the MinionCard class which contains the fields and methods specific to the Goliath card
            - Sentinel class -> subclass of the MinionCard class which contains the fields and methods specific to the Sentinel card
            - Warden class -> subclass of the MinionCard class which contains the fields and methods specific to the Warden card

        - specialabilitycards package -> contains 5 classes: SpecialAbilityCard and Disciple, Miraj, TheCursedOne, TheRipper which are the subclasses of the SpecialAbilityCard class
            - SpecialAbilityCard class -> specially designed class for the special ability cards which contains the fields and methods that are common for all the special ability cards 
            - Disciple class -> subclass of the SpecialAbilityCard class which contains the fields and methods specific to the Disciple card
            - Miraj class -> subclass of the SpecialAbilityCard class which contains the fields and methods specific to the Miraj card
            - TheCursedOne class -> subclass of the SpecialAbilityCard class which contains the fields and methods specific to the The Cursed One card
            - TheRipper class -> subclass of the SpecialAbilityCard class which contains the fields and methods specific to the The Ripper card
        
    - Command class -> contains processing methods for all commands given as input
    - CommandHandler class -> contains a method which has a switch case for all possible commands and calls the corresponding method from the Command class
    - Game class -> contains informations about the current game and a method which does all the necessary initializations for a new game
    - GameStats class -> contains information about the played games 
    - Main class -> makes the reading from the input, does the processing and writes the output in the output file
    - Player class -> contains information about the players, their decks, their cards in hands and their hero cards and methods to add and remove cards from the deck, from hand and 
    place cards on the table 
    - Table class -> contains information about the cards on the table and methods to add and remove cards from the table
    - Test class -> used for debugging purposes

</br>    

### Game rules:

* Each player has a deck of cards, a hero card and two rows to place cards on the table.
* A player draws a card from the deck at the beginning of its turn, but only if there are less than 5 cards already in its hand.
* A player can place a card on the table only under clear conditions: the card must be in the player's hand, the player has enough mana to play the card and there is enough space on the table.
* Since the table is 4x5 and each player has 2 rows, a player can have maximum 5 cards on the front row and 5 cards on the back row.
* There are clear specification about which cards can be places where:
    - Minion cards Sentinel and Berserker can be placed only on the back row
    - Minion cards Goliath and Warden can be placed only on the front row
    - Special Ability cards The Ripper and Miraj can be placed only on the front row
    - Special Ability cards The Cursed One and Disciple can be placed only on the back row
* No card can't attack/use its ability on the opponent cards as long as there is a tank card on the enemy rows(in this case only the tank cards can be attacked).
* A player can't attack/use the ability of a card/hero card in the same turn.
* Cards that are frozen can't attack/use their abilitities.
* At the end of a player's turn he gains an amount of mana equal to the round number, but this increase is capped at 10.
* The game ends when a player's hero reaches 0 health points.
* If a command is invalid meaning the conditions to execute it are not met, the game continues with the next command after displaying an error message.

</br>

### Commands:

*There are two types of possible commands: action commands and debug commands.*

#### **Action commands:**
This is what each action command does if it is valid:
* endPlayerTurn - ends the current player's turn
* placeCard - places a card from the current player's hand on the table
* cardUsesAttack - a card card from the current player's row attacks a card from the opponent's row
* cardUsesAbility - a card from the current player's row uses its ability on a card from the opponent's row or from its own row depending on the card ability
* useAttackHero - a card from the current player's row attacks the opponent's hero
* useHeroAbility - the current player's hero uses its ability on a row of the opponent's table or of its own dependind on the hero ability

However, if the action command is not valid, the game continues with the next command after displaying an error message specific to that command.

#### **Debug commands:**
This is what each debug command does if it is valid:
* getCardAtPosition - displays a card's fields from the given position from the table
* getPlayerMana - displays the the current mana of the player given in input
* getCardsInHand - displays the cards in the player's hand (the player is given in input) and their information
* getCardsOnTable - displays all the cards on the table and their information 
* getFrozenCardsOnTable - displays all the frozen cards on the table and their information
* getPlayerDeck - displays the cards in the player's deck (the player is given in input) and their fields
* getPlayerHero - displays the hero card of the player given in input and its fields
* getPlayerTurn - displays the current player whose turn is active
* getPlayerOneWins - displays the number of wins of the first player
* getPlayerTwoWins - displays the number of wins of the second player
* getTotalGamesPlayed - displays the total number of games played by the two players

Though, if the debug command is not valid, the game continues with the next command after displaying an error message specific to that command.

**Observation:** When I mentioned the verb "display" above i meant that the information is written in the output Arraynode to be send to the output file, but i used thata term to make it easier to understand.

</br>

### General observations:

* The game was designed to be scalable by using OOP concepts. If for some sort of reason the game needs to be expanded to have more cards, more players or more possible commands, it can be easily done without making major changes to the code. This can be performed by adding new classes that extend the existing ones and by creating new methods that handle new wanted actions.

* The game is structured in suitable classes and packages to make the code easy to read and understand. One of the main aspects of the project is the modularisation: each card has its own class, the commands are in a separate Command class, the players have their own class and so on, precisely to make the code as clean as possible.

* The current implementation uses Arraylists to store the cards in the deck, in hand or on table. This was performed to avoid possible problems provoked by the size, the index of addition or removal of the cards. 

* There are several Javadoc comments in the source code specially written to give additional explications about the functionality, the use and the parameters of the methods and constructors. 

</br>

## Conclusion:

This project was a great opportunity for my to put into practice all the concepts acknowledged during the first 3 laboratories and to make an idea about how a real-life project in Java is structored. Also, it was a good chance for me to comprehend the usefullness of Javadoc comments and how they can help a future reader better understand the implementation of the code. One other important aspect of this task was the fact that at the end of it I began to see the key differences between the functional programming and the OOP and how the latter can be more efficient in many cases, especially when the assignment is big and needs to be scalable. To sum up, this game was a good start into the Object-Oriented Programming world and I am looking forward to the next laboratories and projects to see how I can improve my skills and knowledge in this field.




















