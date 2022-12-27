package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.BuildConfig
import and09.multiweatherapp.HttpRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class OpenWeatherMapAPI private constructor(queryString: String): WeatherAPI {
    private val weatherData: JSONObject

    companion object {
        private const val API_KEY = BuildConfig.OpenWeatherMap_API_KEY
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/weather?id=524901&appid=$API_KEY&"
        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherAPI {
            return OpenWeatherMapAPI("q=" + URLEncoder.encode(locationName, "utf-8"))
        }
        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherAPI {
            return OpenWeatherMapAPI("lat=$lat&lon=$lon")
        }
    }

    @get:Throws(JSONException::class)
    override val temperature: Int
        get() {
            val main = weatherData.getJSONObject("main")
            val tempKelvin = main.getDouble("temp")
            return (tempKelvin - 273.15).toInt()
        }
    @get:Throws(JSONException::class)
    override val description: String
        get() {
            val weather = weatherData.getJSONArray("weather")
            return weather.getJSONObject(0).getString("description")
        }
    @get:Throws(JSONException::class)
    override val iconUrl: String
        get() {
            val weather = weatherData.getJSONArray("weather")
            return "https://openweathermap.org/img/w/${weather.getJSONObject(0).getString("icon")}.png"
        }
    @get:Throws(JSONException::class)
    override val location: String
        get() {
            return weatherData.getString("name")
        }
    override val providerUrl: String
        get() = "https://www.openweathermap.org"
    
    init {
        val result = HttpRequest.request(BASE_URL + queryString)
        weatherData = JSONObject(result)
        println("Webservice: OpenWeatherMap")
        println("Request Url (Query String: $queryString)= ${BASE_URL + queryString}")
        println("JSON= ${weatherData.toString()}") // Debug!
    }
}