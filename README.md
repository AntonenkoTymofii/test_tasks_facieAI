# Test Task Facie AI

**Task started 25.02.25 at 20:00**

**Task finished 27.02.25 at 13:00**

## How to run project
To run the project, you need to clone the repository from GitHub and 
run it with the ```gradlew``` run command to run the tests, 
run the ```gradlew test``` command

## How to use API
To use the API, use the following commands on the command line:
This one is needed to load products into the cache

```
curl -X POST “http://localhost:8080/api/v1/enrich/upload-products” \
-H “Content-Type: multipart/form-data” \
-F “file=@D:/homework/test_tasks_facieAI/largeSizeProduct.csv”
```

This one reads the sales file and loads the name and date via id:

```
curl -X POST “http://localhost:8080/api/v1/enrich” -H “Content-Type: multipart/form-data” -F “file=@D:/homework/test_tasks_facieAI/middleSizeTrade.csv”
```

## Limits
The code has a limit on the size of files that can be read, 
but you can extend this limit if necessary.

## Design
I used the MVC pattern for easier understanding of the code. 
I tried to follow the principles of SOLID. You can also implement the 
GoF Strategy pattern.

## Conclusion and ideas
In general, if we talk about improvements, if I had more time, 
I could think, for example, about processing additional file types, 
writing new types of tests (integration, load). We could also add new 
patterns to make the code easier to read and to make the logic more visible.


