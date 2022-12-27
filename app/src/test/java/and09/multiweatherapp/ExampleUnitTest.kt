package and09.multiweatherapp

import and09.multiweatherapp.weatherapi.OpenWeatherMapAPI
import and09.multiweatherapp.weatherapi.WeatherStackAPI
import org.json.JSONException
import org.junit.Test
import java.io.IOException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    @Throws(IOException::class, JSONException::class)
    fun openWeatherMap_getResponseFromName() {
        val api = OpenWeatherMapAPI.fromLocationName("Würzburg")
        println("Temp: ${api.temperature}")
        println("Description: ${api.description}")
        println("Icon: ${api.iconUrl}")
        println("Location: ${api.location}")
        println("Service Provider: ${api.providerUrl}")
        println()
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun openWeatherMap_getResponseFromLatLon() {
        val api = OpenWeatherMapAPI.fromLatLon(37.77, -122.42) // San Francisco
        println("Temp: ${api.temperature}")
        println("Description: ${api.description}")
        println("Icon: ${api.iconUrl}")
        println("Location: ${api.location}")
        println("Service Provider: ${api.providerUrl}")
        println()
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun openWeatherStack_getResponseFromName() {
        val api = WeatherStackAPI.fromLocationName("Berlin")
        println()
    }

    @Test
    @Throws(IOException::class, JSONException::class)
    fun openWeatherStack_getResponseFromLatLon() {
        val api = WeatherStackAPI.fromLatLon(37.77, -122.42) //San Francisco
        println()
    }
}