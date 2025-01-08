package src

import hevs.graphics.FunGraphics
import src.game_class.Maze
import src.game_display.{DisplayMaze, StartScreen}

import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}

object Main extends App{
  val maze: Maze = new Maze(21,21,32)
  val display: FunGraphics = new FunGraphics(700,700 ,"Maze-breaker")
  private val displayMaze: DisplayMaze = new DisplayMaze(display,maze,false,true)
  private val startScreen = new StartScreen(display)

  // Lock and condition for better control
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
  displayMaze.showWindow()
}