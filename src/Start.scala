package src

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Maze}
import src.game_display.StartScreen

import java.awt.{Color, Font, Toolkit}

object Start extends App{
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  var display: FunGraphics = null
  var maze: Maze = new Maze(25,25)
  var offsetX: Int = 0
  var offsetY: Int = 0
  val startScreen = new StartScreen("Maze Breaker")

  /**
   * Draw maze generated
   */
  def Generate(displayPath: Boolean = false): Unit = {
    display = new FunGraphics(screenSize.width,screenSize.height, "Maze breaker")
    offsetX = (display.width - maze.GRID_WIDTH) / 2
    offsetY = (display.height - maze.GRID_HEIGHT) / 2
    for(x <- maze.grid.indices;
        y <- maze.grid(x).indices){
      drawCell(x, y, maze.grid(x)(y), displayPath)
    }
  }

  /**
   * Draw each cell
   * @param x coord x of cell
   * @param y coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell, displayPath: Boolean = false): Unit = {
    if (cell.size < 0) return

    //If the cell is a wall, draw in black
    //Else in white
    if(cell.isWall) {
      //Alter pattern
      if ((x + y) % 2 == 0) {
        display.setColor(new Color(25, 25, 25))
      } else {
        display.setColor(new Color(10, 10, 10))
      }
    }else if(!cell.isWall){
      //Alter pattern
      if ((x + y) % 2 == 0) {
        display.setColor(new Color(230, 230, 230))
      } else {
        display.setColor(new Color(205, 205, 205))
      }

      if(cell.isPathToExit && displayPath){
        display.setColor(new Color(0,255,0))
      }
    }

    //Draw cell rect and center it in the window
    display.drawFillRect(x*maze.cellSize+offsetX,y*maze.cellSize+offsetY,cell.size,cell.size)

    /*
    //Show number assigned to cell
    if(cell.isWall) {
      display.drawString(x*maze.cellSize+offsetX,y*maze.cellSize+offsetY, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(255,255,255), 1,1)
    }else{
      display.drawString(x*maze.cellSize+offsetX,y*maze.cellSize+offsetY, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
    }

    //Show distance from exit
    if(!cell.isWall) {
      display.drawString(x*maze.cellSize+offsetX,y*maze.cellSize+offsetY, cell.distanceFromExit.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
    }*/
  }
}
