package src

import hevs.graphics.FunGraphics
import src.game_class.{Maze, Player, Question}
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}
import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}
import scala.io.{BufferedSource, Source}
import scala.util.Random


/**
 * Main program
 */
object Main extends App{

  var maze: Maze = _ //Maze
  private val showPath: Boolean = false //Show path to exit to player
  private val centerCamera: Boolean = true //Center camera to player
  private val cellMazeSize: Int = 32 //Maze's cell size
  private val mazeSize: Int = 21 //Maze size row and column
  private var playerDirection = 1; //Default number direction when starting game
  private var messageStartTime: Long = 0 //Time to display message
  private val display: FunGraphics = new FunGraphics(700,700 ,"Maze-breaker") //Global fungraphics
  private var displayMaze: DisplayMaze = _ //Display maze class
  private val startScreen = new StartScreen(display) //Starting game class
  private var player = new Player(0, 1) //Create player
  private var doorLockedMessage: Boolean = false; //Show door locked message
  private var isGameFinished = false; //Game is finished
  private val lock: Lock = new ReentrantLock()
  private val condition: Condition = lock.newCondition()
  var level: Int = 1; //Default level when starting game
  private val questions: Array[Question] = readFile()   // Tableau de questions contenant les questions du fichier
  private var contenu: String = ""
  private var idQuestion: Int = 0
  private var isQuestion: Boolean = false
  private val validChar = Array(".", ",","[","]").concat(Array.range(0, 10).map(_.toString)).concat(Array.range('a', 'z' + 1).map(_.toChar.toString))
  private var isFrozen = false
  private var step: Int = 1
  private var eventStart: Long = 0
  private var eventLength: Long = 0
  private var allowCustomProperties: Boolean = true
  private var showAllMaze: Boolean = false

  var moveKeyListener: KeyAdapter = new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {
      if (e.getKeyChar == 'p' && allowCustomProperties) {
        displayMaze.displaySolutionPath()
      } else if(e.getKeyChar == 'm' && allowCustomProperties){
        showAllMaze = !showAllMaze
        displayMaze.displayMaze(showAllMaze)
      }else if(e.getKeyChar == 'l' && allowCustomProperties){
        displayMaze.displayDistanceCell()
      }else if(e.getKeyChar == 'k' && allowCustomProperties){
        displayMaze.displayAssignedNumber()
      } else {
        if (isFrozen) return
        if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
          playerDirection = 1
        } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
          playerDirection = 3
        } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
          playerDirection = 2
        } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
          playerDirection = 4
        }
        for (_ <- 0 until step) {
          movePlayer(playerDirection)
        }
        caseEvent(player.getPosX, player.getPosY)
      }
    }
  }

  var charKeyListener: KeyAdapter = new KeyAdapter {
    override def keyPressed(e: KeyEvent): Unit = {
      if (validChar.contains(e.getKeyChar.toString)) {
        contenu += e.getKeyChar
      }
      println(contenu)
      if (e.getKeyCode == KeyEvent.VK_ENTER) {
        display.mainFrame.removeKeyListener(charKeyListener)
        allowCustomProperties = false
        if (contenu.trim == questions(idQuestion).answer.trim) {
          contenu += " - Good answer"
          bonus(Random.between(0, 4))
        } else {
          contenu += " - Wrong => Good answer : " + questions(idQuestion).answer
          malus(Random.between(0, 5))
        }
        Thread.sleep(1500)
        contenu = ""
        isQuestion = false
        display.mainFrame.addKeyListener(moveKeyListener)
      } else if (e.getKeyCode == KeyEvent.VK_BACK_SPACE) {
        contenu = contenu.substring(0, math.max(contenu.length - 1, 0))
      }
    }
  }

  startScreen.setOnGameStart(() => {
    lock.lock()
    try {
      condition.signal()
    } finally {
      lock.unlock()
    }
  })

  lock.lock()
  try {
    condition.await() // Wait until `startGame` is set to true
  } finally {
    lock.unlock()
  }

  newLevel() //New level
  addMovement() //Add player movement

  //Game loop
  while (true) {
    if (doorLockedMessage && (messageStartTime == 0)) messageStartTime = System.currentTimeMillis

    // Time spend to show door message
    val currentTime = System.currentTimeMillis
    if (doorLockedMessage && (currentTime - messageStartTime > 3000)) {
      doorLockedMessage = false
      messageStartTime = 0
    }

    // Drawing
    display.frontBuffer.synchronized{
      display.clear(Color.black)
      if (System.currentTimeMillis() > eventLength + eventStart && !allowCustomProperties) {
        resetProperties()
        allowCustomProperties = true
      }
      if (!isQuestion) {
        displayMaze.drawMaze()
        displayMaze.drawPlayer(playerDirection)
        displayMaze.showNotif(doorLockedMessage)
        displayMaze.displayLevel(level.toString)

        if(isGameFinished){
          level+=1
          newLevel()
          isGameFinished = false
        }
      } else {
        displayMaze.drawQuestion(idQuestion, contenu)
      }
    }
    // FPS sync
    display.syncGameLogic(60)
  }

  /**
   * Add movement to player
   */
  private def addMovement(): Unit = {
    display.setKeyManager(moveKeyListener)
  }

  /**
   * Reset malus and bonus properties
   */
  private def resetProperties(): Unit = {
    step = 1
    isFrozen = false
    displayMaze.displayMaze(1)
    displayMaze.displaySolutionPath(false)
  }

  /**
   * Change level
   */
  private def newLevel(): Unit = {
    var levelMaze: Int = mazeSize*level
    if(levelMaze % 2 == 0) levelMaze+=1 // avoid to have even maze
    maze = new Maze(levelMaze,levelMaze,cellMazeSize)
    player = new Player(maze.entry._1, maze.entry._2)
    displayMaze = new DisplayMaze(display, player, maze, questions ,showPath, centerCamera)
    displayMaze.showWindow()
  }

  /**
   * Get coordinate direction with direction number
   * @param direction direction 1,2,3,4
   * @return direction coordinate
   */
  private def getDirectionCoord(direction: Int): (Int, Int) = {
    direction match {
      case 1 => (0,-1)
      case 2 => (+1, 0)
      case 3 => (0, +1)
      case 4 => (-1, 0)
    }
  }

  /**
   * Move player in the maze
   * @param direction player's direction
   */
  def movePlayer(direction: Int): Unit = {
    //Get movex and movey by direction
    val (movX, movY) = getDirectionCoord(direction)

    //Initialize value if player mov in certain direction
    val ifPlayerMoveX = player.getPosX + movX
    val ifPlayerMoveY = player.getPosY + movY

    //Move player only if destination cell is not a wall
    if (!maze.isCellAWall(ifPlayerMoveX, ifPlayerMoveY)){
      //If destination cell is an exit cell && exit is lock show message
      //If it's unlock, move player and finish game
      //Else move player in destination cell
      if(maze.isCellExit(ifPlayerMoveX, ifPlayerMoveY) && maze.isExitLock){
        doorLockedMessage = true
      }else if(maze.isCellExit(ifPlayerMoveX, ifPlayerMoveY) && !maze.isExitLock){
        player.move(movX,movY)
        isGameFinished = true
      }else{
        player.move(movX,movY)
      }
    }

    //Find the shortest path to exit from player
    maze.findShortestPath(player.getPosX, player.getPosY)

    //Open exit if player is on key cell
    maze.openExitIfPlayerOnKey(player.getPosX, player.getPosY)
  }

  // Créer une fonction readQuestion, lire le fichier et mettre toute les questions dans un tableau
  // Crée des zones aléatoire dans la labyrinthe, quand le joueur arrive sur la zone, affiche la question => OK
  private def readFile(): Array[Question] = {
    var questionStock2: Array[Question] = null
    var fileContent: Array[String] = null
    try {
      val filename = "src/questions/Questions.txt"
      val src: BufferedSource = Source.fromFile(filename)
      fileContent = src.getLines().toArray
      questionStock2 = new Array[Question](fileContent.length)
      for (line <- fileContent.indices) {
        questionStock2(line) = new Question(fileContent(line).split(";")(0), fileContent(line).split(";")(1))
      }
      src.close()

    } catch {
      case e: Exception => e.printStackTrace()
    }
    return questionStock2
  }

  private def bonus(randomBonus: Int): Unit = {
    // Voir le chemin de sortie pdt x temps
    if (randomBonus == 1) {
      displayMaze.displaySolutionPath(true)
      eventStart = System.currentTimeMillis()
      eventLength = 6500 // 5 secondes de bonus
    }
    // Vitesse x 2
    if (randomBonus == 2) {
      step = 2
      eventStart = System.currentTimeMillis()
      eventLength = 6500 // 5 secondes de bonus
    }
    // Dezoom
    if (randomBonus == 3) {
      displayMaze.displayMaze(5)
      eventStart = System.currentTimeMillis()
      eventLength = 6500 // 5 secondes de bonus
    }
  }

  private def malus(randomMalus: Int): Unit = {
    // Freeze pdt x temps
    if (randomMalus == 1) {
      isFrozen = true
      eventStart = System.currentTimeMillis()
      eventLength = 6500 // 5 secondes de bonus
    }
    // TP Random
    if (randomMalus == 2) {
      val (x,y) = maze.getRandomCell;
      player.setPosX(x)
      player.setPosY(y)
    } else if (randomMalus == 3) {
      step = 8
      eventStart = System.currentTimeMillis()
      eventLength = 6500 // 5 secondes de bonus
    }
    // Retour à l'entrée du labyrinthe
    else if(randomMalus == 4){
      player.setPosX(maze.entry._1)
      player.setPosY(maze.entry._2)
    }
  }

  // Création d'une nouvelle fenêtre contenant la question et une TextBox pour
  // répondre à la question affichée
  private def caseEvent(x: Int, y: Int): Unit = {
    if (maze.grid(x)(y).getClass.getSimpleName.equals("EventQuestions")) {
      idQuestion = Random.between(0, questions.length)
      isQuestion = true
      display.mainFrame.addKeyListener(charKeyListener)
      display.mainFrame.removeKeyListener(moveKeyListener)
      maze.triggerQuestionIfPlayerOnEvent(x,y)
    }
  }
}