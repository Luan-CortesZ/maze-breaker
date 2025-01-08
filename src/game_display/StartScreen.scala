package src.game_display

import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap

import java.awt.event.{MouseEvent, MouseListener}
import java.awt.Color

class StartScreen(var display: FunGraphics) {

  private var btnStart: Button = _
  private var btnExit: Button = _
  private var onGameStartCallback: () => Unit = () => {}
  private var startGame: Boolean = false;
  private val background = new GraphicsBitmap("/src/res/menu.png")
  private val center = display.width / 2
  display.clear(Color.black)
  display.drawTransformedPicture(center,center,0,display.width/background.getWidth,background)

  createButtons()
  displayStartScreen()

  private def createButtons(): Unit = {
    val buttonWidth = 250
    val buttonHeight = 100

    val centerX = (display.width - buttonWidth) / 2

    val btnStartY = (display.height / 2) - buttonHeight
    val btnExitY = (display.height / 2) + 15

    btnStart = new Button(centerX, btnStartY + 50, "START", buttonWidth, buttonHeight, display)
    btnExit = new Button(centerX, btnExitY + 50, "EXIT", buttonWidth, buttonHeight, display)
  }

  private def displayStartScreen() : Unit = {
    displayButtons()
  }

  def setOnGameStart(callback: () => Unit): Unit = {
    onGameStartCallback = callback
  }

  private def displayButtons(): Unit = {
    btnExit.displayButton(new Color(42,34,88), Color.white, 60f)
    btnStart.displayButton(new Color(42,34,88), Color.white, 60f)
  }

  // Mouse listener to click on button
  display.addMouseListener(new MouseListener {
    override def mouseClicked(e: MouseEvent): Unit = {
      if(e.getX >= btnExit.posX && e.getX <= btnExit.getPosXWidthButton && e.getY >= btnExit.posY && e.getY <= btnExit.getPosYHeightButton){
        sys.exit()
      }else if(e.getX >= btnStart.posX && e.getX <= btnStart.getPosXWidthButton && e.getY >= btnStart.posY && e.getY <= btnStart.getPosYHeightButton){
        startGame = true
        onGameStartCallback() // Notify the callback
      }
    }
    override def mousePressed(e: MouseEvent): Unit = {}
    override def mouseReleased(e: MouseEvent): Unit = {}
    override def mouseEntered(e: MouseEvent): Unit = {}
    override def mouseExited(e: MouseEvent): Unit = {}
  })
}
