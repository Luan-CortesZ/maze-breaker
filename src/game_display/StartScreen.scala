package src.game_display

import hevs.graphics.FunGraphics

import java.awt.event.{MouseAdapter, MouseEvent, MouseListener}
import java.awt.Color

/**
 * Start game window
 * @param display fungraphics window
 */
class StartScreen(var display: FunGraphics) {
  private val image: Image = new Image() //Bank of images
  private val center = display.width / 2 //Center of window
  private var btnStart: Button = _ //Starting button
  private var btnExit: Button = _ //Exit button
  private var onGameStartCallback: () => Unit = () => {} //When game starting

  display.clear(Color.black)
  display.drawTransformedPicture(center,center,0,display.width/image.background.getWidth,image.background)

  displayStartScreen()

  /**
   * Create starting and exit buttons
   */
  private def createButtons(): Unit = {
    //Specify button's size
    val buttonWidth = 250
    val buttonHeight = 100

    //Centering buttons
    val centerX = (display.width - buttonWidth) / 2
    val btnStartY = (display.height / 2) - buttonHeight
    val btnExitY = (display.height / 2) + 15

    //Initialize buttons
    btnStart = new Button(centerX, btnStartY + 50, "START", buttonWidth, buttonHeight, display)
    btnExit = new Button(centerX, btnExitY + 50, "EXIT", buttonWidth, buttonHeight, display)
  }

  /**
   * Create and show buttons
   */
  private def displayStartScreen() : Unit = {
    createButtons()
    displayButtons()
  }

  /**
   * On game start detection
   * @param callback function to do when game starting
   */
  def setOnGameStart(callback: () => Unit): Unit = {
    onGameStartCallback = callback
  }

  /**
   * Display button in window
   */
  private def displayButtons(): Unit = {
    btnExit.displayButton(new Color(42,34,88), Color.white, 60f)
    btnStart.displayButton(new Color(42,34,88), Color.white, 60f)
  }

  // Mouse listener to click on button
  display.addMouseListener(new MouseAdapter {
    //When mouse is clicked
    override def mouseClicked(e: MouseEvent): Unit = {
      //Verify button position and do event
      if(e.getX >= btnExit.posX && e.getX <= btnExit.getPosXWidthButton && e.getY >= btnExit.posY && e.getY <= btnExit.getPosYHeightButton){
        sys.exit()
      }else if(e.getX >= btnStart.posX && e.getX <= btnStart.getPosXWidthButton && e.getY >= btnStart.posY && e.getY <= btnStart.getPosYHeightButton){
        onGameStartCallback() // Notify the callback
      }
    }
  })
}
