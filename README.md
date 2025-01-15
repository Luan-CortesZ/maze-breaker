# Maze-Breaker

## Authors

- [@Luan Cortes Zuka](https://github.com/Luan-CortesZ)
- [@Filipe Da Silva](https://github.com/Dricjord)

## Description

**Maze-Breaker** is a game where you play as a character trying to escape an endless maze. Find your way through traps that can grant you bonuses to aid your journey or penalties to hinder your progress toward the exit.

## 🎮 Key Features

- **Random Maze**: Each game is unique thanks to the random maze generation.
- **Limited Vision**: The player's view is restricted to a 3x3 square centered on the character.
- **Difficulty Scaling**: Each level becomes progressively harder, enlarging the maze and adding more traps.
- **Event Tiles**: Some tiles trigger special events:
  - **Key**: Find a hidden key in the maze, essential for unlocking the exit.
  - **Quiz**: Answer randomly selected questions. Depending on your answer:
    - Correct: Receive a bonus.
    - Incorrect: Suffer a penalty.

## 🔧 Setup

### Prerequisites
- IntelliJ IDEA installed
- Scala plugin installed in IntelliJ IDEA
- Git installed (via IntelliJ, command line, or a third-party app)

### Installation
1. Clone the repository from the `master` branch.
2. Add the library `fungraphics-1.5.15.jar`.

### Launching the Game
- Run the program from the `Main` class.

## 🕹️ How to Play?

### Gameplay
When you launch the game, you’ll see a menu where you can choose to:
- Start the game
- Quit the game

#### Movement
Use the keys `W, A, S, D` to move forward, turn left, move backward, and turn right, respectively.

#### Key
Find the hidden chest in the maze to unlock the exit.

#### Questions
Land on a quiz tile to answer a question related to HES courses. A correct answer grants a bonus, while a wrong answer results in a penalty.

#### Bonus List
- Expands the player's vision to a 10x10 area for 5 seconds.
- Displays the path to the exit for 5 seconds.
- Allows the player to move two steps at a time for 5 seconds.

#### Penalty List
- Freezes the player for 5 seconds.
- Teleports the player back to the maze entrance.
- Randomly teleports the player within the maze.
- Forces the player to move eight steps at a time for 5 seconds.

## 🖼️ Demo

### Gameplay
- Starting the game and moving the character

![startgame](https://github.com/user-attachments/assets/085fe8f1-6cb1-4ba8-8ded-bb66036622ab)

- Locked Door

![doorlocked](https://github.com/user-attachments/assets/871ce5ce-65c2-4355-bd37-574633f16a5c)

- Retrieving the Key and Advancing to the Next Level

![getkey_nextlevel](https://github.com/user-attachments/assets/2bea4a99-412f-45d2-a612-fa3cb85c7e26)

### Bonus & Penalty
- Bonus: Increased Vision

![bonus_increaseview](https://github.com/user-attachments/assets/241826b6-e70b-4fbb-a564-8381c064d18a)

- Bonus: Path Display

![bonus_showpath](https://github.com/user-attachments/assets/54433974-9aa8-4668-8996-3252b99595df)

- Penalty: Freezing

![malus_freeze](https://github.com/user-attachments/assets/1dd5e97c-ad41-448d-a633-d85e16da1892)

- Penalty: Teleportation to Entrance

![malus_tpentry](https://github.com/user-attachments/assets/8fb8d207-d258-4609-8e78-2080f4d54f11)
