# CityFit 
The main aim of the application is to create an incentive for city people to be more active by generating a city wise leaderboard by tracking human activities from their smartphones. It also uses Weather API to get city wise weather details. Both human data and weather data are stored on cloud, the idea behind this to generate data so that organisations could use it to understand how activity is related to weather conditions. 
 
### Data fusion and analysis data 
To gather data, I am using two APIs. Google’s Activity Recognition API and OpenWeatherMap API. Both are handled differently. 
 
###### For Google Activity Recognition API: 
For this I have coded a Background Detector Service class which detects activity on change. From this I extract the activity using Detector Service Class and store it on Firestore. 
 
With this, I also continuously gather data for a particular interval of time which I then add and store it on cloud all together under respective user’s document. The reason for this is to reduce network calls which will in return optimize battery usage.This data is divided into different fields like Walking, Running, Cycling, Driving, On Foot, On Bicycle, Still, Tilting. And then I update these fields as activities are detected. To calculate time I have used Hash Maps which helps me easily map seconds to each activity types. 
 
####### For OpenWeatherMap API : 
I am using a HttpClient service, that helps me connect to the URL using my API key and current city I am in, and then I use a JSON Parser file in which I have defined the structure of response to parse every field and use it in my app. 
 

###### Data fusion and analysis algorithm implementation
The data was stored on Firestore in a format where a user’s data was stored under its own city document. Which helps generate leaderboard city wise. The Dashboard of the app uses both weather and user data and shows the current status that is Current temperature and total minutes active for the day. Wind levels, Clouds, Humidity, city name and condition description. The aim of this is for the user to also get a visualisation of how they perform in different weather conditions. 

### An overview 


 
### Data visualisation and actuation
 
There three main activities/pages in the App.
Dashboard : Visualisation of current weather current and Total minutes spent during the day. 
Leaderboard : List of all city users (using this app) and their active minutes and rank. 
Track activity : Details descriptions of the activity. (View Track details)
 
Data visualisation and/or actuation 
 
Allowing multiple user instances. 
 
Login  						Registration 
          
 
 
            
 
 
 
 
 
 
 
 
 
 
 
 

 
The main aim for this task was to be make it easier for user to see detailed division of their spent minutes on different activities. So I created a new tab, where a user can see report of different activities performed throughout the data. 
Another feature I added was allowing more than one city to use the app. That is now, this app can be used in any city and people will be able to see leaderboard of the city they are in based on their location. 
 

 
