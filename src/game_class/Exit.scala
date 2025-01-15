package src.game_class

import src.game_display.Image

/**
 * Class inherited from Cell class to know Exit cell
 * @param isLock Know if exit is lock or not
 */
class Exit(var isLock: Boolean = true) extends Cell{
  val images: Image = new Image()

  /**
   * Unlock entry
   */
  def unLock(): Unit = {
    isLock = false;
    image = images.opened_door
  }
}