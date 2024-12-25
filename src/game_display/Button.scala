package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont

import java.awt.font.FontRenderContext
import java.awt.{Color, Font}

class Button(var posX: Int, var posY: Int, var title: String, var width: Int, var height: Int, private var display: FunGraphics) {

  /**
   * display button with text at center
   * @param color button's color
   * @param textSize text size
   */
  def displayButton(buttonColor: Color, textColor: Color, textSize: Float): Unit = {
    display.setColor(buttonColor)
    display.drawFillRect(posX, posY, width, height)

    // Make text center
    val font = new CustomFont().upheaval.deriveFont(Font.PLAIN, textSize)
    val fontRenderContext = new FontRenderContext(null, true, true)
    val fontMetrics = font.getLineMetrics(title, fontRenderContext)
    val textHeight = fontMetrics.getAscent
    val descent = fontMetrics.getDescent
    val textWidth = font.getStringBounds(title, fontRenderContext).getWidth.toInt
    val textX: Int = posX + (width - textWidth) / 2
    val textY: Int = (posY + (height + textHeight) / 2 - descent.toInt).toInt

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
