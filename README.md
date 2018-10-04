ABOUT: 
- Android application that displays the weather of a location based on either city, zipcode, or current location. Uses maps to show location of weather.

INSTALLING: 
- You will need to get an api key for "Maps SDK for Android" and enter that key into res/values/string/google_location_key field. Example: API KEY HERE 
- You will need an api key for OpenWeatherMaps and enter that key into res/values/string/open_weather_key field. Example: API KEY HERE

API's:
- OpenWeatherMap JSON API for the weather information. 
- Volley API for the REST calls. 
- Google Maps API for the maps. 
- Google Location API for the location services.

IMPROVEMENTS: 
- Update UI to make it look better. It is terribly ugly right now.
- Change the DisplayDataActivity and EnterLocationActivity activities into fragments for smoother transitions. 
- Add "Place Autocomplete" to the city input field to autocomplete the city. - Testing needs to be done.

ISSUES: 
- OpenWeatherMap has inaccurate information. It was sunny outside and it stated it was currently "light rain." 
- Place Autocomplete wasn't working when I implemented it. I added the fragment and Java code and it would open and close right away. I disregarded it for now.
