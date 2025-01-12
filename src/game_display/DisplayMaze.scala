package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player}
import hevs.graphics.utils.GraphicsBitmap
import java.awt.Color
import scala.util.Random
import java.awt.font.FontRenderContext
import java.awt.Font

/**
 * Display maze window
 * @param display fungraphics window
 * @param maze maze generated
 * @param displayPath displayPath or not
 * @param centerCamera centerCamera on player or not
 */
class DisplayMaze(var display: FunGraphics, var player: Player, var maze: Maze = null, var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  private var offsetX: Int = 0 //OffsetX to center maze
  private var offsetY: Int = 0 //OffsetY to center maze
  private val image: Image = new Image() //Bank of image
  private val randomGroundPicture = Random.nextInt(image.lstGroundPictures.length)

  /**
   * Show maze in window
   */
  def showWindow(): Unit = {
    initializeCellImage()
  }

  /**
   * Initialize each image for specific cells
   */
  private def initializeCellImage(): Unit = {
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      var cellImage: GraphicsBitmap = null
      if (maze.grid(x)(y).isWall){
        cellImage = image.wallPicture //Wall picture
      }else if (!maze.grid(x)(y).isWall){
        cellImage = image.lstGroundPictures(Random.nextInt(image.lstGroundPictures.length)) //Random ground pictures
        if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && maze.grid(x)(y).asInstanceOf[Exit].isLock){
          cellImage = image.locked_door //Exit door locked
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && !maze.grid(x)(y).asInstanceOf[Exit].isLock){
          cellImage = image.opened_door //Exit door opened
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Entry")){
          cellImage = image.entry_door //Entry door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Key")){
          cellImage = image.keyPicture //Key picture
        }
      }
      if(maze.grid(x)(y).isWall && maze.isValidCell(x,y) && Random.nextInt(5) == 1){
        maze.grid(x)(y).hasTorch = true //Random wall has torch or not
      }
      maze.grid(x)(y).setImage(cellImage) //Set cell image
    }
  }

  /**
   * Show message
   */
  def showNotif(doorLockedMessage: Boolean): Unit = {
    if(doorLockedMessage){
      drawTextBox("The door is locked...")
    }
  }

  /**
   * Draw textbox with message
   * @param text text to display
   */
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
    val textX: Int = posX + (rectWidth - textWidth) / 2
    display.drawString(textX, posY, text, font, new Color(0,0,0), 1,1)
  }

  /**
   * Draw generated maze
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

  /**
   * Draw player with his image
   * @param direction direction to draw player
   */
  def drawPlayer(direction: Int): Unit = {
    val centerX = display.width / 2
    val centerY = display.height / 2
    val playerPicture = direction match {
      case 1 => image.playerTop
      case 2 => image.playerRight
      case 3 => image.playerDown
      case 4 => image.playerLeft
    }
    //Is player centered ?
    if(centerCamera){
      display.drawTransformedPicture(centerX, centerY, 0, maze.cellSize/32, playerPicture)
    }else{
      display.drawTransformedPicture(player.getPosX*maze.cellSize+offsetX + maze.cellSize/2, player.getPosY*maze.cellSize+offsetY + maze.cellSize/2, 0, maze.cellSize/32, playerPicture)
    }
  }

  /**
   * Get x coordinate to center object
   * @param x coordinate x
   * @return centered position
   */
  private def getXCoordWithOffset(x: Int): Int = {
    x*maze.cellSize+offsetX
  }

  /**
   * Get y coordinate to center object
   * @param y coordinate y
   * @return centered position
   */
  private def getYCoordWithOffset(y: Int): Int = {
    y*maze.cellSize+offsetY
  }

  /**
   * Draw each cell
   * @param x coord x of cell
   * @param y coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    val drawX = getXCoordWithOffset(x)
    val drawY = getYCoordWithOffset(y)
    val lightZone = cell.size*9

    //Verify if cell is in visible zone before drawing
    //Else draw maze without detail
    if (drawX >= lightZone && drawX <= display.width-lightZone && drawY >= lightZone && drawY <= display.height-lightZone) {

      //draw by default ground picture to have global background
      display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, image.lstGroundPictures.head)

      //Draw cell image
      display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, cell.image)

      //Draw torch if cell hasTorch
      if(cell.hasTorch){
        display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, image.torch)
      }

      //Draw path to exit if display path is true and cell is a path to exit
      if(maze.grid(x)(y).isPathToExit && displayPath){
        display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, image.path)
      }

      if(cell.hasTorch){
        display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, image.torch)
      }
    }else if (drawX >= 0 && drawX <= display.width && drawY >= 0 && drawY <= display.height) {
      display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, image.lstGroundPictures.head)
      //Draw cell image

      if(cell.image == image.keyPicture || cell.image == image.locked_door || cell.image.name == image.opened_door.name){
        display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, image.lstGroundPictures(randomGroundPicture))
      }else{
        display.drawTransformedPicture(drawX, drawY, 0, cell.size/32, cell.image)
      }

      display.setColor(new Color(0, 0, 0, 0.5f))
      display.drawFillRect(drawX-cell.size/2, drawY-cell.size/2, cell.size, cell.size)
    }

    //showDistanceFromExit(drawX, drawY, cell)
    //showAssignedNumber(drawX, drawY, cell)
  }

  /**
   * Display distance from exit in all cell
   * @param x position x of text
   * @param y position y of text
   * @param cell cell to draw distance from exit
   */
  private def showDistanceFromExit(x: Int, y: Int, cell: Cell): Unit = {
    if(!cell.isWall) {
      display.drawString(x, y, cell.distanceFromExit.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
    }
  }

  /**
   * Display assigned number in all cell
   * @param x position x of text
   * @param y position y of text
   * @param cell cell to show assigned number
   */
  private def showAssignedNumber(x: Int, y: Int, cell: Cell): Unit = {
    if(cell.isWall) {
      display.drawString(x, y, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(255,255,255), 1,1)
    }else{
      display.drawString(x, y, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
    }
  }
}
