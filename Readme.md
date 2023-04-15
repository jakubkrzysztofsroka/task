# Test Task

This task includes creating an application that will parse the CSV file containing
the number in each row and then divide it into separate streams based on the modulo operation. Each
stream should produce the sum of input and send it to the queue. The application should then
provide websocket api with docs that will forward sums through it.

## How to run 

setup infrastructure 
```shell
docker docker-compose -f docker-compose/kafka.yaml up
```
then run application
```shell
sbt run
```

## Application 

docs in yaml format are available under [http://localhost:8080/docs/](http://localhost:8080/docs/)

notice that the default port is 8080 but it can be changed via configuration file.

to test websocket you can use one of free online tools like:
[piesocket](https://www.piesocket.com/websocket-tester)

use the address(you can change value of the number parameter): 

```shell
  http://localhost:8080/ws?number=0
```
