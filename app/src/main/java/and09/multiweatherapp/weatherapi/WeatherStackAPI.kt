package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.BuildConfig
import and09.multiweatherapp.HttpRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLEncoder

class WeatherStackAPI private constructor(queryString: String) :WeatherAPI {
    private val weatherData: JSONObject
    companion object {
        private const val API_KEY = BuildConfig.WeatherStack_API_KEY
        private const val BASE_URL = "http://api.weatherstack.com/current?access_key=$API_KEY&"

        @FromLocationName
        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherStackAPI {
            return WeatherStackAPI("query=" + URLEncoder.encode(locationName, "UTF-8"))
        }
        @FromLatLon
        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherStackAPI {
            return WeatherStackAPI("query=$lat,$lon")
        }
    }

    @get:Throws(JSONException::class)
    override val temperature: Int
        get() {
            val weather = weatherData.getJSONObject("current")
            return weather.getInt("temperature")
        }
    @get:Throws(JSONException::class)
    override val description: String
        get() {
            val weather = weatherData.getJSONObject("current")
            return weather.getJSONArray("weather_descriptions").getString(0)
        }
    @get:Throws(JSONException::class)
    override val iconUrl: String
        get() {
            val weather = weatherData.getJSONObject("current")
            return weather.getJSONArray("weather_icons").getString(0)
        }
    @get:Throws(JSONException::class)
    override val location: String
        get() {
            return weatherData.getJSONObject("location").getString("name")
        }
    override val providerUrl: String
        get() = "https://www.weatherstack.com"

    init {
        val result = HttpRequest.request(BASE_URL + queryString)
        weatherData = JSONObject(result)
        println("Webservice: WeatherStack")
        println("Request Url (Query String: $queryString)= ${BASE_URL + queryString}")
        println("JSON= ${weatherData.toString()}")
        if (weatherData.has("success") && !weatherData.getBoolean("success")) throw FileNotFoundException()
    }
}