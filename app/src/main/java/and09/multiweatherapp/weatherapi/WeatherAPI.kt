package and09.multiweatherapp.weatherapi

import org.json.JSONException
annotation class FromLocationName
annotation class FromLatLon
interface WeatherAPI {
    @get:Throws(JSONException::class)
    val temperature: Int

    @get:Throws(JSONException::class)
    val description: String

    @get:Throws(JSONException::class)
    val iconUrl: String

    @get:Throws(JSONException::class)
    val location: String

    val providerUrl: String
}