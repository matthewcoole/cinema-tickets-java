# Java Cinema Tickets Code
This code implements the java cinema tickets service

## Dependencies
The following additional dependencies have been added to `pom.xml`:
- Maven Surefire Plugin

## Build
Build has been tested on the following platform:
- Ubuntu 22.04 LTS
- Oracle JDK 17.0.9
- Apache Maven 3.6.3

To build, run:
```shell
mvn package
```
This will also run the unit tests.

Tests can be run manually using:
```shell
mvn test
```

## Solution Description
My ticket service makes use of guard clauses to check for constraints before processing the payment and seat reservation. 
The seat and payment services are handled using dependency injection (although this is a slight violation of the comment in the TicketServiceImpl class as I created a new public constructor method). 
I have also separated out the pricing list into a different class that the ticket service depends on as I would expect, in a real system, the prices to be stored in some kind of database where they could be updated and changed; I'd also expect this to be di'd but implementing some kind of database service seemed a little out of scope.

### Assumptions
- Prices should probably be handled using BigDecimal or some kind of currency library, but based on the use of integers in the payment service (that can't be modified) I made the assumption to treat currency as just integers representing pounds.
- It was unclear if one adult could have multiple infants sat on their lap. Should there be one adult for every one infant? Could an adult have two infants on their lap? Can an accompanying child have an infant on their lap? I have simply allowed for any number of infant tickets to be purchased as long as there is at least one adult (this would be something to clarify when gathering real world requirements).
