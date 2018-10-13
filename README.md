Digital Dreidel
===============
Originally made in 2016 as a part of my goal to self-study JavaFX

Code updated to fix various bugs on October 8th, 2018

The Game
--------
This game allows users to select the number of players, chips, and chip worth, and then simulate the traditional game of Dreidel on the their computers.

![Dreidel Sample](https://github.com/vasilzhigilei/dreidel/blob/master/dreidel/SampleGame.PNG)

The game does not require any external libraries. It only uses JavaFX for the graphical work.

There is still needless repetition in the code that I must revise. Examples include repetition of the same loop in multiple locations within the code, as well as similar code for the MouseEvents for box, box2, and pyramid.

#### List of Known Bugs That Still Need to Be Fixed
* If autospin is true, every other turn box and pyramid do not spin (box2 spins every time, it is the one that is triggered automatically).
* Cash becomes long decimal due to double calculations in certain cases where chipWorth is not a whole number.
* Sometimes the display color is not updated properly for a new loser.

History
-------

##### Version 1.0.1 (9 October, 2018)
* Added doc comments to methods

##### Version 1.0 (8 October, 2018)
* Reworked the "rotation end" code for determining chips, losers, winner, and next player.
* Fixed math with "He" result (Math.ceil)
