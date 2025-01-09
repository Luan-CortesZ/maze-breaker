package src.game_display

import hevs.graphics.utils.GraphicsBitmap

class Image {
  val keyPicture = new GraphicsBitmap("/src/res/objects/chest.png")
  val wallPicture = new GraphicsBitmap("/src/res/maze/wall.png")
  val opened_door = new GraphicsBitmap("/src/res/maze/opened_door.png")
  val locked_door = new GraphicsBitmap("/src/res/maze/locked_door.png")
  val entry_door = new GraphicsBitmap("/src/res/maze/entry_door.png")
  val torch = new GraphicsBitmap("/src/res/maze/torch.png")
  val playerLeft = new GraphicsBitmap("/src/res/player/left.png")
  val playerRight = new GraphicsBitmap("/src/res/player/right.png")
  val playerTop = new GraphicsBitmap("/src/res/player/top.png")
  val playerDown = new GraphicsBitmap("/src/res/player/down.png")
  val lstGroundPictures: List[GraphicsBitmap] = List(
    new GraphicsBitmap("/src/res/maze/ground.png"),
    new GraphicsBitmap("/src/res/maze/ground2.png"),
    new GraphicsBitmap("/src/res/maze/ground3.png"),
    new GraphicsBitmap("/src/res/maze/ground4.png"),
    new GraphicsBitmap("/src/res/maze/ground5.png"),
    new GraphicsBitmap("/src/res/maze/ground6.png"),
    new GraphicsBitmap("/src/res/maze/ground7.png"),
    new GraphicsBitmap("/src/res/maze/ground8.png"),
  )
}
