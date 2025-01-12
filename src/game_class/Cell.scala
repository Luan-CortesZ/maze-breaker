package src.game_class

import hevs.graphics.utils.GraphicsBitmap

/**
 *
 * @param size cells size
 * @param isWall is this cell a wall or not
 * @param number number assigned to this cell
 * @param distanceFromExit distance from this cell to Exit
 * @param isPathToExit is this cell in the shortest path to Exit
 */
class Cell(var size: Int, var isWall: Boolean = true, var number: Int = 0, var distanceFromExit: Int = -1, var isPathToExit: Boolean = false) {
  var image: GraphicsBitmap = _
  var hasTorch: Boolean = false

  /**
   * Set new Image to this cell
   * @param newImage new Image assigned to this cell
   */
  def setImage(newImage: GraphicsBitmap): Unit = {
    image = newImage
  }

  def this() = {
    this(0)
  }
}
