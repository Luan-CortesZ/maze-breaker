package src

import src.game_class.{Maze, Question}
import src.game_display.{DisplayMaze, StartScreen}

import java.awt.event.{WindowEvent, WindowListener}
import java.awt.Toolkit
import scala.io.{BufferedSource, Source}

object Main extends App {
  val questionLue: Array[Question] = lectureFichier()

  private val startScreen = new StartScreen("Maze Breaker")
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  val maze: Maze = new Maze(25, 25, 16)
  val displayMaze: DisplayMaze = new DisplayMaze(800, 800, maze, questionLue ,false, true)
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
  // Créer une fonction readQuestion, lire le fichier et mettre toute les questions dans un tableau
  // Crée des zones aléatoire dans la labyrinthe, quand le joueur arrive sur la zone, affiche la question => OK
  def lectureFichier(): Array[Question] = {
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


}
