package src.game_class

import src.Main.level
import src.game_display.Image

import scala.collection.mutable
import scala.util.Random

/**
 * Maze class to generate
 * @param width Width of maze
 * @param height Height of maze
 * @param cellSize Cell's size
 */
class Maze(var width: Int, var height: Int, var cellSize: Int = 30) {
  //Prevent user to create even maze
  if(width % 2 == 0 || height % 2 == 0){
    Console.err.println("the labyrinth must have an odd length and width")
    sys.exit()
  }
  val image: Image = new Image() //Get all image
  val GRID_WIDTH: Int = width * cellSize // Width of the grid
  val GRID_HEIGHT: Int = height * cellSize // Heigth of the grid
  var entry: (Int, Int) = (0, 1) //Coord of the entry
  var exit: (Int, Int) = (width-1, height-2) //Coord of the exit
  val grid: Array[Array[Cell]] = Array.ofDim(width,height) //Initialize array to create maze

  generateMaze() //Generate maze
  solve() //Solve maze to have path from the entry to the exit

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
   * Check if cell is inside the maze
   * @param x coord x of cell
   * @param y coord y of cell
   * @return true if cell position is valid
   *         false if not
   */
  def isValidCell(x: Int, y: Int): Boolean = {
    x >= 0 && x < width && y >= 0 && y < height
  }

  /**
   * Verify if cell is exit cell or not
   * @param x coordinate x of cell
   * @param y coordinate y of cell
   * @return true if it's an exit cell
   *         false if not
   */
  def isCellExit(x: Int, y: Int): Boolean = {
    grid(x)(y).getClass.getSimpleName.equals("Exit")
  }

  /**
   * Verify if cell is entry cell or not
   * @param x coordinate x of cell
   * @param y coordinate y of cell
   * @return true if it's an entry cell
   *         false if not
   */
  private def isCellEntry(x: Int, y: Int): Boolean = {
    grid(x)(y).getClass.getSimpleName.equals("Entry")
  }

  /**
   * Verify if exit cell is lock
   * @return true if yes
   *         false if not
   */
  def isExitLock: Boolean = {
    grid(exit._1)(exit._2).asInstanceOf[Exit].isLock
  }

  /**
   * Verify if cell is surrounded by wall
   * @param x coordinate x of cell
   * @param y coordinate y of cell
   * @return true if it's the case
   *         false if not
   */
  private def isSurroundedByWalls(x: Int, y: Int): Boolean = {
    //Initialize list of direction to verify around the cell
    val directions = List(
      (0, -1), // Haut
      (0, 1), // Bas
      (-1, 0), // Gauche
      (1, 0) // Droite
    )

    //Count cell's that are walls
    directions.count { case (dx, dy) =>
      val nx = x + dx
      val ny = y + dy
      isValidCell(nx, ny) && isCellAWall(nx, ny)
    } == 3
  }

  /**
   * Verify that maze generation is complete
   * @return true if generation is complete
   */
  private def isMazeGenerationFinished: Boolean = {
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
   * Verify if cell is exit or entry cell or not
   * @param x coordinate x of cell
   * @param y coordinate y of cell
   * @return true if it's an exit or entry cell
   *         false if not
   */
  private def isCellEntryOrExit(x: Int, y: Int): Boolean = {
    isCellExit(x,y) || isCellEntry(x,y)
  }

  /**
   * Verify if exit cell is far enough the entry
   * @return true if it's the case
   *         false if not
   */
  private def isExitFarEnough: Boolean = {
    resetDistance() // Reset cell distance value
    getDistanceFromExit() //Set distance to cell from entry to exit
    //Verify if exit cell distance is far enough
    grid(entry._1)(entry._2).distanceFromExit >= grid.flatten.maxBy(_.distanceFromExit).distanceFromExit/2
  }

  /**
   * Initialize cells of the grid
   */
  private def initializeCells(): Unit = {
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

  /**
   * Initialize a simple Maze with no entry and exit
   * and no objects
   */
  private def initializeMaze(): Unit = {
    //While maze generator not finished
    while (!isMazeGenerationFinished){
      val (x,y) = getRandomWall

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
  }

  /**
   * Function that generate maze
   * Create entry and exit
   * Create objects
   */
  private def generateMaze(): Unit = {
    initializeCells()
    initializeMaze()
    complexMaze()
    initializeEntryAndExit()
    createKey()
    for(i <- 0 to level*6){
      createEventQuestions()
    }
  }

  /**
   * Create key in random cell inside the maze
   */
  private def createKey(): Unit = {
    val cell: (Int,Int) = getRandomCell
    val keyCell = new Key()
    keyCell.distanceFromExit = grid(cell._1)(cell._2).distanceFromExit
    keyCell.number = grid(cell._1)(cell._2).number
    keyCell.size = grid(cell._1)(cell._2).size
    keyCell.isPathToExit = grid(cell._1)(cell._2).isPathToExit
    keyCell.isWall = false
    grid(cell._1)(cell._2) = keyCell
  }

  /**
   * Create event in random cell inside the maze
   */
  private def createEventQuestions(): Unit = {
    val cell: (Int,Int) = getRandomCell
    val eventQuestions = new EventQuestions()
    eventQuestions.distanceFromExit = grid(cell._1)(cell._2).distanceFromExit
    eventQuestions.number = grid(cell._1)(cell._2).number
    eventQuestions.size = grid(cell._1)(cell._2).size
    eventQuestions.isPathToExit = grid(cell._1)(cell._2).isPathToExit
    eventQuestions.isWall = false
    grid(cell._1)(cell._2) = eventQuestions
  }

  /**
   * Open exit if player get key
   * @param x position x of player
   * @param y position y of player
   */
  def openExitIfPlayerOnKey(x: Int, y: Int): Unit = {
    //If player is on key cell
    if(grid(x)(y).getClass.getSimpleName.equals("Key")){
      grid(exit._1)(exit._2).asInstanceOf[Exit].unLock() //unlock exit
      //Transform key cell to normal cell
      grid(x)(y) = new Cell(grid(x)(y).size,grid(x)(y).isWall,grid(x)(y).number,grid(x)(y).distanceFromExit,grid(x)(y).isPathToExit)
      grid(x)(y).setImage(image.lstGroundPictures.head)
    }
  }

   /**
   * Show question if player is on a event case
   * @param x position x of player
   * @param y position y of player
   */
  def triggerQuestionIfPlayerOnEvent(x: Int, y: Int): Unit = {
    //If player is on key cell
    if(grid(x)(y).getClass.getSimpleName.equals("EventQuestions")){
      grid(x)(y) = new Cell(grid(x)(y).size, grid(x)(y).isWall, grid(x)(y).number, grid(x)(y).distanceFromExit, grid(x)(y).isPathToExit)
      grid(x)(y).setImage(image.lstGroundPictures.head)
    }
  }

  /**
   * Complex maze
   */
  private def complexMaze(): Unit = {
    //transform some walls to path
    for(i <- 0 to width){
      val (x,y) = getRandomWall
      grid(x)(y).isWall = false
    }
  }

  /**
   * Get random cell that's not a wall or an entry or exit inside the maze
   * @return coordinate x and y of cell
   */
  def getRandomCell: (Int, Int) = {
    var cell: (Int,Int) = (0,0)
    do{
      cell = (Random.nextInt(width-2)+1, Random.nextInt(height-2)+1)
    }while(isCellAWall(cell._1,cell._2) || isCellEntryOrExit(cell._1, cell._2))
    cell
  }

  /**
   * Get random wall in the maze
   * @return coord of random wall
   */
  private def getRandomWall: (Int, Int) = {
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
   * Initialize random entry and exit
   */
  private def initializeEntryAndExit(): Unit = {
    createRandomEntry()
    val entryCell = new Entry()
    entryCell.distanceFromExit = grid(entry._1)(entry._2).distanceFromExit
    entryCell.number = grid(entry._1)(entry._2).number
    entryCell.size = grid(entry._1)(entry._2).size
    entryCell.isPathToExit = grid(entry._1)(entry._2).isPathToExit
    grid(entry._1)(entry._2) = entryCell
    grid(entry._1)(entry._2).isWall = false

    do{
      createRandomExit()
    }while(!isExitFarEnough)

    val exitCell = new Exit()
    exitCell.distanceFromExit = grid(exit._1)(exit._2).distanceFromExit
    exitCell.number = grid(exit._1)(exit._2).number
    exitCell.isPathToExit = grid(exit._1)(exit._2).isPathToExit
    exitCell.size = grid(exit._1)(exit._2).size
    grid(exit._1)(exit._2) = exitCell
    grid(exit._1)(exit._2).isWall = false
  }

  /**
   * Create random exit inside de maze
   */
  private def createRandomExit(): Unit = {
    //Create random exit inside the maze
    do {
      exit = (Random.nextInt(width - 2) + 1, Random.nextInt(height - 2) + 1)
    } while (!isSurroundedByWalls(exit._1, exit._2) || isCellAWall(exit._1, exit._2))
  }

  /**
   * Create random entry at border of maze
   */
  private def createRandomEntry(): Unit = {
    Random.nextInt(4) match {
      case 0 => // Random entry in left section
        do{
          entry = (0, Random.nextInt(height))
        }while(grid(entry._1+1)(entry._2).isWall)
      case 1 => // Random entry in Up section
        do{
          entry = (Random.nextInt(width), 0)
        }while(grid(entry._1)(entry._2+1).isWall)
      case 2 => // Random entry in right section
        do{
          entry = (width-1, Random.nextInt(height))
        }while(grid(entry._1-1)(entry._2).isWall)
      case 3 => // Random entry in down section
        do{
          entry = (Random.nextInt(width), height-1)
        }while(isCellAWall(entry._1,entry._2-1))
    }
  }

  /**
   * reset cell's distance attribute
   */
  private def resetDistance(): Unit = {
    for(x <- grid.indices;
        y <- grid(x).indices){
      grid(x)(y).distanceFromExit = -1
    }
  }

  /**
   * Solve maze
   */
  private def solve() : Unit = {
    getDistanceFromExit()
    //Find the shortest path to resolve maze
    findShortestPath(entry._1, entry._2)
  }

  /**
   * reset path to exit attribute to all cell
   */
  private def resetPathToExit(): Unit = {
    for(x <- grid.indices;
        y <- grid(x).indices){
      grid(x)(y).isPathToExit = false
    }
  }

  /**
   * Find path to exit
   */
  def findShortestPath(x: Int = 0, y: Int = 0): Unit = {
    resetPathToExit()
    var current = (x,y) //Position x and y at current cell

    //While current cell is not equal to exit cell
    while (current != exit) {
      val (x, y) = current //Get coordinate of current cell

      //Get neighbors of current cell
      val neighbors = Seq(
        (x - 1, y), // Haut
        (x + 1, y), // Bas
        (x, y - 1), // Gauche
        (x, y + 1)  // Droite
      )

      //Find the neighbor with the smallest distance
      val next = neighbors.filter {
          case (nx, ny) => isValidCell(nx,ny) && !isCellAWall(nx, ny)
        }
        .minBy { case (nx, ny) => grid(nx)(ny).distanceFromExit }

      //Set nextCell PathToExit
      grid(next._1)(next._2).isPathToExit = true
      current = next // Set next cell to current cell
    }

    grid(exit._1)(exit._2).isPathToExit = false
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
      for ((nx, ny) <- neighbors; if isValidCell(nx,ny)) {
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
