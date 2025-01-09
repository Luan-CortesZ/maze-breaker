package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont
import src.game_class.{Cell, Exit, Maze, Player}
import hevs.graphics.utils.GraphicsBitmap

import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.Color
import scala.util.Random
import java.awt.font.FontRenderContext
import java.awt.{Color, Font}

class DisplayMaze(var display: FunGraphics, var maze: Maze = null, var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  private var offsetX: Int = 0
  private var offsetY: Int = 0
  val image: Image = new Image()
  private var player = new Player(0, 1)
  private var playerDirection = 1;
  var messageStartTime: Long = 0
  var doorLockedMessage: Boolean = false;

  def showWindow(): Unit = {
    player = new Player(maze.entry._1, maze.entry._2)
    initializeCellImage()
    addMovemement()
  }

  private def initializeCellImage(): Unit = {
    // Dessiner les cellules visibles
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      var cellImage: GraphicsBitmap = null
      if (maze.grid(x)(y).isWall){
        cellImage = image.wallPicture
      }else if (!maze.grid(x)(y).isWall){
        cellImage = image.lstGroundPictures(Random.nextInt(image.lstGroundPictures.length))
        if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && maze.grid(x)(y).asInstanceOf[Exit].isLock){
          cellImage = image.locked_door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && !maze.grid(x)(y).asInstanceOf[Exit].isLock){
          cellImage = image.opened_door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Entry")){
          cellImage = image.entry_door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Key")){
          cellImage = image.keyPicture
        }
      }
      if(maze.grid(x)(y).isWall && maze.isWallInsideMaze(x,y) && Random.nextInt(3) == 1){
        maze.grid(x)(y).hasTorch = true
      }
      maze.grid(x)(y).setImage(cellImage)
    }
  }

  private def addMovemement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
          if (!maze.isCellAWall(player.getPosX, player.getPosY - 1)){
            if(maze.isCellExit(player.getPosX, player.getPosY - 1) && maze.isExitLock()){
              doorLockedMessage = true
            }else if(maze.isCellExit(player.getPosX, player.getPosY - 1) && !maze.isExitLock()){

            }else{
              player.move(0,-1)
              playerDirection = 1
            }
          }
        } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
          if(!maze.isCellAWall(player.getPosX, player.getPosY + 1)){
            if(maze.isCellExit(player.getPosX, player.getPosY + 1) && maze.isExitLock()){
              doorLockedMessage = true
            }else if(maze.isCellExit(player.getPosX, player.getPosY + 1) && !maze.isExitLock()){

            }else{
              player.move(0, +1)
              playerDirection = 3
            }
          }
        } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
          if (!maze.isCellAWall(player.getPosX + 1, player.getPosY)){
            if(maze.isCellExit(player.getPosX + 1, player.getPosY) && maze.isExitLock()){
              doorLockedMessage = true
            }else if(maze.isCellExit(player.getPosX + 1, player.getPosY) && !maze.isExitLock()){

            }else{
              player.move(+1, 0)
              playerDirection = 2
            }
          }
        } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
          if (!maze.isCellAWall(player.getPosX - 1, player.getPosY)){
            if(maze.isCellExit(player.getPosX - 1, player.getPosY) && maze.isExitLock()){
              doorLockedMessage = true
            }else if(maze.isCellExit(player.getPosX - 1, player.getPosY) && !maze.isExitLock()){

            }else{
              player.move(-1, 0)
              playerDirection = 4
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
        drawPlayer(playerDirection)
        if(doorLockedMessage){
          drawTextBox("The door is locked...")
        }
      }

      // FPS sync
      display.syncGameLogic(60)
    }
  }

  def drawTextBox(text: String): Unit = {
    display.setColor(Color.WHITE)
    val font = new Font("Sans Serif", 0, maze.cellSize/2)
    val fontRenderContext = new FontRenderContext(null, true, true)
    val fontMetrics = font.getLineMetrics(text, fontRenderContext)
    val textHeight = fontMetrics.getAscent.toInt
    val textWidth = font.getStringBounds(text, fontRenderContext).getWidth.toInt
    val rectWidth = textWidth+10
    val rectHeight = textHeight+10
    val posX = getXCoordWithOffset(player.getPosX)-rectWidth/2+maze.cellSize/2
    val posY = getYCoordWithOffset(player.getPosY)-rectHeight
    val descent = fontMetrics.getDescent
    val textX: Int = posX + (rectWidth - textWidth) / 2
    display.drawFillRect(posX, posY, rectWidth, rectHeight)
    display.drawString(textX, posY, text, font, new Color(0,0,0), 1,1)
  }

  /**
   * Draw maze generated
   */
  private def drawMaze(): Unit = {
    offsetX = (display.width - maze.GRID_WIDTH) / 2
    offsetY = (display.height - maze.GRID_HEIGHT) / 2
    if(centerCamera){
      // Calculer les offsets dynamiquement pour centrer la vue sur le joueur
      offsetX = display.width / 2 - player.getPosX * maze.cellSize
      offsetY = display.height / 2 - player.getPosY * maze.cellSize
    }

    // Dessiner les cellules visibles
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  private def drawPlayer(direction: Int): Unit = {
    // Dessiner le joueur au centre de la fenêtre
    val centerX = display.width / 2
    val centerY = display.height / 2
    val playerPicture = direction match {
      case 1 => image.playerTop
      case 2 => image.playerRight
      case 3 => image.playerDown
      case 4 => image.playerLeft
    }
    if(centerCamera){
      display.drawTransformedPicture(centerX + maze.cellSize/2, centerY + maze.cellSize/2, 0, maze.cellSize/32, playerPicture)
    }else{
      display.drawTransformedPicture(player.getPosX*maze.cellSize+offsetX + maze.cellSize/2, player.getPosY*maze.cellSize+offsetY + maze.cellSize/2, 0, maze.cellSize/32, playerPicture)
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
    if (drawX + cell.size >= 0 && drawX <= display.width && drawY + cell.size >= 0 && drawY <= display.height) {
      val finalColor = {
        if (cell.isPathToExit && displayPath) new Color(0, 255, 0)
        else new Color(0,0,0)
      }

      display.drawTransformedPicture(drawX + cell.size/2, drawY + cell.size/2, 0, cell.size/32, image.lstGroundPictures.head)
      display.drawTransformedPicture(drawX + cell.size/2, drawY + cell.size/2, 0, cell.size/32, cell.image)

      if(cell.hasTorch){
        display.drawTransformedPicture(drawX + cell.size/2, drawY + cell.size/2, 0, cell.size/32, image.torch)
      }

      display.setColor(finalColor)
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
}
