package src

import hevs.graphics.FunGraphics
import src.game_class.Maze
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}
import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}

/**
 * Main program
 */
object Main extends App{

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
    if (displayMaze.doorLockedMessage && (messageStartTime == 0)) messageStartTime = System.currentTimeMillis

    // Calculer le temps écoulé
    val currentTime = System.currentTimeMillis
    if (displayMaze.doorLockedMessage && (currentTime - messageStartTime > 3000)) {
      displayMaze.doorLockedMessage = false
      messageStartTime = 0
    }

    // Drawing
    display.frontBuffer.synchronized{
      display.clear(Color.black)
      displayMaze.drawMaze()
      displayMaze.drawPlayer(playerDirection)
      displayMaze.showNotif()
      if(displayMaze.finishGame){
        level+=1
        newLevel()
      }
    }
    // FPS sync
    display.syncGameLogic(60)
  }

  /**
   * Change level
   */
  private def newLevel(): Unit = {
    val maze: Maze = new Maze(mazeSize,mazeSize,cellMazeSize)
    displayMaze = new DisplayMaze(display,maze,showPath,centerCamera)
    displayMaze.showWindow()
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
          displayMaze.movePlayer(playerDirection)
        }}})
  }
}