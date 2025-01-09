package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player, Question}

import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.{Color, Font}

class DisplayMaze(width: Int, height: Int, var maze: Maze = null, var questions: Array[Question], var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  var display: FunGraphics = _
  var offsetX: Int = 0
  var offsetY: Int = 0
  var player = new Player(0, 1)

  private val defaultFont = new Font("SansSerif", Font.PLAIN, 24)
  var mainMenuMouseListener: MouseAdapter = _
  var charKeyListener: KeyAdapter = _
  var gameKeyListener: KeyAdapter = _
  var displayQuestion: FunGraphics = _

  def showWindow(): Unit = {
    display = new FunGraphics(width, height, "Maze breaker")
    player = new Player(maze.entry._1, maze.entry._2)
    addMovemement()
  }

  def addMovemement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
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
        maze.openExitIfPlayerOnKey(player.posX, player.posY)
        caseEvent(player.posX, player.posY)
      }
    })

    while (true) {
      // Drawing
      display.frontBuffer.synchronized {
        display.clear(Color.black)
        drawMaze()
        drawPlayer()
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
  // reépondre à la question affichée
  def caseEvent(x: Int, y: Int): Unit = {
    if (maze.grid(x)(y).getClass.getSimpleName.equals("EventQuestions")) {

      // Créé une nouvelle fenêtre contenant la question et la TextBox
      displayQuestion = new FunGraphics(350, 200, "Event - Question time")

      // Réutilisation de la classe Button pour afficher le texte de la question
      var btnQuestion: Button = new Button(50, 50, questions(0).questionShowed, 200, 30, displayQuestion) // Remplacer le 0 par un nbre random
      btnQuestion.displayButton(Color.WHITE, Color.BLACK, 20f)

      // Tout marche correctement et comme voulu jusqu'ici (manque random question)
      /* --------------------------------------------------------------------------------------- */
      // Création de la textBox
      drawTextBox(50, 100, "Insérez votre réponse", 50, 100, displayQuestion)

      // ----------------------------
      displayQuestion.mainFrame.getContentPane.removeMouseListener(mainMenuMouseListener)

      charKeyListener = new KeyAdapter {
        override def keyPressed(e: KeyEvent): Unit = {
          contenu += e.getKeyChar
          println(contenu)
          drawClientMenu(contenu)
          if (e.getKeyCode == KeyEvent.VK_ENTER) {
            println("Voici le contenu de la réponse" + contenu)
            if (contenu.trim == questions(0).answer.trim) {
              println("Good answer")
            } else {
              println(s"Mauvaise réponse, voici la bonne réponse : " + questions(0).answer)
            }
          }
        }
      }
      displayQuestion.mainFrame.addKeyListener(charKeyListener)
    }
  }

  var contenu: String = ""

  def drawTextBox(posX: Int, posY: Int, content: String, width: Int, height: Int, display: FunGraphics, font: Font = defaultFont): Unit = {

    display.setColor(Color.WHITE)
    //display.drawFillRect(posX, posY, width, height)
    display.drawFillRect(posX, posY + height, width, height)
    display.drawFillRect(posX + width, posY, width, height)
    display.drawString(posX + 10, posY, content, font, Color.BLACK)
  }

  //  def RecupAnswer(): Unit = {
  //    display.mainFrame.getContentPane.removeMouseListener(mainMenuMouseListener)
  //
  //    charKeyListener = new KeyAdapter {
  //      override def keyPressed(e: KeyEvent): Unit = {
  //        contenu += e.getKeyChar
  //        println("ew")
  //        drawClientMenu(contenu)
  //      }
  //    }
  //    display.mainFrame.addKeyListener(charKeyListener)
  //
  //    display.clear()
  //    drawClientMenu(contenu)
  //  }

  def drawClientMenu(t: String): Unit = {
    // displayQuestion.clear()
    drawTextBox(50, 100, contenu, 50, 100, displayQuestion, defaultFont)
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
