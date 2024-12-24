package src

import hevs.graphics.FunGraphics
import hevs.graphics.samples.TestDrawString.fg.clear

import java.awt.{Color, Toolkit}
import java.awt.event.{KeyAdapter, KeyEvent, KeyListener}

object Game extends App {
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  var maze: Maze = new Maze(19, 19)
  // var display = new FunGraphics(screenSize.width,screenSize.height, "Maze breaker")
  var display = new FunGraphics(800, 600, "Maze breaker")
  val offsetX: Int = (display.width - maze.GRID_WIDTH) / 2
  val offsetY: Int = (display.height - maze.GRID_HEIGHT) / 2

  Generate()
  val player = new Player(0, 1)
  display.drawForeground()
  display.setColor(Color.RED)
  display.drawFilledCircle(player.getPosX(), player.getPosY(), 10)


  display.setKeyManager(new KeyAdapter() {
    override def keyPressed(e: KeyEvent): Unit = {
      if (e.getKeyCode == KeyEvent.VK_UP || e.getKeyChar == 'w') {
        player.move(player.getPosX(), player.getPosY() + 1)
        // yoffsetY -= 1
        println("flèche du haut press")
      } else if (e.getKeyCode == KeyEvent.VK_DOWN || e.getKeyChar == 's') {
        player.move(player.getPosX(), player.getPosY() - 1)
      } else if (e.getKeyCode == KeyEvent.VK_RIGHT || e.getKeyChar == 'd') {
        // xOffsetX += 1
        player.move(player.getPosX() + 1, player.getPosY())
      } else if (e.getKeyCode == KeyEvent.VK_LEFT || e.getKeyChar == 'a') {
        player.move(player.getPosX() - 1, player.getPosY())
      }
    }
  })

//  while (true) {
//    //draw our object
//    var cursor = display.drawRect(player.getPosX(), player.getPosY(), 25, 25)
//    //refresh the screen at 120 FPS
//    cursor = display.syncGameLogic(120)
//    // cursor.clear()
//    cursor
//  }

  /**
   * Draw maze generated
   */
  private def Generate(): Unit = {
    for (x <- maze.grid.indices;
         y <- maze.grid(x).indices) {
      drawCell(x, y, maze.grid(x)(y))
    }
  }

  /**
   * Draw each cell
   *
   * @param x    coord x of cell
   * @param y    coord y of cell
   * @param cell cell to draw
   */
  private def drawCell(x: Int, y: Int, cell: Cell): Unit = {
    if (cell.size < 0) return

    //If the cell is a wall, draw in black
    //Else in white
    if (cell.isWall) {
      //Alter pattern
      if ((x + y) % 2 == 0) {
        display.setColor(new Color(25, 25, 25))
      } else {
        display.setColor(new Color(10, 10, 10))
      }
    } else {
      //Alter pattern
      if ((x + y) % 2 == 0) {
        display.setColor(new Color(230, 230, 230))
      } else {
        display.setColor(new Color(205, 205, 205))
      }

    }

    //Draw cell rect and center it in the window
    display.drawFillRect(x * maze.cellSize + offsetX, y * maze.cellSize + offsetY, cell.size, cell.size)

    /*if(cell.isWall) {
      display.drawString(x*maze.cellSize+offsetX,y*maze.cellSize+offsetY, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(255,255,255), 1,1)
    }else{
      display.drawString(x*maze.cellSize+offsetX,y*maze.cellSize+offsetY, cell.number.toString, new Font("Sans Serif", 0, 15), new Color(0,0,0), 1,1)
    }*/

  }


}
