package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Maze}

import java.awt.{Color, Font}

class DisplayMaze(width: Int, height: Int, var maze: Maze = null, var displayPath: Boolean = false) {
  var display: FunGraphics = _
  var offsetX: Int = 0
  var offsetY: Int = 0

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
    // Base color if cell is a wall or not and alter pattern
    val baseColor = if (cell.isWall) {
      if ((x + y) % 2 == 0) new Color(25, 25, 25) else new Color(10, 10, 10)
    } else {
      if ((x + y) % 2 == 0) new Color(230, 230, 230) else new Color(205, 205, 205)
    }

    // Specific color for specific cell
    val finalColor = if (!cell.isWall) {
      if (cell.isExit) new Color(0, 255, 255)
      else if (cell.isEntry) new Color(255, 255, 0)
      else if (cell.isPathToExit && displayPath) new Color(0, 255, 0)
      else baseColor
    } else baseColor

    display.setColor(finalColor)

    // Draw cells
    display.drawFillRect(x * cell.size + offsetX, y * cell.size + offsetY, cell.size, cell.size)


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
