# ANT

*The next revolutionary pathing system*

This pathing algorithm which has been never been created before as of my information. 
It creates a cubic hermite spline depending on its start and end positions. Then it 
simulates a graph of the velocities and the acceleration across the graph. Then, it find the maximum point 
on the the graph and scales it to the maximum point that you set. Following, 
using that maximum point, we gat the rotation that it needs to be at for those 
points so that the program can normalize to that point. 
So, it creates a path that is literally the fastest along those constraints. This took so much math and I 
had so much fun making this.

Incorporates:
- Newton's Method
- Gradient Descent
- Cubic Hermite Splines
- Velocity Normalization
- Acceleration Normalization

### How to Run the Project in IntelliJ IDEA
1. Install IntelliJ IDEA (Community or Ultimate Edition).
2. Ensure you have JDK 17 or higher installed (required to compile and run Kotlin).
3. Download the project as a ZIP file and extract it.
4. Open IntelliJ IDEA and click Open on the welcome screen.
5. Navigate to the project folder and click OK.
6. In the Project view on the left side of IntelliJ, expand the src directory.
7. Click the green Run button (▶️) or press Shift + F10 (Windows/Linux) or Control + R (macOS)