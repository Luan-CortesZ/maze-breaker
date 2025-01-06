package src.game_display

import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import src.game_class.{Cell, Exit, Maze}

import java.awt.{Color, Font}

class DisplayMaze(width: Int, height: Int, var maze: Maze = null, var displayPath: Boolean = false) {
  var display: FunGraphics = _
  var offsetX: Int = 0
  var offsetY: Int = 0
  val keyPicture = new GraphicsBitmap("/src/res/chest.png")
  val groundPicture = new GraphicsBitmap("/src/res/ground.png")
  val wallPicture = new GraphicsBitmap("/src/res/wall.png")
  val opened_door = new GraphicsBitmap("/src/res/opened_door.png")
  val locked_door = new GraphicsBitmap("/src/res/locked_door.png")
  val entry_door = new GraphicsBitmap("/src/res/entry_door.png")
  def showWindow(): Unit = {
    display = new FunGraphics(width,height, "Maze breaker")
    drawMaze()
  }
  
  /**
   * Draw maze generated
   */
  def drawMaze(): Unit = {
    offsetX = (display.width - maze.GRID_WIDTH) / 2
    offsetY = (display.height - maze.GRID_HEIGHT) / 2
    for(x <- maze.grid.indices;
        y <- maze.grid(x).indices){
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  /**
   * Draw each cell
   * @param x coord x of cell
   * @param y coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    // Specific color for specific cell
    val finalColor = {
      if (cell.isPathToExit && displayPath) new Color(0, 255, 0)
      else new Color(0,0,0)
    }

    display.setColor(finalColor)

    if (cell.isWall){
      display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 1, wallPicture)
    }else if (!cell.isWall){
      display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 1, groundPicture)

      if (cell.getClass.getSimpleName.equals("Exit") && cell.asInstanceOf[Exit].isLock){
        display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 1, locked_door)
      }else if (cell.getClass.getSimpleName.equals("Exit") && cell.asInstanceOf[Exit].isLock){
        display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 1, opened_door)
      }else if (cell.getClass.getSimpleName.equals("Entry")){
        display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 1, entry_door)
      }
    }

    if (cell.getClass.getSimpleName.equals("Key")){
      display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 1, keyPicture)
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
