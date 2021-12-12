# Pacman Game:

### Old and good Pacman game , written in java using BFS algorithm for ghosts to track after the pacman.
### In addition using threads for each ghost to move independently , each ghost object implements an interface of behaviour (aggressive , patrol or static).

* Aggressive - the ghost will constantly track the pacman in the fastest path using BFS.
* Patrol - the ghost will get random path and will move back and forth .
* Static - the ghost will not move untill the pacman gets into it's raduis , and when occures , the ghost will chase aggressive untill the pacman gets out of it's radius .

### Further more using an interface to build the graph which has an absrtact function to return the neibhours of each points in the matrix (for BFS algorithm and the pacman move).

### By using interfaces , threads and BFS algorithm iv'e got a nice old fashion pacman game.

### the code is generic and allows me to change maps size to be able to create levels.

### Small size map:

![pacman](https://user-images.githubusercontent.com/57447475/145708176-84861d30-b868-418b-a792-041150c49f6e.jpeg)



### Big size map:

![pacman2](https://user-images.githubusercontent.com/57447475/145708184-d8f7817c-7f6d-41f4-9a19-3bda81093851.jpeg)



