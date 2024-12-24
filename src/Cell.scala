package src

/**
 *
 * @param size cells size
 * @param isWall is this cell a wall or not
 * @param isExit is this cell the exit
 * @param isEntry is this cell the entry
 * @param number number assigned to this cell
 */
class Cell(var size: Int, var isWall: Boolean = true, var isExit: Boolean = false, var isEntry: Boolean = false, var number: Int = 0) {
  def this() = {
    this(0)
  }
}