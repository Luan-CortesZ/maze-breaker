package src.game_display

import hevs.graphics.FunGraphics
import src.game_class.{Cell, Exit, Maze, Player}

import java.awt.event.{KeyAdapter, KeyEvent}
import java.awt.{Color, Font}
/*
  J'ai modifié ta façon de déplacer le joueur, ta manière de penser était bonne mais
  je pense pas qu'on ai besoin de boucle, sachant que ça update le dessin indéfiniment et que
  ça crée des clignotements. Je n'arrivais pas à déplacer le joueur quand il y avait la boucle aussi

  J'ai également réorganiser les fichiers du projet pour qu'on s'y retrouve mieux
  Le fichier Game -> Main
  pour dessiner le labyrinthe j'ai déplacé tout dans ce fichier donc c'est ici qu'on gère les dessins


  J'ai gardé ton code en commentaire mais j'ai crée plusieurs méthodes pour que le déplacement se fasse correctement dans le labyrinthe

  à toi d'implémenter lorsque le joueur croise un mur ou lorsqu'il sort du labyrinthe

  Si t'as des questions hésite pas
   */
class DisplayMaze(width: Int, height: Int, var maze: Maze = null, var displayPath: Boolean = false) {
  var display: FunGraphics = _
  var offsetX: Int = 0
  var offsetY: Int = 0
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
        display.clear(Color.white)
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
    offsetX = (display.width - maze.GRID_WIDTH) / 2
    offsetY = (display.height - maze.GRID_HEIGHT) / 2
    for(x <- maze.grid.indices;
        y <- maze.grid(x).indices){
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  def drawPlayer(): Unit = {
    // Création du curseur
    display.setColor(Color.RED)
    display.drawFilledCircle(player.getPosX()*maze.cellSize+offsetX, player.getPosY()*maze.cellSize+offsetY, maze.cellSize)
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
      if (cell.getClass.getSimpleName.equals("Exit") && cell.asInstanceOf[Exit].isLock) new Color(255, 0, 0)
      else if(cell.getClass.getSimpleName.equals("Exit") && !cell.asInstanceOf[Exit].isLock) new Color(0, 125, 0)
      else if (cell.getClass.getSimpleName.equals("Entry")) new Color(0, 255, 255)
      else if (cell.isPathToExit && displayPath) new Color(0, 255, 0)
      else if (cell.getClass.getSimpleName.equals("Key")) new Color(255,255,0)
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
