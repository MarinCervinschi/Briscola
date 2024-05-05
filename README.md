# Briscola

### UTM
````mermaid
classDiagram
    MainApp --|>  GameController
    GameController --|> Board
Const
    Menu --|> GameController
class MainApp{
-screen: Stage
+main()
    }
class GameController{
-nextPlayer: String
+showBackground()
+showCards()
+showLastMove()
+showHover()
+nextTurn()
    }
class Board{
-create()
-scoreboard()
-addCards()
-move()
-checkResult()
-calcPoints()
-updateScore()
}
class Menu{
-screen: Stage
+play()
+rules()
}
class Const{
-WIDTH: Integer
-HEIGHT: Integer
-CARDS: Integer
-SEED: Integer
-CSIZE: Integer
}

````