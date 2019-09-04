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

## Configuration
Except standard spring application properties there are following properties:
* decay - float value in range from 0 to 1. Sets memory decay for Markov's chain. 1 is perfect memory.
Decay in range from 0 to 1 -> model would forget earlier observations and adapt to changes faster

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

Considering peak load of **9000** concurrent users each having 30 seconds sessions during 10 minutes, we get 18000 users per minute,
or 1080000 user per hour, or 25920000 users daily. Of course, during day user activity will be changing but we don't have
 any diagrams showing activity profile, so we go with simplest case. This is more than 1000000 users requested in the task.
 
Quick (1 minite) test for **12000** concurrent users:
```
2019-09-04 21:08:09                                          78s elapsed
---- Requests ------------------------------------------------------------------
> Global                                                   (OK=816000 KO=0     )
> RockPaperScissors                                        (OK=816000 KO=0     )

---- rock-paper-scissors concurrent users scenario -----------------------------
          active: 0      / done: 24000 
================================================================================

Simulation computerdatabase.RockPaperScissorsSimulation completed in 78 seconds
Parsing log file(s)...
Parsing log file(s) done
Generating reports...

================================================================================
---- Global Information --------------------------------------------------------
> request count                                     816000 (OK=816000 KO=0     )
> min response time                                     -3 (OK=-3     KO=-     )
> max response time                                   6520 (OK=6520   KO=-     )
> mean response time                                    35 (OK=35     KO=-     )
> std deviation                                        365 (OK=365    KO=-     )
> response time 50th percentile                          0 (OK=0      KO=-     )
> response time 75th percentile                          0 (OK=0      KO=-     )
> response time 95th percentile                          1 (OK=1      KO=-     )
> response time 99th percentile                        728 (OK=730    KO=-     )
> mean requests/sec                                10329.114 (OK=10329.114 KO=-     )
---- Response Time Distribution ------------------------------------------------
> t < 800 ms                                        808326 ( 99%)
> 800 ms < t < 1200 ms                                1979 (  0%)
> t > 1200 ms                                         5695 (  1%)
> failed                                                 0 (  0%)

```
We could see that max response time and 99th percentile time grow significantly.

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