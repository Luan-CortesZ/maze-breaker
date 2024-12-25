package src.game_class

import scala.collection.mutable
import scala.util.Random

class Maze(width: Int, height: Int, var cellSize: Int = 30) {
  val GRID_WIDTH: Int = width * cellSize // Width of the grid
  val GRID_HEIGHT: Int = height * cellSize // Heigth of the grid
  var entry: (Int, Int) = (0, 1) //Coord of the entry
  var exit: (Int, Int) = (width-1, height-2) //Coord of the exit
  val grid: Array[Array[Cell]] = Array.ofDim(width,height) //Maze

  generateMaze() // Generate maze
  solve()
  /**
   * Verify if specific cell of the maze is a wall or not
   * @param x Pos x of cell
   * @param y Pos y of cell
   * @return If cell is a wall
   */
  def isCellAWall(x: Int, y: Int): Boolean = {
    grid(x)(y).isWall
  }

  /**
   * Initialize cells of the grid
   */
  private def initializeCell(): Unit = {
    var nb: Int = 0;
    for (x <- grid.indices; y <- grid(x).indices) {
      grid(x)(y) = new Cell(cellSize, true) //wall cell by default
      if(x % 2 != 0 && y % 2 !=0){
        nb+=1;
        grid(x)(y).number = nb //Assigned value
        grid(x)(y).isWall = false //Path
      }
    }
  }

  private def initializeEntryAndExit(): Unit = {
    grid(entry._1)(entry._2).isWall = false
    grid(exit._1)(exit._2).isWall = false
  }

  /**
   * Function that generate maze
   */
  private def generateMaze(): Unit = {
    initializeCell()
    initializeEntryAndExit()

    //While maze generator not finished
    while (!isFinished){
      val (x,y) = getRandomWall()

      //Verify if coord is a wall
      if(isCellAWall(x,y)) {
        var cell_1: Cell = new Cell()
        var cell_2: Cell = new Cell()
        //Verify if left cell to this cell is not a wall
        //Else its up and down cell
        if (!isCellAWall(x-1,y)) {
          cell_1 = grid(x - 1)(y)
          cell_2 = grid(x + 1)(y)
        } else{
          cell_1 = grid(x)(y-1)
          cell_2 = grid(x)(y+1)
        }

        //Verify if two cell around the wall don't have the same number
        if (cell_1.number != cell_2.number) {
          grid(x)(y).isWall = false //Break the wall
          grid(x)(y).number = cell_1.number //Assign one of two cell number to this cell

          val targetNumber = cell_1.number //Keep targetNumber
          val sourceNumber = cell_2.number //sourceNumber to replace

          //browse all grid and replace ancient number by targetNumber
          for (i <- grid.indices; j <- grid(i).indices) {
            if (grid(i)(j).number == sourceNumber) {
              grid(i)(j).number = targetNumber
            }
          }
        }
      }
    }
    complexify()
  }

  /**
   * Verify that maze generation is complete
   * @return true if generation is complete
   */
  private def isFinished: Boolean = {
    val nb = grid(1)(1).number; //Get one arbitrary number

    //Go through the entire grid and check that all cells have the same number
    //If not, the generation is not complete.
    for(x <- 1 until grid.length by 2;
        y <- 1 until grid(x).length by 2){
      if(!isCellAWall(x,y) && grid(x)(y).number != nb){
        return false
      }
    }
    true
  }

  /**
   * Complexify maze
   */
  private def complexify(): Unit = {
    for(i <- 0 to width){
      val (x,y) = getRandomWall()
      grid(x)(y).isWall = false

    }
  }

  /**
   * Get random wall in the maze
   * @return coord of random wall
   */
  private def getRandomWall(): (Int, Int) = {
    val x = Random.nextInt(width-2)+1 // Get random x coord inside the grid (without border)
    var y = 0;
    // Get random y coord inside the grid (without border)
    if(x % 2 == 0){
      y = Random.nextInt((height-1)/2)*2+1
    }else{
      y = Random.nextInt((height-2)/2)*2+2
    }
    (x,y)
  }

  /**
   * Solve maze
   */
  private def solve() : Unit = {
    getDistanceFromExit()
    findPath()
  }

  /**
   * Find path to exit
   */
  private def findPath(): Unit = {
    grid(entry._1)(entry._2).isPathToExit = true
    var current = entry

    //While current cell is not exit cell
    while (current != exit) {
      val (x, y) = current //Get coord of current cell

      //Get neighbors of current cell
      val neighbors = Seq(
        (x - 1, y), // Haut
        (x + 1, y), // Bas
        (x, y - 1), // Gauche
        (x, y + 1)  // Droite
      )

      //Find the neighbor with the smallest distance
      val next = neighbors.filter {
          case (nx, ny) =>
          nx >= 0 && nx < width && ny >= 0 && ny < height && !isCellAWall(nx, ny)
        }
        .minBy { case (nx, ny) => grid(nx)(ny).distanceFromExit }

      //Set nextCell PathToExit
      grid(next._1)(next._2).isPathToExit = true
      current = next // Set next cell to current cell
    }
  }

  /**
   * Get distance from all cells to exit
   */
  private def getDistanceFromExit(): Unit = {
    //Create mutable queue to browse all cells
    val queue: mutable.Queue[(Int, Int, Int)] = mutable.Queue()
    grid(exit._1)(exit._2).distanceFromExit = 0 // Set exit cell with 0 distance
    queue.enqueue((exit._1, exit._2, 0)) // Enqueue the exit cell with 0 distance

    //While queue is not empty
    while (queue.nonEmpty) {
      //Dequeue cell and get his coord and distance
      val (x, y, distance) = queue.dequeue()

      // Go through cell neighbors
      val neighbors = Seq(
        (x - 1, y), // Up
        (x + 1, y), // Down
        (x, y - 1), // Left
        (x, y + 1)  // Right
      )

      //Verify if neighbors are inside the grid
      for ((nx, ny) <- neighbors; if nx >= 0 && nx < width && ny >= 0 && ny < height) {
        val neighborCell = grid(nx)(ny)

        //Verify if neighbor is not a wall and is not visited
        if (!neighborCell.isWall && neighborCell.distanceFromExit == -1) {
          neighborCell.distanceFromExit = distance + 1
          queue.enqueue((nx, ny, neighborCell.distanceFromExit))
        }
      }
    }
  }
}
