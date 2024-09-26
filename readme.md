
## Comments

I would consider this task somewhat incomplete as I didn't get to a stage where I could test the routes.
There were many complications along the way and given a few more hours I could get there but I feel like this 
project should give a somewhat reasonable view into my programming abilities.

Despite the lack of routes testing I did perform test-driven-development, however the tests for the routes I originally
wrote needed re-writing when various parts of the codebase changed.

I am not overly happy with the end result. As I said before, the lack of testing is an issue - but there were others. 
Given more time I would like to have abstracted over other parts of the collatz machine process, including the timer
and the underlying state management, implemented using Refs. As a consequence, the tests take a few seconds to run as
they are using a real timer and have to sleep for several secconds inbetween actions.

## Process

This task was a complicated one, and it became more complicated as I understood it more, and I ended up having to use 
various tools that I either had never used before or wasn't familiar with, which naturally adds time and complextiy.
Chief among which was fiber management, which to me seemed mandatory to stop the underlying processes that I had started.
There was likely other ways to do that, including running everything as streams, but that idea came late and ultimately this
is a time based exercise.

### Stages

After some hacking to figure out how the underlying mechanics of the program would work, and learning about the Collatz
conjecture itself, I began by building the structure of the code, and roughly how the pieces would fit together. This
looks like objects and classes with unimplemented methods, for the most part.

The key takeaways from the initial learning were that two things were required and should define the code.
- state management
- process spawning

In a mutable world the state management would be simple but writing everything purely functional for me is vital. 
I elected to use `Ref`s for this.

In terms of process spawning I knew about the existence of `Fibers` and thought that would be the most accurate 
and representative way of doing it, but I had to learn how to use them properly.

In general, here are the important considerations:

- Immutability
- Separation of concerns
  - layering the code so that different layers perform their own set of operations
    - makes the code easier to change in future and more testable
- Abstraction
- Error handling

Abstraction is the main area that needs improving in this app. I attempted to abstract over all layers of the project,
including the fiber management, the machines and the machine state manager but only managed to do the latter in the time
I allocated to this exercise. This is something I would improve with more time.

## Improvements

- Abstraction (as mentioned above)
- Change management of processes to streams
  - to avoid fiber complexity
- Add tests for routes


## Running locally

docker pull sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0
docker build -t collatz-machine .  
docker run -it -p 8080:8080 collatz-machine 