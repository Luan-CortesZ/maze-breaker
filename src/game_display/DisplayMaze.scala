package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player}
import hevs.graphics.utils.GraphicsBitmap

import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.Color
import scala.util.Random

class DisplayMaze(var display: FunGraphics, var maze: Maze = null, var displayPath: Boolean = false, var centerCamera: Boolean = false) {
  private var offsetX: Int = 0
  private var offsetY: Int = 0
  val image: Image = new Image()
  private var player = new Player(0, 1)
  private var playerDirection = 1;


  def showWindow(): Unit = {
    player = new Player(maze.entry._1, maze.entry._2)
    initializeCellImage()
    addMovemement()
  }

  private def initializeCellImage(): Unit = {
    // Dessiner les cellules visibles
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      var cellImage: GraphicsBitmap = null
      if (maze.grid(x)(y).isWall){
        cellImage = image.wallPicture
      }else if (!maze.grid(x)(y).isWall){
        cellImage = image.lstGroundPictures(Random.nextInt(image.lstGroundPictures.length))
        if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && maze.grid(x)(y).asInstanceOf[Exit].isLock){
          cellImage = image.locked_door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Exit") && !maze.grid(x)(y).asInstanceOf[Exit].isLock){
          cellImage = image.opened_door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Entry")){
          cellImage = image.entry_door
        }else if (maze.grid(x)(y).getClass.getSimpleName.equals("Key")){
          cellImage = image.keyPicture
        }
      }
      maze.grid(x)(y).setImage(cellImage)
    }
  }

  private def addMovemement(): Unit = {
    display.setKeyManager(new KeyAdapter() {
      override def keyPressed(e: KeyEvent): Unit = {
        if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
          if (!maze.isCellAWall(player.getPosX, player.getPosY - 1)){
            player.move(0,-1)
            playerDirection = 1
          }
        } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
          if(!maze.isCellAWall(player.getPosX, player.getPosY + 1)){
            player.move(0, +1)
            playerDirection = 3
          }
        } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
          if (!maze.isCellAWall(player.getPosX + 1, player.getPosY)){
            player.move(+1, 0)
            playerDirection = 2
          }
        } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
          if (!maze.isCellAWall(player.getPosX - 1, player.getPosY)){
            player.move(-1, 0)
            playerDirection = 4
          }
        }
        maze.openExitIfPlayerOnKey(player.posX, player.posY)
    }})

    while (true) {
      // Drawing
      display.frontBuffer.synchronized{
        display.clear(Color.black)
        drawMaze()
        drawPlayer(playerDirection)
      }

      // FPS sync
      display.syncGameLogic(60)
    }

  }

  /**
   * Draw maze generated
   */
  private def drawMaze(): Unit = {
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

  private def drawPlayer(direction: Int): Unit = {
    // Dessiner le joueur au centre de la fenêtre
    val centerX = display.width / 2
    val centerY = display.height / 2
    val playerPicture = direction match {
      case 1 => image.playerTop
      case 2 => image.playerRight
      case 3 => image.playerDown
      case 4 => image.playerLeft
    }
    if(centerCamera){
      display.drawTransformedPicture(centerX + maze.cellSize/2, centerY + maze.cellSize/2, 0, maze.cellSize/32, playerPicture)
    }else{
      display.drawTransformedPicture(player.getPosX*maze.cellSize+offsetX + maze.cellSize/2, player.getPosY*maze.cellSize+offsetY + maze.cellSize/2, 0, maze.cellSize/32, playerPicture)
    }
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

      display.drawTransformedPicture(drawX + cell.size/2, drawY + cell.size/2, 0, cell.size/32, image.lstGroundPictures.head)
      display.drawTransformedPicture(drawX + cell.size/2, drawY + cell.size/2, 0, cell.size/32, cell.image)
      display.setColor(finalColor)
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
