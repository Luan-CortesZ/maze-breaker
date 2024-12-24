package src

// Stock position ou la modifier

// Crée deux variables et jouer avec dans la création du curseur

class Player(var posX: Int, var posY: Int) {

  def getPosX(): Int = posX
  def getPosY(): Int = posY

  def move(deltaX: Int, deltaY: Int): Unit = {
    posX += deltaX
    posY += deltaY
  }
}
