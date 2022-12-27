package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.HttpRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class SpringWeatherAPI private constructor(queryString: String) : WeatherAPI {
    private val weatherData: JSONObject
    companion object {
        private const val BASE_URL = "http://localhost:8080/weather?"
        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherAPI {
            return SpringWeatherAPI("location" + URLEncoder.encode(locationName, "UTF-8"))
        }
        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherAPI {
            return SpringWeatherAPI("query=$lat,$lon")
        }
    }

    @get:Throws(JSONException::class)
    override val temperature: Int
        get() {
            return  weatherData.getInt("temperature")
        }

    @get:Throws(JSONException::class)
    override val description: String
        get() {
            return weatherData.getString("description")
        }

    @get:Throws(JSONException::class)
    override val location: String
        get() {
            return weatherData.getString("locationName")
        }

    @get: Throws(JSONException::class)
    override val iconUrl: String
        get() {
            return weatherData.getString("iconUrl")
        }

    @get:Throws(JSONException::class)
    override val providerUrl: String
        get() {
            return weatherData.getString("providerUrl")
        }

    init {
        val result = HttpRequest.request(BASE_URL + queryString)
        weatherData = JSONObject(result)
        println(weatherData.toString())
    }
}