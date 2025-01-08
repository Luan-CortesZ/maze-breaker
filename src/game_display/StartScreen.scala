package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont
import src.game_class.Maze

import java.awt.event.{MouseEvent, MouseListener}
import java.awt.{Color, Font}

class StartScreen(var display: FunGraphics) {

  private var btnStart: Button = _
  private var btnExit: Button = _
  private var onGameStartCallback: () => Unit = () => {}
  var startGame: Boolean = false;

  

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
    displayTitle()
    displayButtons()
  }

  def setOnGameStart(callback: () => Unit): Unit = {
    onGameStartCallback = callback
  }

  private def displayButtons(): Unit = {
    btnExit.displayButton(Color.black, Color.white, 75f)
    btnStart.displayButton(Color.black, Color.white, 75f)
  }

  /**
   * Display window title
   */
  private def displayTitle(): Unit = {
    val text: String = "Maze Breaker"
    val font = new CustomFont().upheaval.deriveFont(Font.PLAIN, 90f)
    val stringSize = display.getStringSize(text, font)

    val centerX = ((display.width - stringSize.getWidth) / 2).toInt
    val centerY = ((display.height / 2) - stringSize.getHeight - 200).toInt

    display.drawString(centerX,centerY,text, font, Color.black)
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
