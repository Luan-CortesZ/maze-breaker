package src.game_class

/**
 *
 * @param size cells size
 * @param isWall is this cell a wall or not
 * @param number number assigned to this cell
 */
class Cell(var size: Int, var isWall: Boolean = true, var number: Int = 0, var distanceFromExit: Int = -1, var isPathToExit: Boolean = false) {
  def this() = {
    this(0)
  }
}
