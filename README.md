# IMDB top 1000 movie search engine

This is a search engine implemented based on Lucene. You can search top 1000 movies all the time on this search engine.

### How to run it

first you should download this project to your own machine. This project is based on Spring boot and Maven. So please make sure your environment can run this kind of program. 

Then please run the main function in the "GetDataService.java" class. It will help you to build the index on your local machine. 

Then you can start the spring boot and visit this website on "localhost:8080"



### Further improvement

1. improve the front-end, change the color of the background and the size of the search bar. Also change the layout of the search result please.
2. improve the size of the data. Now, I only use top250 as the dataset to do search. Please enlarge it to 1000. The way to do it is to get the top 1000 film name and save it as .txt file. Each name should be in one line. then replace the "data/top250.txt" with this file and also change the corresponding path in "Path.java" and run the main function again to get the index.
3. Try to improve the search algorithm. For example, try to improve the sort of search result.
