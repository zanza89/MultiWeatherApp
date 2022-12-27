package and09.multiweatherapp.weatherapi

import and09.multiweatherapp.BuildConfig
import and09.multiweatherapp.HttpRequest
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder

class WeatherStackAPI private constructor(queryString: String) {
    private val weatherData: JSONObject
    companion object {
        private const val API_KEY = BuildConfig.WeatherStack_API_KEY
        private const val BASE_URL = "http://api.weatherstack.com/current?access_key=$API_KEY&"

        @Throws(IOException::class, JSONException::class)
        fun fromLocationName(locationName: String?): WeatherStackAPI {
            return WeatherStackAPI("query=" + URLEncoder.encode(locationName, "UTF-8"))
        }

        @Throws(IOException::class, JSONException::class)
        fun fromLatLon(lat: Double, lon: Double): WeatherStackAPI {
            return WeatherStackAPI("query=$lat,$lon")
        }
    }
    init {
        val result = HttpRequest.request(BASE_URL + queryString)
        weatherData = JSONObject(result)
        println("Webservice: WeatherStack")
        println("Request Url (Query String: $queryString)= ${BASE_URL + queryString}")
        println("JSON= ${weatherData.toString()}")
    }
}