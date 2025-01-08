package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont

import java.awt.{Color, Font}

class Button(var posX: Int, var posY: Int, var title: String, var width: Int, var height: Int, private var display: FunGraphics) {

  /**
   * display button with text at center
   * @param buttonColor button's color
   * @param textSize text size
   */
  def displayButton(buttonColor: Color, textColor: Color, textSize: Float, border: Boolean = true): Unit = {
    if(border){
      display.setColor(Color.black)
      display.drawFillRect(posX, posY, width+10, height+10)
    }
    display.setColor(buttonColor)
    display.drawFillRect(posX, posY, width, height)
    val font = new CustomFont().upheaval.deriveFont(Font.PLAIN, textSize)
    val stringSize = display.getStringSize(title, font)
    // Make text center
    val textX: Int = (posX + (width - stringSize.getWidth) / 2).toInt
    val textY: Int = (posY + (height + stringSize.getHeight) / 2 - 5).toInt

    // Draw centered text
    display.drawString(textX, textY, title, font, textColor)
  }

  /**
   * calculate width with posX
   * @return width plus posX
   */
  def getPosXWidthButton: Int = {
    width + posX
  }

  /**
   * calculate height with posY
   * @return height plus posY
   */
  def getPosYHeightButton: Int = {
    height + posY
  }
}
