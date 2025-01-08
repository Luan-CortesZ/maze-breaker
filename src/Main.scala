package src

import hevs.graphics.FunGraphics
import src.game_class.Maze
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.event.{WindowEvent, WindowListener}
import java.awt.{Color, Toolkit}
import java.util.concurrent.locks.{Condition, Lock, ReentrantLock}

object Main extends App{
  val maze: Maze = new Maze(21,21,128)
  val display: FunGraphics = new FunGraphics(1400,1400 ,"Maze-breaker")
  val displayMaze: DisplayMaze = new DisplayMaze(display,maze,false,true)
  val startScreen = new StartScreen(display)

  // Lock and condition for better control
  val lock: Lock = new ReentrantLock()
  val condition: Condition = lock.newCondition()

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