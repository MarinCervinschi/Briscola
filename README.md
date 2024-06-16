# Briscola

### UML
````mermaid
classDiagram
direction BT
class Animation {
  + Animation() 
  + createTransition(Rectangle, int, int) void
  + fadeTransition(Node) void
}
class Board {
  + Board() 
  - Deque~Card~ deck
  - Rectangle briscolaObject
  - Card briscolaCard
  - Hand playerHand
  - Hand botHand
  + getTable(int) Card
  + createDeck() void
  + clearTable() void
  + shuffleDeck() void
  + addCardToTable(Card) void
  + tableIsFull() boolean
   Card briscolaCard
   Deque~Card~ deck
   String briscolaToCards
   Hand playerHand
   Hand botHand
   Rectangle briscolaObject
}
class Bot {
  + Bot(GameObjects, String) 
  - Hand hand
  - boolean hasPlayed
  - checkValue(Rectangle) Rectangle
  - getCardValue(Card) int
  - findMinCardName(boolean) Rectangle
  + move() void
  - probabilityToHaveHigherCardSameSeed(List~Card~, int, Card) double
  - findMaxCardName(String, boolean) Rectangle
  - mediumMove() Rectangle
  - complementaryProbability(double, int) double
  - getRectangle(Card) Rectangle
  - probabilityToHaveBriscola(List~Card~, int) double
  - calcProbability(int, int, int, int) double
  - easyMove() Rectangle
  - calcBestCardToPlay(List~Card~, int, List~Card~) Pair~Card, Double~
  - hardMove() Rectangle
  - findBestCard(Card) Rectangle
  - getCard(Rectangle) Card
   boolean hasPlayed
   Hand hand
}
class Card {
  + Card(String, String, int, boolean) 
  - boolean isBriscola
  - String name
  - int value
  - String seed
  + equals(Object) boolean
  + toString() String
   String name
   boolean isBriscola
   int value
   String seed
}
class Const {
  - Const() 
}
class GameController {
  + GameController() 
  + initialize() void
  + startGame() void
  - addMenuIconAction() void
  + exit() void
  + showRules() void
  - start() void
}
class GameInit {
  + GameInit(GameObjects) 
  - List~Card~ playedCards
  - boolean gameEnded
  - boolean isPauseActive
  - Bot bot
  - Hand playerHand
  - fillHandAndSwitchTurn(Rectangle, Card) void
  - resetGame(boolean) void
  - endGame() void
  + mainLoop() void
  - handleBriscolaCard(Rectangle) void
  - findWinningCard(Card[], int[]) Rectangle[]
  - getEndGameMessage(int, int) Label
  - adjustCardValue(int) int
  - getPauseTransition(int, int, boolean) PauseTransition
  - checkTable() void
  - fillBotHand(Rectangle, Card) void
  - updatePoints(Label, int) void
  - selectCard(Rectangle, Card) void
  - checkForName(Card[], int[]) Rectangle[]
  - fillHands() void
  - fillPlayerHand(Rectangle, Card) void
   boolean gameEnded
   Hand playerHand
   List~Card~ playedCards
   Node[] visibilityFalse
   boolean isPauseActive
   Card[] cardsFromTable
   Rectangle[] rectangleFromTable
   Bot bot
}
class GameObjects {
  + GameObjects(BorderPane, AnchorPane) 
  - Label botPoints
  - Label botTurn
  - Label playerPoints
  - BorderPane tablePane
  - MenuButton menuIcon
  - Deque~Rectangle~ deckObject
  - HBox botHandBox
  - HBox playerHandBox
  - Board board
  - Label deckCards
  - HBox tableBox
  - Label playerTurn
  - createMenuIcon() MenuButton
  - setTurnIcon(String, Label) void
  + createCardObject(Card) Rectangle
  - createLabel(double, double, double) Label
  - initializeDeckBox() void
  + initializeGameObjects() void
  + createGameObjects() void
  - createBorderPane() void
  + initializePoints() void
  + appendHandsObject() void
  + showBackground() void
   BorderPane tablePane
   HBox botHandBox
   Label playerTurn
   MenuButton menuIcon
   HBox tableBox
   Label deckCards
   Deque~Rectangle~ deckObject
   HBox playerHandBox
   Board board
   Label botPoints
   Label botTurn
   Label playerPoints
}
class Hand {
  + Hand() 
  - List~Rectangle~ cardsObject
  - List~Card~ cards
  + removeCard(Card, Rectangle) void
  + addCard(Card, Rectangle) void
   boolean cardsObjectFull
   int length
   boolean emptyObject
   Card[] cards
   Rectangle[] cardsObject
}
class MainApp {
  + MainApp() 
  + start(Stage) void
  + main(String[]) void
}
class Sound {
  + Sound() 
  + play(String) void
}
class Style {
  + Style() 
  + setTurnStyle(boolean, GameObjects) void
  + showHoverEffect(Rectangle) void
  + setMenuIconHover(MenuButton, TranslateTransition) void
   Rectangle cardStyle
}

Board "1" *--> "table *" Card 
Board  ..>  Card : «create»
Board  ..>  Hand : «create»
Board "1" *--> "playerHand 1" Hand 
Bot  -->  GameInit 
Bot "1" *--> "gameObjects 1" GameObjects 
Bot "1" *--> "hand 1" Hand 
GameController  ..>  Bot : «create»
GameController  ..>  GameInit : «create»
GameController "1" *--> "gameInit 1" GameInit 
GameController "1" *--> "gameObjects 1" GameObjects 
GameController  ..>  GameObjects : «create»
GameInit "1" *--> "bot 1" Bot 
GameInit  ..>  Card : «create»
GameInit "1" *--> "playedCards *" Card 
GameInit "1" *--> "gameObjects 1" GameObjects 
GameInit "1" *--> "playerHand 1" Hand 
GameObjects "1" *--> "board 1" Board 
GameObjects  ..>  Board : «create»
GameObjects  ..>  Card : «create»
Hand "1" *--> "cards *" Card 
Hand  ..>  Card : «create»



````
