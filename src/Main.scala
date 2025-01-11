package src

import hevs.graphics.FunGraphics
import src.game_class.Maze
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}
import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}

object Main extends App{

  private val showPath: Boolean = false
  private val centerCamera: Boolean = true
  private val cellMazeSize: Int = 32
  private val mazeSize: Int = 21
  private var playerDirection = 1;
  private var level: Int = 1;
  private var messageStartTime: Long = 0

  private val display: FunGraphics = new FunGraphics(700,700 ,"Maze-breaker")
  private var displayMaze: DisplayMaze = _
  private val startScreen = new StartScreen(display)

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

  newLevel()
  displayMaze.showWindow()
  addMovemement()

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

  private def newLevel(): Unit = {
    val maze: Maze = new Maze(mazeSize,mazeSize,cellMazeSize)
    displayMaze = new DisplayMaze(display,maze,showPath,centerCamera)
    displayMaze.showWindow()
  }

  private def addMovemement(): Unit = {
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
        }

      }})
  }
}