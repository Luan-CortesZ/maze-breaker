package src.game_display

import hevs.graphics.FunGraphics
import hevs.graphics.utils.GraphicsBitmap
import src.fonts.CustomFont
import src.game_class.Maze

import java.awt.event.{MouseEvent, MouseListener}
import java.awt.{Color, Font}

class StartScreen(var display: FunGraphics) {

  private var btnStart: Button = _
  private var btnExit: Button = _
  private var onGameStartCallback: () => Unit = () => {}
  var startGame: Boolean = false;
  val background = new GraphicsBitmap("/src/res/menu.png")

  display.drawTransformedPicture(300,400,0,0.3,background)

  createButtons()
  displayStartScreen()

  private def createButtons(): Unit = {
    val buttonWidth = 300
    val buttonHeight = 120

    val centerX = (display.width - buttonWidth) / 2

    val btnStartY = (display.height / 2) - buttonHeight - 5
    val btnExitY = (display.height / 2) + 5

    btnStart = new Button(centerX, btnStartY, "START", buttonWidth, buttonHeight, display)
    btnExit = new Button(centerX, btnExitY, "EXIT", buttonWidth, buttonHeight, display)
  }

  private def displayStartScreen() : Unit = {
    displayButtons()
  }

  def setOnGameStart(callback: () => Unit): Unit = {
    onGameStartCallback = callback
  }

  private def displayButtons(): Unit = {
    btnExit.displayButton(Color.black, Color.white, 75f)
    btnStart.displayButton(Color.black, Color.white, 75f)
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
