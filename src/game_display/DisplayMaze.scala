package src.game_display

import hevs.graphics.FunGraphics
import src.Main.maze.grid
import src.fonts.CustomFont
import src.game_class.{Cell, Exit, Maze, Player, Question}

import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.{Color, Font}
import scala.util.Random

class DisplayMaze(width: Int, height: Int, var maze: Maze = null, var questions: Array[Question], var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  var display: FunGraphics = _
  var offsetX: Int = 0
  var offsetY: Int = 0
  var player = new Player(0, 1)

  var defaultFont = new Font("SansSerif", Font.PLAIN, 24)
  var mainMenuMouseListener: MouseAdapter = _
  var charKeyListener: KeyAdapter = _
  charKeyListener =  new KeyAdapter {
    override def keyPressed(e: KeyEvent): Unit = {
      if (validChar.contains(e.getKeyChar.toString)){
        contenu += e.getKeyChar
      }
      println(contenu)

      if (e.getKeyCode == KeyEvent.VK_ENTER) {
        display.mainFrame.removeKeyListener(charKeyListener)
        if (contenu.trim == questions(getIdQuestion).answer.trim) {
          contenu += " - Good answer"
          bonus(Random.between(0,4))
        } else {
          contenu += " - Wrong => Good answer : " + questions(getIdQuestion).answer
          malus(Random.between(0,5))
        }
        Thread.sleep(1500)
        contenu = ""
        isQuestion = false
        display.mainFrame.addKeyListener(moveKeyListener)
      } else if (e.getKeyCode == KeyEvent.VK_BACK_SPACE ){
        contenu = contenu.substring(0, math.max(contenu.length - 1, 0))
      }
    }
  }
  var moveKeyListener = new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {
      if (isFrozen) return
      for(_<- 0 until step){
        if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
          if (!maze.isCellAWall(player.getPosX(), player.getPosY() - 1)) {
            player.move(0, -1)
            drawPlayer()
          }
        } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
          if (!maze.isCellAWall(player.getPosX(), player.getPosY() + 1)) {
            player.move(0, +1)
            drawPlayer()
          }
        } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
          if (!maze.isCellAWall(player.getPosX() + 1, player.getPosY())) {
            player.move(1, 0)
            drawPlayer()
          }
        } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
          if (!maze.isCellAWall(player.getPosX() - 1, player.getPosY())) {
            player.move(-1, 0)
            drawPlayer()
          }
        }
      }
      maze.openExitIfPlayerOnKey(player.posX, player.posY)
      caseEvent(player.posX, player.posY)
    }
  }
  var gameKeyListener: KeyAdapter = _
  var displayQuestion: FunGraphics = _
  var getIdQuestion: Int = 0
  var isQuestion: Boolean = false
  var validChar = Array(".").concat(Array.range(0, 10).map(_.toString)).concat(Array.range('a', 'z' + 1).map(_.toChar.toString))
  var step: Int = 1
  var eventStart: Long = 0
  var eventLength: Long = 0
  var isFrozen = false
  var pathView = false
  var dezooom = false

  def showWindow(): Unit = {
    display = new FunGraphics(width, height, "Maze breaker")
    player = new Player(maze.entry._1, maze.entry._2)
    addMovemement()
  }

  def addMovemement(): Unit = {
    display.setKeyManager(moveKeyListener)

    while (true) {
      // Drawing
      display.frontBuffer.synchronized {
        display.clear(Color.black)
        if (System.currentTimeMillis() > eventLength + eventStart){
          step = 1
          isFrozen = false
          pathView = false
          dezooom = false
        }
        if (!isQuestion){
          drawMaze()
          drawPlayer()
        } else{
          drawQuestion()
        }
      }
      // FPS sync
      display.syncGameLogic(60)
    }

  }

  /**
   * Draw maze generated
   */
  def drawMaze(): Unit = {
    offsetX = (display.width - maze.GRID_WIDTH) / 2
    offsetY = (display.height - maze.GRID_HEIGHT) / 2
    if (centerCamera) {
      // Calculer les offsets dynamiquement pour centrer la vue sur le joueur
      offsetX = display.width / 2 - player.getPosX() * maze.cellSize
      offsetY = display.height / 2 - player.getPosY() * maze.cellSize
    }

    // Dessiner les cellules visibles
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  def drawPlayer(): Unit = {
    // Dessiner le joueur au centre de la fenêtre
    display.setColor(Color.RED)
    val centerX = display.width / 2
    val centerY = display.height / 2
    if (centerCamera) {
      display.drawFilledCircle(centerX, centerY, maze.cellSize)
    } else {
      display.drawFilledCircle(player.getPosX() * maze.cellSize + offsetX, player.getPosY() * maze.cellSize + offsetY, maze.cellSize)
    }
  }

  /**
   * Draw each cell
   *
   * @param x    coord x of cell
   * @param y    coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    // Calculer les coordonnées de la cellule avec les offsets
    val drawX = x * cell.size + offsetX
    val drawY = y * cell.size + offsetY

    // Vérifier si la cellule est dans la zone visible avant de la dessiner
    if (drawX + cell.size >= 0 && drawX <= display.width &&
      drawY + cell.size >= 0 && drawY <= display.height) {

      // Couleur de base
      val baseColor = if (cell.isWall) {
        if ((x + y) % 2 == 0) new Color(25, 25, 25) else new Color(10, 10, 10)
      } else {
        if ((x + y) % 2 == 0) new Color(230, 230, 230) else new Color(205, 205, 205)
      }

      // Couleur finale spécifique
      val finalColor = if (!cell.isWall) {
        if (cell.getClass.getSimpleName.equals("Exit") && cell.asInstanceOf[Exit].isLock) new Color(255, 0, 0)
        else if (cell.getClass.getSimpleName.equals("Exit") && !cell.asInstanceOf[Exit].isLock) new Color(0, 125, 0)
        else if (cell.getClass.getSimpleName.equals("Entry")) new Color(0, 255, 255)
        else if (cell.isPathToExit && displayPath) new Color(0, 255, 0)
        else if (cell.getClass.getSimpleName.equals("Key")) new Color(255, 255, 0)
        else if (cell.getClass.getSimpleName.equals("EventQuestions")) new Color(255, 0, 255)
        else baseColor
      } else baseColor

      display.setColor(finalColor)
      display.drawFillRect(drawX, drawY, cell.size, cell.size)
    }


  }

  // Création d'une nouvelle fenêtre contenant la question et une TextBox pour
  // répondre à la question affichée
  def caseEvent(x: Int, y: Int): Unit = {
    getIdQuestion = Random.between(0, questions.length)
    if (maze.grid(x)(y).getClass.getSimpleName.equals("EventQuestions")) {
      isQuestion = true
      display.mainFrame.addKeyListener(charKeyListener)
      display.mainFrame.removeKeyListener(moveKeyListener)
      grid(x)(y) = new Cell(grid(x)(y).size,grid(x)(y).isWall,grid(x)(y).number,grid(x)(y).distanceFromExit,grid(x)(y).isPathToExit)
    }
  }

  var contenu: String = ""

  def bonus(randombonus2: Int): Unit = {
    // Voir le chemin de sortie pdt x temps
    // Vitesse x 2
    if (randombonus2 == 2){
      step = 2
      eventStart = System.currentTimeMillis()
      eventLength = 6500      // 5 secondes de bonus
    }
    // Dezoom
  }
  def malus(randomMalus: Int): Unit = {
    // Freeze pdt x temps
    if (randomMalus == 1){
      isFrozen = true
      eventStart = System.currentTimeMillis()
      eventLength = 6500      // 5 secondes de bonus
    }
    // TP Random
    if(randomMalus == 2){
      var tmpFound = false
      while (!tmpFound){
        var randomX = Random.between(0, grid.length)
        var randomY = Random.between(0, grid(0).length)
        tmpFound = !grid(randomX)(randomY).isWall
        player.posX = randomX
        player.posY = randomY
      }
    } else if (randomMalus == 3){
      step = 8
      eventStart = System.currentTimeMillis()
      eventLength = 6500      // 5 secondes de bonus
    }
    // Retour à l'entrée du labyrinthe
  }

  def drawTextBox(posX: Int, posY: Int, content: String, width: Int, height: Int, display: FunGraphics, font: Font = defaultFont, backgroundColor: Color = Color.WHITE): Unit = {

    // display.getStringSize(content,defaultFont).getWidth.floor.toInt + 40
    var stringSize = display.getStringSize(content,defaultFont).getWidth.floor.toInt + 40

    var btnAnswer: Button = new Button(20, 100, contenu, stringSize, 30, display)
    btnAnswer.displayButton(Color.WHITE, Color.BLACK, 20f)
  }

  def drawQuestion(): Unit ={
    // Réutilisation de la classe Button pour afficher le texte de la question
    var btnQuestion: Button = new Button(20, 50, questions(getIdQuestion).questionShowed, display.getStringSize(questions(getIdQuestion).questionShowed,defaultFont).getWidth.floor.toInt + 40, 30, display)
    btnQuestion.displayButton(Color.WHITE, Color.BLACK, 20f)

    // Création de la textBox
    drawTextBox(250, 100, contenu, 300, 100, display)
  }

  /*
  //Show number assigned to cell
  if(cell.isWall) {
    display.drawString(x*cell.size+offsetX,y*cell.size+offsetY, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(255,255,255), 1,1)
  }else{
    display.drawString(x*cell.size+offsetX,y*cell.size+offsetY, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
  }

  //Show distance from exit
  if(!cell.isWall) {
    display.drawString(x*cell.size+offsetX,y*cell.size+offsetY, cell.distanceFromExit.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
  }
}*/
}
