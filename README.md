# Projekt Verteilte Systeme - Team Soccer

## Average player speed

**Running:**

    bin/flink run ../testapp/target/soccer-0.1.jar AggregatedPlayerStats "file://[wherever]/full-game"

**Preliminary Results:**
*1s downsampling, one leg tracking*
> [Team A] Nick Gertje (GK):   Distance(2.8km)   Played(62min)   AvgSpeed(2.7km/h)   MaxSpeed(21.4km/h)  
[Team A] Dennis Dotterweich:   Distance(6.4km)   Played(62min)   AvgSpeed(6.2km/h)   MaxSpeed(27.2km/h)  
[Team A] Roman Hartleb:   Distance(7.9km)   Played(62min)   AvgSpeed(7.7km/h)   MaxSpeed(25.3km/h)  
[Team A] Sandro Schneider:   Distance(6.3km)   Played(62min)   AvgSpeed(6.1km/h)   MaxSpeed(30.5km/h)  
[Team A] Wili Sommer:   Distance(6.2km)   Played(62min)   AvgSpeed(6.0km/h)   MaxSpeed(30.0km/h)  
[Team A] Philipp Harlass:   Distance(6.5km)   Played(62min)   AvgSpeed(6.3km/h)   MaxSpeed(26.2km/h)  
[Team A] Erik Engelhardt:   Distance(6.1km)   Played(62min)   AvgSpeed(5.9km/h)   MaxSpeed(37.8km/h)  
[Team A] Niklas Waelzlein:   Distance(5.3km)   Played(62min)   AvgSpeed(5.1km/h)   MaxSpeed(24.6km/h)  
[Team B] Leon Krapf (GK):   Distance(3.4km)   Played(62min)   AvgSpeed(3.2km/h)   MaxSpeed(63.0km/h)  
[Team B] Vale Reitstetter:   Distance(6.4km)   Played(62min)   AvgSpeed(6.2km/h)   MaxSpeed(50.0km/h)  
[Team B] Christopher Lee:   Distance(6.0km)   Played(62min)   AvgSpeed(5.8km/h)   MaxSpeed(34.1km/h)  
[Team B] Leo Langhans:   Distance(6.7km)   Played(62min)   AvgSpeed(6.5km/h)   MaxSpeed(34.7km/h)  
[Team B] Kevin Baer:   Distance(6.4km)   Played(62min)   AvgSpeed(6.2km/h)   MaxSpeed(29.7km/h)  
[Team B] Luca Ziegler:   Distance(6.8km)   Played(62min)   AvgSpeed(6.6km/h)   MaxSpeed(30.4km/h)  
[Team B] Ben Mueller:   Distance(7.0km)   Played(62min)   AvgSpeed(6.8km/h)   MaxSpeed(24.6km/h)  
[Team B] Leon Heinze:   Distance(7.1km)   Played(62min)   AvgSpeed(6.9km/h)   MaxSpeed(25.1km/h)  
[Referee]:   Distance(4.1km)   Played(62min)   AvgSpeed(4.0km/h)   MaxSpeed(34.4km/h)  

## Heatmap

Aggregate the necessary data for the heatmap.
The helper class is `HeatmapData.java`, in which you can also define the resolution of the output data for the heatmap.
```
// 8 X 13 --> 104 cells
// 16 X 25 --> 400 cells
// 32 X 50 --> 1600 cells
// 64 X 100 --> 6400 cells
public static int fieldXResolution = 16;
public static int fieldYResolution = 25;
```

You can find some sample heatmaps in the folder `sample heatmaps`
Run the jobs via

    ./flink run target/soccer-0.1.jar HeatmapBall path/to/full-game ouputpath/for/heatmapdata

Currently, the following 2 jobs are working:
* HeatmapPlayer
* HeatmapBall

### Create a plot from the measurements

If you have collected the data and stored it into the file, run the python script `CreateHeatmap.py`.
It will generate the Heatmap.

**Important:**

The script makes use of the service plot.ly.
For the script to work to have to install the libraries via `sudo pip install plotly` or have a look at the website for you specific platform.
If you are on Linux, you can use my credentials.
Install the library and put the following content in the file `~/.plotly/.credentials`.

```
{
    "username": "whoww", 
    "stream_ids": [], 
    "api_key": "2v7ayto1oy", 
    "proxy_username": "", 
    "proxy_password": ""
}
```

Then just run the script.
