package src.fonts

import java.awt.Font
import java.io.File

/**
 * CustomFont class to get custom font
 */
class CustomFont {
  val upheaval: Font = Font.createFont(Font.TRUETYPE_FONT, new File("src\\fonts\\upheavtt.ttf"))
}