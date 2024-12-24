package src
import java.awt.event.{KeyEvent, KeyListener}
// import javax.swing.{JFrame, JPanel}

class KeyMovement extends KeyListener {
  // Fait une action quand la touche est tapée
  override def keyTyped(e: KeyEvent): Unit = {
    if(e.getKeyCode == KeyEvent.VK_KP_UP || e.getKeyCode == KeyEvent.VK_W){
      println("la touche W ou flèche du haut a été sélect")
    } else if (e.getKeyCode == KeyEvent.VK_KP_DOWN || e.getKeyCode == KeyEvent.VK_S){
      println("la touche S ou flèche du bas a été sélect")
    } else if (e.getKeyCode == KeyEvent.VK_KP_RIGHT || e.getKeyCode == KeyEvent.VK_D){
      println("la touche D ou flèche de droite a été sélect")
    }else if (e.getKeyCode == KeyEvent.VK_KP_LEFT || e.getKeyCode == KeyEvent.VK_A){
      println("la touche A ou flèche de gauche a été sélect")
    }
  }
  // Fait une action quand la touche est préssée
  override def keyPressed(e: KeyEvent): Unit = {
    ???
  }
  // Fait une action quand la touche est relachée
  override def keyReleased(e: KeyEvent): Unit = {

  }
}
