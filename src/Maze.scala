package src

import hevs.graphics.FunGraphics

import scala.util.Random

class Maze(width: Int, height: Int) {
  var cellSize: Int = 50
  val GRID_WIDTH: Int = width * cellSize
  val GRID_HEIGHT: Int = height * cellSize
  val grid: Array[Array[Cell]] = Array.ofDim(width,height)

  initializeCell(cellSize)

  private def initializeCell(size: Int): Unit = {
    for(x <- grid.indices;
        y <- grid(x).indices){
      grid(x)(y) = new Cell(size, true)
    }
  }
}
