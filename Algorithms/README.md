# Algorithms

## Overview
The objective of the Algorithm sub-team is to formulate an algorithm that drives the robot through an unknown maze. There are two main phases of the algorithm - the exploration and the fastest path. The exploration involves the robot exploring the maze to detect the locations of the obstacles. This information is then stored for the execution of the fastest path where the robot finds the shortest path between the Start zone (coordinates: 1,1) and the Goal zone (coordinates: 13,18). In the process, the algorithm which is executed on the PC, communicates with the robot through the Raspberry Pi, to the Arduino and motors, and the Android tablet.

## Exploration
The following flow chart shows our exploration algorithm logic, which follows a right wall hugging algorithm. Our exploration will have 10 cases - 1, 2, 3, 4, 5, 6, A, B, C, D, as illustrated in the flow chart. The movements will be based on these 10 cases.

<img width="150" alt="image" src=https://user-images.githubusercontent.com/48685014/100953116-d60e9a80-354c-11eb-9673-af4582ca0e6d.png>

## Fastest Path
The fastest path program should compute a path that can bring the robot from the Start to Goal zone with the shortest time possible. As such, we will be using the A star (A*) search, which is able to achieve optimality and completeness, two valuable properties of search algorithms.

1. Optimality
When a search algorithm has the property of optimality, it means it is guaranteed to find the best possible solution.

2. Completeness
When a search algorithm has the property of completeness, it means that if a solution to a given problem exists, the algorithm is guaranteed to find it.

## Auto Re-alignment of Robot’s Position
Auto-realignment is necessary to make sure that the robot is moving in a straight-line. This is because the error in the movement of the robot’s motors and external conditions such as excess friction may result in the robot to deviate from a straight line in real-life applications. The robot needs to move in a straight line to work in accordance with the algorithm. As a result, the Arduino has a function that will be called by the Algorithm program whenever the robot needs to realign.

The algorithm program will decide to trigger the auto-realignment condition. Whenever the robot is inside a corner made by two obstacles or walls, it would perform auto-realignment two times by rotating to align to the wall and then rotate back to the correct orientation.

## Simulation in GUI
This is our GUI used to simulate the algorithm. This [video](https://drive.google.com/file/d/1mAI2orzMnwGE0eveNlQrg34YBCxBdEIb/view?usp=sharing) highlights our simulator in action.

<img width="450" alt="image" src=https://user-images.githubusercontent.com/48685014/100953319-546b3c80-354d-11eb-91ca-9833da4e400e.png>
