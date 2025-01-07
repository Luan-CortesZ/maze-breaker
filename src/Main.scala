package src

import src.game_class.Maze
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.event.{WindowEvent, WindowListener}
import java.awt.Toolkit

object Main extends App{
  private val startScreen = new StartScreen("Maze Breaker")
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  val maze: Maze = new Maze(25,25,128)
  val displayMaze: DisplayMaze = new DisplayMaze(800,800,maze,false,true)
  displayMaze.showWindow()

  //Add window listener to startScreen frame
  startScreen.display.mainFrame.addWindowListener(new WindowListener {
    override def windowOpened(e: WindowEvent): Unit = {}

    override def windowClosing(e: WindowEvent): Unit = {}

    override def windowClosed(e: WindowEvent): Unit = {}

    override def windowIconified(e: WindowEvent): Unit = {}

    override def windowDeiconified(e: WindowEvent): Unit = {}

    override def windowActivated(e: WindowEvent): Unit = {}

    //When window closed
    //Create the maze and display it
    override def windowDeactivated(e: WindowEvent): Unit = {
    }
  })





}
