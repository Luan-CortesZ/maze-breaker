package src.game_display

import hevs.graphics.FunGraphics
import src.Start
import src.fonts.CustomFont

import java.awt.event.{MouseEvent, MouseListener}
import java.awt.{Color, Font}

class StartScreen(var title: String) {
  private val display = new FunGraphics(500,500, "Maze-breaker | Start")
  private val btnExit = new Button(150, 350,"EXIT", 200, 80, display)
  private val btnStart = new Button(150, 250,"START", 200, 80, display)

  displayTitle()
  btnExit.displayButton(Color.black, Color.white, 50f)
  btnStart.displayButton(Color.black, Color.white, 50f)

  /**
   * Display window title
   */
  private def displayTitle(): Unit = {
    display.drawString(40,100,title, new CustomFont().upheaval.deriveFont(Font.PLAIN, 60f), Color.black)
  }

  // Mouse listener to click on button
  display.addMouseListener(new MouseListener {
    override def mouseClicked(e: MouseEvent): Unit = {
      if(e.getX >= btnExit.posX && e.getX <= btnExit.getPosXWidthButton && e.getY >= btnExit.posY && e.getY <= btnExit.getPosYHeightButton){
        sys.exit()
      }else if(e.getX >= btnStart.posX && e.getX <= btnStart.getPosXWidthButton && e.getY >= btnStart.posY && e.getY <= btnStart.getPosYHeightButton){
        Start.Generate()
        display.mainFrame.dispose()
      }
    }

    override def mousePressed(e: MouseEvent): Unit = {

    }

    override def mouseReleased(e: MouseEvent): Unit = {

    }

    override def mouseEntered(e: MouseEvent): Unit = {

    }

    override def mouseExited(e: MouseEvent): Unit = {

    }
  })
}
