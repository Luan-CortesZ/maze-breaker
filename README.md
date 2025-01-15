
# Maze-Breaker

## Authors

- [@Luan Cortes Zuka](https://github.com/Luan-CortesZ)
- [@Filipe Da Silva](https://github.com/Dricjord)


## Explication

**Maze-Breaker** est un jeu où vous incarnez un personnage essayant d'échapper un labyrinthe interminable. Trouvez votre chemin à travers des pièges qui vous octroiront des bonus pour vous aider dans votre périple ou des malus, pour vous empêcher d'atteindre la sortie.
## 🎮 Fonctionnalités principales

- **Labyrinthe aléatoire** : Chaque partie est unique grâce à une génération aléatoire du labyrinthe
- **Vue limitée** : La vue du joueur est limité à un carré de 3x3 centrer sur le joueur.
- **Difficulté** : Chaque niveau est de plus en plus difficile, agrandissant le labyrinthe et rajoutant encore plus de pièges.
- **Cases événement** : Certaines cases déclenchent des événements spéciaux :
  - **Clé** : Trouvez une clé dissimulée dans le labyrinthe indispensable ouvrir la porte de sortie.
  - **Quiz** : Répondez à des questions sélectionnées aléatoirement. Selon votre réponse :
    - Bonne réponse : Vous recevez un bonus.
    - Mauvaise réponse : Vous subissez un malus.
## 🔧 Setup
### Prérequis
- IntelliJ IDEA installed
- Scala plugin in IntelliJ IDEA
- Git installed either on IntelliJ / cmd / third party app

### Installation
- Clonez le repo sur la branche `master`
- Ajoutez la libraire `fungraphics-1.5.15.jar`

### Lancement du jeu
- Lancez le programme en vous situant sur le `Main`
## Comment jouer ?
### Gameplay
Lors du lancement du jeu vous avez un menu où vous pourrez choisir de :
- Lancer le jeu
- Quitter le jeu

#### Mouvement
Utiliser les touches `W, A, S ,D` pour avancer, tourner à gauche, reculer et tourner à droite

#### Clé
Trouver le coffre caché dans le labyrinthe pour dévérouiller la sortie.

#### Questions
Tombez sur une case question pour répondre à une question sur les cours HES. Si vous répondez juste, vous recevrez un bonus, si vous répondez faux, vous subissez un malus

#### Liste des bonus
- Agrandi la zone de vision du joueur avec une zone de 10x10 pendant 6.5 secondes
- Montre au joueur le chemin jusqu'à la sortie pendant 6.5 secondes
- Fait en sorte que le joueur se déplace de 2 par 2 pendant 6.5 secondes

#### Liste des malus
- Immobilise le joueur pendant 6.5 secondes
- Téléporte le joueur à l'entrée du labyrinthe
- Téléporte le joueur au hasard dans le labyrinthe
- Fait en sorte que le joueur se dép'lace de 8 par 8 pendant 6.5 secondes

## Demo

Insert gif or link to demo

