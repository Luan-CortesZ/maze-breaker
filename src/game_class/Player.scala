package src.game_class

/**
 * Player class
 * @param posX coordinate x of player
 * @param posY coordinate y of player
 */
class Player(private var posX: Int, private var posY: Int) {

  def getPosX: Int = posX
  def getPosY: Int = posY

  def setPosX(value: Int): Unit = posX = value
  def setPosY(value: Int): Unit = posY = value

  /**
   * Move player by x and y
   * @param deltaX offset x to move player horizontally
   * @param deltaY offset y to move player vertically
   */
  def move(deltaX: Int, deltaY: Int): Unit = {
    posX += deltaX
    posY += deltaY
  }
}
