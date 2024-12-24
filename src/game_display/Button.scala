package src.game_display

import hevs.graphics.FunGraphics
import src.fonts.CustomFont

import java.awt.font.FontRenderContext
import java.awt.{Color, Font}

class Button(var posX: Int, var posY: Int, var title: String, var width: Int, var height: Int, private var display: FunGraphics) {

  def displayButton(color: Color, textSize: Float): Unit = {
    display.setColor(Color.black)
    display.drawFillRect(posX, posY, width, height)

    val font = new CustomFont().upheaval.deriveFont(Font.PLAIN, textSize)
    val fontRenderContext = new FontRenderContext(null, true, true)
    val fontMetrics = font.getLineMetrics(title, fontRenderContext)
    val textHeight = fontMetrics.getAscent
    val descent = fontMetrics.getDescent
    val textWidth = font.getStringBounds(title, fontRenderContext).getWidth.toInt
    val textX: Int = posX + (width - textWidth) / 2
    val textY: Int = (posY + (height + textHeight) / 2 - descent.toInt).toInt

    // Dessine le texte centré
    display.drawString(textX, textY, title, font, color)
  }

  def getPosXWidthButton: Int = {
    width + posX
  }

  def getPosYHeightButton: Int = {
    height + posY
  }
}
