# Rock-paper-scissors web application

## Technical stack
* Java 11 (yes, we have Java 12 already but 11 is LTS while 12 is not)
* Spring Boot with Netty as application server
* Spring WebFlux as web sockets framework for high throughput
* Lombok to get rid of java language verbosity

## Assumptions
* it doesn't make sense to have common transition probability matrix for all users since each user has it's own
non-random distribution of moves which we want to adapt to.
* deployment configuration is out of scope

## Build
`./mvnw clean package` under JDK 11

## Run
`java -jar rock-paper-scissors-{version}.jar` under JRE 11

## Performance
Tested on Intel Core i7-4720HQ @ 2.60GHz with 16 GB RAM.
Gatling was used for load/performance testing.

### TLDR
In test scenario each user plays 30 turns with 1 second between each turn.

Results of load testing could be found in `9000_concurrent_users_during_10_minutes` directory of this repo, `index.html` file.
100% of 5724920 requests done by 168380 users in 10 minutes. 99th percentile for response time is 24 ms.
For hardware configuration mentioned above server could accept up to **9000** concurrent users.

Considering peak load of **9000** concurrent users each having 30 seconds sessions, we get 18000 users per minute,
or 1080000 user per hour, or 25920000 users daily. Of course, during day user activity will be changing but we don't have
 any diagrams showing activity profile, so we go with simplest case. This is more than 1000000 users requested in the task.
 
Test for 10000 concurrent users has shown that response time became to grow significantly and there were
something like 0.01% of failed requests.

There are no requirements on peak load so it is difficult to say if 9000 concurrent users is enough.
Better hardware will provide better results.

### Environment setup for running Gatling script:
1. Install Scala (tested on 2.13.0) https://www.scala-lang.org/download/ and add it to PATH environment variable
2. Download Gatling bundle https://gatling.io/open-source/start-testing/
3. Copy scala script from this repo inside Gatling bundle: `cp rock-paper-scissors/LoadTest.scala {gatling-bundle-dir}/user-files/simulations/computerdatabase/LoadTest.scala`
4. Inside Gatling bundle: execute `bin/gatling.sh`
5. In your terminal, following message is displayed:
```
Choose a simulation number:
     [0] computerdatabase.BasicSimulation
     [1] computerdatabase.RockPaperScissorsSimulation
     [2] computerdatabase.advanced.AdvancedSimulationStep01
     [3] computerdatabase.advanced.AdvancedSimulationStep02
     [4] computerdatabase.advanced.AdvancedSimulationStep03
     [5] computerdatabase.advanced.AdvancedSimulationStep04
     [6] computerdatabase.advanced.AdvancedSimulationStep05

```
6. Choose number pointing to `computerdatabase.RockPaperScissorsSimulation` (1 in our case) and press Enter
7. In your terminal, following message is displayed:
```
Select run description (optional)
```
8. Enter any text and press Enter or just press Enter.
9. You could see that script has started executing, output goes both to console and log file.
10. Results of the load testing script execution are available in console (plain text) and in `results` directory
inside Gatling bundle. Each simulation result is located in separate subdirectory with name like `rockpaperscissorssimulation-20190903061926628`
where first part of name is simulation's name and the second one is timestamp. Inside that directory
you could open `index.html` file to see test report in HTML format.