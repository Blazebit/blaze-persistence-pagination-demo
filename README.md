# Blaze-Persistence pagination demo

This project demonstrates how to use Blaze-Persistence to implement a paginated datatable with PrimeFaces.

## Run with docker

It is very easy to run the demo with docker. Just run

    mvn package
    
which will build the project and bundle it into a docker image.

After that you just run 

    mvn docker:start

to start the image. 
The application should then be reachable at `http://<DOCKER_HOST>:8080/pagination-demo`.