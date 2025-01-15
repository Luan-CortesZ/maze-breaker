package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player}
import hevs.graphics.utils.GraphicsBitmap
import src.fonts.CustomFont

import java.awt.Color
import scala.util.Random
import java.awt.font.FontRenderContext
import java.awt.Font
import src.game_class.Question

/**
 * Display maze window
 * @param display fungraphics window
 * @param maze maze generated
 * @param displayPath displayPath or not
 * @param centerCamera centerCamera on player or not
 */
class DisplayMaze(var display: FunGraphics, var player: Player, var maze: Maze = null, var questions: Array[Question], private var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  private var offsetX: Int = 0 //OffsetX to center maze
  private var offsetY: Int = 0 //OffsetY to center maze
  private val image: Image = new Image() //Bank of image
  private val randomGroundPicture = Random.nextInt(image.lstGroundPictures.length) // Get random ground picture
  private val centerX = display.width / 2 //Center of window
  private val centerY = display.height / 2 //Center of window
  private val defaultFont = new Font("SansSerif", Font.PLAIN, 24)
  // Size of the light zone in cells (example: 3x3 or 4x4 cells)
  private var lightZoneRadius = 1 // Number of cells around the player (radius)
  private var showDistanceCell: Boolean = false
  private var showNumberCell: Boolean = false

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
      if (maze.grid(x)(y).isWall) {
        cellImage = image.wallPicture //Wall picture
      } else if (!maze.grid(x)(y).isWall) {
        cellImage = image.lstGroundPictures(Random.nextInt(image.lstGroundPictures.length)) //Random ground pictures
        if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && maze.grid(x)(y).asInstanceOf[Exit].isLock) {
          cellImage = image.locked_door //Exit door locked
        } else if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && !maze.grid(x)(y).asInstanceOf[Exit].isLock) {
          cellImage = image.opened_door //Exit door opened
        } else if (maze.grid(x)(y).getClass.getSimpleName.equals("Entry")) {
          cellImage = image.entry_door //Entry door
        } else if (maze.grid(x)(y).getClass.getSimpleName.equals("Key")) {
          cellImage = image.keyPicture //Key picture
        } else if (maze.grid(x)(y).getClass.getSimpleName.equals("EventQuestions")) {
          cellImage = image.eventPicture //Event picture
        }
      }
      if (maze.grid(x)(y).isWall && maze.isValidCell(x, y) && Random.nextInt(5) == 1) {
        maze.grid(x)(y).hasTorch = true //Random wall has torch or not
      }
      maze.grid(x)(y).setImage(cellImage) //Set cell image
    }
  }

  /**
   * Show message
   */
  def showNotif(doorLockedMessage: Boolean): Unit = {
    if (doorLockedMessage) {
      drawText("The door is locked...")
    }
  }

  /**
   * Draw textbox with message
   *
   * @param text text to display
   */
  private def drawText(text: String): Unit = {
    display.setColor(Color.WHITE)
    val font = new Font(Font.SANS_SERIF, Font.BOLD, 16)
    val fontRenderContext = new FontRenderContext(null, true, true)
    val fontMetrics = font.getLineMetrics(text, fontRenderContext)
    val textHeight = fontMetrics.getAscent.toInt
    val textWidth = font.getStringBounds(text, fontRenderContext).getWidth.toInt
    val rectWidth = textWidth + 10
    val rectHeight = textHeight + 10
    val posX = getXCoordWithOffset(player.getPosX) - rectWidth / 2 + maze.cellSize / 2
    val posY = getYCoordWithOffset(player.getPosY) - rectHeight
    val textX: Int = posX + (rectWidth - textWidth) / 2
    display.drawString(textX, posY, text, font, new Color(0, 0, 0), 1, 1)
  }

  /**
   * Display in which level user is
   * @param mazeLevel user level
   */
  def displayLevel(mazeLevel: String): Unit = {
    val title = "LEVEL "+mazeLevel
    display.setColor(Color.WHITE)
    val font = new CustomFont().upheaval.deriveFont(Font.PLAIN, 64)
    val fontRenderContext = new FontRenderContext(null, true, true)
    val fontMetrics = font.getLineMetrics(title, fontRenderContext)
    val textHeight = fontMetrics.getAscent.toInt
    val textWidth = font.getStringBounds(title, fontRenderContext).getWidth.toInt
    val rectWidth = textWidth + 10
    val rectHeight = textHeight + 10
    val posX = getXCoordWithOffset(player.getPosX) - rectWidth / 2 + maze.cellSize / 2
    val posY = getYCoordWithOffset(player.getPosY) - rectHeight
    val textX: Int = posX + (rectWidth - textWidth) / 2
    display.drawFillRect(posX, maze.height+50, rectWidth, rectHeight)
    display.drawString(textX, maze.height+50, title, font, new Color(0, 0, 0), 1, 1)
  }

  /**
   * Draw generated maze
   */
  def drawMaze(): Unit = {
    offsetX = (display.width - maze.GRID_WIDTH) / 2
    offsetY = (display.height - maze.GRID_HEIGHT) / 2
    if (centerCamera) {
      // Calculate offsets dynamically to focus on the player
      offsetX = centerX - player.getPosX * maze.cellSize
      offsetY = centerY - player.getPosY * maze.cellSize
    }

    // Draw visible cells
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  /**
   * Draw player with his image
   *
   * @param direction direction to draw player
   */
  def drawPlayer(direction: Int): Unit = {
    val playerPicture = direction match {
      case 1 => image.playerTop
      case 2 => image.playerRight
      case 3 => image.playerDown
      case 4 => image.playerLeft
    }
    //Is player centered ?
    if (centerCamera) {
      display.drawTransformedPicture(centerX, centerY, 0, maze.cellSize / 32, playerPicture)
    } else {
      display.drawTransformedPicture(player.getPosX * maze.cellSize + offsetX + maze.cellSize / 2, player.getPosY * maze.cellSize + offsetY + maze.cellSize / 2, 0, maze.cellSize / 32, playerPicture)
    }
  }

  /**
   * Funtion used to create the label for the answer and centered the text
   * @param posX            X position of the label
   * @param posY            Y position of the label
   * @param content         Content of the label
   * @param height          Height of the label
   * @param display         The display where the label will be created
   * @param font            The font used for the label
   * @param backgroundColor The background for the label in the window
   */
  private def drawTextBox(posX: Int, posY: Int, content: String, height: Int, display: FunGraphics, font: Font = defaultFont, backgroundColor: Color = Color.WHITE): Unit = {
    var stringSize = display.getStringSize(content, defaultFont).getWidth.floor.toInt + 40

    var btnAnswer: Button = new Button(posX, posY, content, stringSize, height, display)
    btnAnswer.displayButton(Color.WHITE, Color.BLACK, 20f)
  }

  /**
   * Draw the question and the answer
   * @param idQuestion  The id of the question, used to know the answer and display the question
   * @param content     Content of the TextBox
   */
  def drawQuestionAnswer(idQuestion: Int, content: String): Unit = {
    // Create a button like a label to display the question
    var btnQuestion: Button = new Button(20, 50, questions(idQuestion).questionShowed, display.getStringSize(questions(idQuestion).questionShowed, defaultFont).getWidth.floor.toInt + 40, 30, display)
    btnQuestion.displayButton(Color.WHITE, Color.BLACK, 20f)

    // Draw the label where the question is showed
    drawTextBox(20, 100, content, 30, display)
  }

  /**
   * Display/hide the shortest path to user
   */
  def displaySolutionPath(): Unit = {
    displayPath = !displayPath
  }

  /**
   * Chose to display or not the shortest path to user
   */
  def displaySolutionPath(display: Boolean): Unit = {
    displayPath = display
  }

  /**
   * Show all maze
   */
  def displayMaze(show: Boolean): Unit = {
    if(show){
      lightZoneRadius = 10000
    }else{
      lightZoneRadius = 1
    }
  }

  /**
   * Display maze with value chosen by user
   */
  def displayMaze(value: Int): Unit = {
    lightZoneRadius = value
  }

  /**
   * Get x coordinate to center object
   *
   * @param x coordinate x
   * @return centered position
   */
  private def getXCoordWithOffset(x: Int): Int = {
    x * maze.cellSize + offsetX
  }

  /**
   * Get y coordinate to center object
   *
   * @param y coordinate y
   * @return centered position
   */
  private def getYCoordWithOffset(y: Int): Int = {
    y * maze.cellSize + offsetY
  }

  /**
   * Draw each cell
   *
   * @param x    coord x of cell
   * @param y    coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    val drawX = getXCoordWithOffset(x)
    val drawY = getYCoordWithOffset(y)

    // Limits in pixels based on the size of the cells
    val lightZonePixel = lightZoneRadius * cell.size

    // Check if the cell is in the visible zone
    if (drawX >= centerX - lightZonePixel &&
      drawX <= centerX + lightZonePixel &&
      drawY >= centerY - lightZonePixel &&
      drawY <= centerY + lightZonePixel) {

      // Draw default ground picture for global background
      display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, image.lstGroundPictures.head)

      // Draw the cell image
      display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, cell.image)

      // Draw the torch if the cell contains one
      if (cell.hasTorch) {
        display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, image.torch)
      }

      // Draw the path to the exit if enabled
      if (maze.grid(x)(y).isPathToExit && displayPath) {
        display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, image.path)
      }

    } else if (drawX >= 0 && drawX <= display.width && drawY >= 0 && drawY <= display.height) {
      // Outside the light zone, draw without details
      display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, image.lstGroundPictures.head)
      if (cell.image == image.keyPicture || cell.image == image.locked_door || cell.image.name == image.opened_door.name || cell.image.name == image.eventPicture.name) {
        display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, image.lstGroundPictures(randomGroundPicture))
      } else {
        display.drawTransformedPicture(drawX, drawY, 0, cell.size / 32, cell.image)
      }

      // Apply a shadow to hide details
      display.setColor(new Color(0, 0, 0, 0.7f))
      display.drawFillRect(drawX - cell.size / 2, drawY - cell.size / 2, cell.size, cell.size)
    }

    if(showNumberCell){
      showAssignedNumber(drawX, drawY, cell)
    }else if(showDistanceCell){
      showDistanceFromExit(drawX, drawY, cell)
    }
  }

  /**
   * Display distance from exit in all cell
   *
   * @param x    position x of text
   * @param y    position y of text
   * @param cell cell to draw distance from exit
   */
  private def showDistanceFromExit(x: Int, y: Int, cell: Cell): Unit = {
    if (!cell.isWall) {
      display.drawString(x, y, cell.distanceFromExit.toString, new Font("Sans Serif", 0, 15), new Color(255, 255, 255), 1, 1)
    }
  }

  /**
   * Display or not number cell
   */
  def displayAssignedNumber(): Unit = {
    showNumberCell = !showNumberCell
    showDistanceCell = false
  }

  /**
   * Display assigned number in all cell
   *
   * @param x    position x of text
   * @param y    position y of text
   * @param cell cell to show assigned number
   */
  private def showAssignedNumber(x: Int, y: Int, cell: Cell): Unit = {
    display.drawString(x, y, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(255, 255, 255), 1, 1)
  }

  /**
   * Display or not distance cell
   */
  def displayDistanceCell(): Unit = {
    showDistanceCell = !showDistanceCell
    showNumberCell = false
  }
}
