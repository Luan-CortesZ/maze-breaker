package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player}
import hevs.graphics.utils.GraphicsBitmap

import java.awt.event.{KeyAdapter, KeyEvent}
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
  var player = new Player(0, 1)

  def showWindow(): Unit = {
    display = new FunGraphics(width,height, "Maze breaker")
    player = new Player(maze.entry._1, maze.entry._2)
    addMovemement()
  }

  def addMovemement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
          player.move(0,-1)
        } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
          player.move(0, +1)
        } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
          player.move(+1, 0)
        } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
          player.move(-1, 0)
        }
        maze.openExitIfPlayerOnKey(player.posX, player.posY)
    }})

    while (true) {
      // Drawing
      display.frontBuffer.synchronized{
        display.clear(Color.black)
        drawMaze()
        drawPlayer()
      }

      // FPS sync
      display.syncGameLogic(60)
    }
  }

  /**
   * Draw maze generated
   */
  def drawMaze(): Unit = {
    // Calculer les offsets dynamiquement pour centrer la vue sur le joueur
    offsetX = display.width / 2 - player.getPosX() * maze.cellSize
    offsetY = display.height / 2 - player.getPosY() * maze.cellSize

    // Dessiner les cellules visibles
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  def drawPlayer(): Unit = {
    // Dessiner le joueur au centre de la fenêtre
    display.setColor(Color.RED)
    val centerX = display.width / 2
    val centerY = display.height / 2
    display.drawFilledCircle(centerX, centerY, maze.cellSize)
  }

  /**
   * Draw each cell
   * @param x coord x of cell
   * @param y coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    // Calculer les coordonnées de la cellule avec les offsets
    val drawX = x * cell.size + offsetX
    val drawY = y * cell.size + offsetY

    // Vérifier si la cellule est dans la zone visible avant de la dessiner
    if (drawX + cell.size >= 0 && drawX <= display.width && drawY + cell.size >= 0 && drawY <= display.height) {
      val finalColor = {
        if (cell.isPathToExit && displayPath) new Color(0, 255, 0)
        else new Color(0,0,0)
      }

      display.setColor(finalColor)

      if (cell.isWall){
        display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 4, wallPicture)
      }else if (!cell.isWall){
        display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 4, groundPicture)

        if (cell.getClass.getSimpleName.equals("Exit") && cell.asInstanceOf[Exit].isLock){
          display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 4, locked_door)
        }else if (cell.getClass.getSimpleName.equals("Exit") && cell.asInstanceOf[Exit].isLock){
          display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 4, opened_door)
        }else if (cell.getClass.getSimpleName.equals("Entry")){
          display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 4, entry_door)
        }else if (cell.getClass.getSimpleName.equals("Key")){
          display.drawTransformedPicture(x * cell.size + offsetX + cell.size/2, y * cell.size + offsetY + cell.size/2, 0, 4, keyPicture)
        }
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
}
