package src

import hevs.graphics.FunGraphics
import src.game_class.{Maze, Player}
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}
import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}

/**
 * Main program
 */
object Main extends App{

  var maze: Maze = _
  private val showPath: Boolean = false //Show path to exit to player
  private val centerCamera: Boolean = true //Center camera to player
  private val cellMazeSize: Int = 32 //Maze's cell size
  private val mazeSize: Int = 21 //Maze size row and column
  private var playerDirection = 1; //Default number direction when starting game
  private var level: Int = 1; //Default level when starting game
  private var messageStartTime: Long = 0 //Time to display message
  private val display: FunGraphics = new FunGraphics(700,700 ,"Maze-breaker") //Global fungraphics
  private var displayMaze: DisplayMaze = _ //Display maze class
  private val startScreen = new StartScreen(display) //Starting game class
  private var player = new Player(0, 1) //Create player
  private var doorLockedMessage: Boolean = false; //Show door locked message
  private var isGameFinished = false; //Game is finished

  private val lock: Lock = new ReentrantLock()
  private val condition: Condition = lock.newCondition()
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
  displayMaze.showWindow() //Show maze
  addMovement() //Add player movement

  //Game loop
  while (true) {
    if (doorLockedMessage && (messageStartTime == 0)) messageStartTime = System.currentTimeMillis

    // Calculer le temps écoulé
    val currentTime = System.currentTimeMillis
    if (doorLockedMessage && (currentTime - messageStartTime > 3000)) {
      doorLockedMessage = false
      messageStartTime = 0
    }

    // Drawing
    display.frontBuffer.synchronized{
      display.clear(Color.black)
      displayMaze.drawMaze()
      displayMaze.drawPlayer(playerDirection)
      displayMaze.showNotif(doorLockedMessage)
      if(isGameFinished){
        level+=1
        newLevel()
        isGameFinished = false
      }
    }
    // FPS sync
    display.syncGameLogic(60)
  }

  /**
   * Change level
   */
  private def newLevel(): Unit = {
    var levelMaze: Int = mazeSize*level
    if(levelMaze % 2 == 0) levelMaze+=1
    maze = new Maze(levelMaze,levelMaze,cellMazeSize)
    player = new Player(maze.entry._1, maze.entry._2)
    displayMaze = new DisplayMaze(display, player, maze, showPath, centerCamera)
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

  /**
   * Add movement to player
   */
  private def addMovement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
        if(e.getKeyChar == 'p'){
          displayMaze.displayPath = !displayMaze.displayPath
        }else{
          if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
            playerDirection = 1
          } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
            playerDirection = 3
          } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
            playerDirection = 2
          } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
            playerDirection = 4
          }
          movePlayer(playerDirection)
        }}})
  }
}