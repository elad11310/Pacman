# Pacman Game:

### Old and good Pacman game , written in java using BFS algorithm for ghosts to track the pacman.
### In addition using threads for each ghost to move independently , each ghost object implements an interface of behaviour (aggressive , patrol or static).

* Aggressive - the ghost will constantly track the pacman in the fastest path using BFS.
* Patrol - the ghost will get random path and will move back and forth .
* Static - the ghost will not move untill the pacman gets into it's raduis , and when occures , the ghost will chase aggressive untill the pacman gets out of it's radius .

### Further more using an interface to build the graph which has an absrtact function to return the neibhours of each points in the matrix (for BFS algorithm and the pacman move).

### By using interfaces , threads and BFS algorithm iv'e got a nice old fashion pacman game.

### There is an option to change map size and make different levels.

### Small size map:

![pacman](https://user-images.githubusercontent.com/57447475/145708176-84861d30-b868-418b-a792-041150c49f6e.jpeg)



### Big size map:


![pacman3](https://user-images.githubusercontent.com/57447475/145708284-63a669a6-0c0e-4445-9492-8ce187c32a99.jpeg)



### Update : 
* Added special dots which are bigger then normal one's and if pacman eats them , the ghosts are in fade mode and can be eaten by pacman.


![WhatsApp Image 2021-12-13 at 18 20 53](https://user-images.githubusercontent.com/57447475/145849325-354bd0f1-09ec-4cee-bc28-316b06be01c9.jpeg)



