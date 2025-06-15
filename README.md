# AirPollutionMonitor

AirPollutionMonitor is an application to monitor air pollution from [API of Environmental Protection Administration (EPA) in Taiwan](https://data.epa.gov.tw/api/v2/aqx_p_432?limit=1000&api_key=cebebe84-e17d-4022-a28f-81097fda5896&sort=ImportDate%20desc&format=json).

### Upper List ###
Show data with PM2.5 <= 10 in horizontal.  
### Lower List ###
Show data with PM2.5 > 10 in vertical.  
When status = "良好", show "The status is good, we want to go out
to have fun".  
When status != "良好", show button and show toast when pressing it.  
### Search View ###
Filter lower list with keyword

## Used Technique ##
- Kotlin
- MVVM
- Coroutine & Flow
- Compose UI
- Retrofit
- ViewBinding
- Sealed Class

## Link ##
- [Video Demo](https://www.youtube.com/watch?v=LZVLlldTSNM)
