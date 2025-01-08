package src.game_class

import src.game_display.Image

class Exit(var isLock: Boolean = true) extends Cell{
  val images: Image = new Image()
  def unLock(): Unit = {
    isLock = false;
    image = images.opened_door
  }

}
