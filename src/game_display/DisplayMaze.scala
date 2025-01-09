package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont
import src.game_class.{Cell, Exit, Maze, Player}
import hevs.graphics.utils.GraphicsBitmap
import src.Main
import src.Main.{displayMaze, doorLockedMessage, maze}

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

  def showWindow(): Unit = {
    player = new Player(maze.entry._1, maze.entry._2)
    initializeCellImage()
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

  def movePlayer(direction: Int): Unit = {
    val (movX, movY) = getDirectionCoord(direction)
    val ifPlayerMoveX = player.getPosX + movX
    val ifPlayerMoveY = player.getPosY + movY

    if (!maze.isCellAWall(ifPlayerMoveX, ifPlayerMoveY)){
      if(maze.isCellExit(ifPlayerMoveX, ifPlayerMoveY) && maze.isExitLock()){
        doorLockedMessage = true
      }else if(maze.isCellExit(ifPlayerMoveX, ifPlayerMoveY) && !maze.isExitLock()){

      }else{
        player.move(movX,movY)
      }
    }
    maze.openExitIfPlayerOnKey(player.posX, player.posY)
  }

  def showNotif(doorLocked: Boolean): Unit = {
    if(doorLocked){
      drawTextBox("The door is locked...")
    }
  }

  private def drawTextBox(text: String): Unit = {
    display.setColor(Color.WHITE)
    val font = new Font(Font.SANS_SERIF, Font.BOLD,16)
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
    display.drawString(textX, posY, text, font, new Color(0,0,0), 1,1)
  }

  /**
   * Draw maze generated
   */
  def drawMaze(): Unit = {
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

  private def getDirectionCoord(direction: Int): (Int, Int) = {
    direction match {
      case 1 => (0,-1)
      case 2 => (+1, 0)
      case 3 => (0, +1)
      case 4 => (-1, 0)
    }
  }

  def drawPlayer(direction: Int): Unit = {
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
