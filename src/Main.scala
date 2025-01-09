package src

import hevs.graphics.FunGraphics
import src.game_class.Maze
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.Color
import java.awt.event.{KeyAdapter, KeyEvent}
import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}

object Main extends App{
  val maze: Maze = new Maze(21,21,32)
  val display: FunGraphics = new FunGraphics(700,700 ,"Maze-breaker")
  private val displayMaze: DisplayMaze = new DisplayMaze(display,maze,false,true)
  private val startScreen = new StartScreen(display)
  var messageStartTime: Long = 0
  var doorLockedMessage: Boolean = false;
  private val lock: Lock = new ReentrantLock()
  private val condition: Condition = lock.newCondition()
  private var playerDirection = 1;

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

  displayMaze.showWindow()
  addMovemement()

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
    }

    // FPS sync
    display.syncGameLogic(60)
  }

  private def addMovemement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
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
      }})
  }
}