package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont

import java.awt.{Color, Font}

class Button(var posX: Int, var posY: Int, var title: String, var width: Int, var height: Int, private var display: FunGraphics) {

  /**
   * Display button with text at center
   * @param buttonColor color of button
   * @param textColor color of text
   * @param textSize size of text
   * @param hasBorder if button has border
   */
  def displayButton(buttonColor: Color, textColor: Color, textSize: Float, hasBorder: Boolean = true): Unit = {
    //Show button border
    if(hasBorder){
      display.setColor(Color.black) //border color
      display.drawFillRect(posX, posY, width+10, height+10)
    }

    //Draw button
    display.setColor(buttonColor)
    display.drawFillRect(posX, posY, width, height)

    //Initialize font
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
