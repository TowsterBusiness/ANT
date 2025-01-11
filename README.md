# ANT

*The next revolutionary pathing system*

This pathing algorithm has been never been created before as of my information. 
It creates a cubic hermite spline depending on its start and end positions. Then it 
simulates a graph of the velocities across the graph. Then, it find the maximum point 
on the the graph and scales it to the maximum point that you set. So, it creates a path 
that is literally the fastest along those constraints. This took so much math and I 
had so much fun making this

Incorporates:
- Newton's Method
- Gradient Descent
- Cubic Hermite Splines