package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player}

import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.{Color, Font}

class DisplayMaze(width: Int, height: Int, var maze: Maze = null, var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  var display: FunGraphics = _
  var offsetX: Int = 0
  var offsetY: Int = 0
  var player = new Player(0, 1)
  var messageStartTime: Long = 0
  var doorLockedMessage: Boolean = false;

  def showWindow(): Unit = {
    display = new FunGraphics(width,height, "Maze breaker")
    player = new Player(maze.entry._1, maze.entry._2)
    addMovemement()
  }

  def addMovemement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
          if (!maze.isCellAWall(player.getPosX(), player.getPosY() - 1)){
            if(maze.isCellExit(player.getPosX(), player.getPosY() - 1) && maze.isExitLock()){
              doorLockedMessage = true
            }else{
              player.move(0,-1)
              drawPlayer()
            }
          }
        } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
          if(!maze.isCellAWall(player.getPosX(), player.getPosY() + 1)){
            if(maze.isCellExit(player.getPosX(), player.getPosY() + 1) && maze.isExitLock()){
              doorLockedMessage = true
            }else{
              player.move(0, +1)
              drawPlayer()
            }
          }
        } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
          if (!maze.isCellAWall(player.getPosX() + 1, player.getPosY())){
            if(maze.isCellExit(player.getPosX() + 1, player.getPosY()) && maze.isExitLock()){
              doorLockedMessage = true
            }else{
              player.move(+1, 0)
              drawPlayer()
            }
          }
        } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
          if (!maze.isCellAWall(player.getPosX() - 1, player.getPosY())){
            if(maze.isCellExit(player.getPosX() - 1, player.getPosY()) && maze.isExitLock()){
              doorLockedMessage = true
            }else{
              player.move(-1, 0)
              drawPlayer()
            }
          }
        }
        maze.openExitIfPlayerOnKey(player.posX, player.posY)
    }})

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
        drawMaze()
        drawPlayer()
        if(doorLockedMessage){
          display.drawString(getXCoordWithOffset(player.getPosX())-50, getYCoordWithOffset(player.getPosY())-20, "The door is locked", new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
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
    if(centerCamera){
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
    if(centerCamera){
      display.drawFilledCircle(centerX, centerY, maze.cellSize)
    }else{
      display.drawFilledCircle(getXCoordWithOffset(player.getPosX()), getYCoordWithOffset(player.getPosY()), maze.cellSize)
    }
  }

  def getXCoordWithOffset(x: Int): Int = {
    x*maze.cellSize+offsetX
  }

  def getYCoordWithOffset(y: Int): Int = {
    y*maze.cellSize+offsetY
  }

  /**
   * Draw each cell
   * @param x coord x of cell
   * @param y coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    // Calculer les coordonnées de la cellule avec les offsets
    val drawX = getXCoordWithOffset(x)
    val drawY = getYCoordWithOffset(y)

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
        else baseColor
      } else baseColor

      display.setColor(finalColor)
      display.drawFillRect(drawX, drawY, cell.size, cell.size)
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
    }*/
  }
}
