# User Search.

Accessing the application.

The application is written in Java11 and built with Maven.

```bash
mvn clean install
java -jar target/user-search-1.jar
```

I  made use of Lombok for reducing boilerplate code. Used Lombok previously at DWP in Blackpool so assumed that
it was ok to use it for this assessment.  You may need to add the plugin for Lombok in your IDE if you don't already have enabled.

### Once running - two ways you to access the application

#### 1) Access the URL's directly 

- http://localhost:8080/user/city/London
- http://localhost:8080/user/city/London?milesRadius=50

#### 2) Swagger hub
The following URL will output the OpenAPI swagger UI.  Similar interface is available to the provided bpdts-test-app.herokuapp.com
Once the application is running you can access it on this url.

- http://localhost:8080/

## Notes / findings

The following url
https://bpdts-test-app.herokuapp.com/city/London/users

Returns users where latitude and longitude does not map to London? However for purpose of this test I assumed that
they do map to London and its just because the data is anonymised.

e.g. 
```json
{
"id": 135,
"first_name": "Mechelle",
"last_name": "Boam",
"email": "mboam3q@thetimes.co.uk",
"ip_address": "113.71.242.187",
"latitude": -6.5115909,
"longitude": 105.652983
}
```

#### Another finding
https://bpdts-test-app.herokuapp.com/user/1

```json
{
"id": 1,
"first_name": "Maurise",
"last_name": "Shieldon",
"email": "mshieldon0@squidoo.com",
"ip_address": "192.57.232.111",
"latitude": 34.003135,
"longitude": -117.7228641,
"city": "Kax"
}
```

Says that the city is in Kax but actually is near Los Angeles. For purpose of this test I assumed that the lat and long
are correct and ignored the city name.

# Design Decisions and Reasoning

When approaching this there were design decisions to be made such as. 

- Do we assume that the calling service already knows what the long and lat is for London? If not
how do we store the coordinates of London?
- Do we store a list of cities with matching coordinates in key value map?  
- Do we just include one city i.e. only support London.
- Is it 50 miles from the centre of London?  Or 50 miles from the edge / anywhere within London.
  
I opted to count the radius as 50 miles from the centre of London using google to determine where the centre is.
Also opted to go for an in memory h2 database.  This to me felt like it would offer the most flexibility and separates the
data from the application. If you wanted to add more cities you would then just add a row in the database. Also you can
search for names that are similar such as "London" and "london". Plus I also wanted to demonstrate that 
I understand Hibernate JPA etc. 

### Where exactly are the latitude and longitude of London?  
There are several open geo data sources that could be used.  I opted to preload coordinates from Google Maps.
I used google maps because its used by billions of people every day and I have high degree of confidence in the data.  
I also felt it would be useful to visually verify results and build up some test cases.
 
Please see `create-cities-geodata.py` for python script I used for getting London and other cities Geo coordinates.

### Testing

When producing the code I followed a TDD approach.  Adding the failing tests upfront and expanded out as I progressed.
I also added Integration test brings an end to end test with a stubbed heroku http server.  

To ensure code coverage I also added jacoco.  After running `mvn clean install`
you will be able to access the coverage report in `target/jacoco/site/index.html`.  Test coverage is currently at 97%.

