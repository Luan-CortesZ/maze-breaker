import hevs.graphics.FunGraphics
import src.{Cell, Maze}

import java.awt.{Color, Toolkit}

object Game extends App{
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  var maze: Maze = new Maze(50,25)
  var display = new FunGraphics(screenSize.width,screenSize.height, "Maze breaker")
  val offsetX: Int = (display.width - maze.GRID_WIDTH) / 2
  val offsetY: Int = (display.height - maze.GRID_HEIGHT) / 2
  Generate()
  
  def Generate() = {
    for(x <- maze.grid.indices;
        y <- maze.grid(x).indices){
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    if (cell.size < 0) return

    if(cell.isPath) {
      if ((x + y) % 2 == 0) {
        display.setColor(new Color(230, 230, 230))
      } else {
        display.setColor(new Color(205, 205, 205))
      }
    }else{
      if ((x + y) % 2 == 0) {
        display.setColor(new Color(50, 50, 50))
      } else {
        display.setColor(new Color(25, 25, 25))
      }
    }
    display.drawFillRect(x* maze.cellSize + offsetX,y* maze.cellSize + offsetY,cell.size,cell.size)
  }
}
