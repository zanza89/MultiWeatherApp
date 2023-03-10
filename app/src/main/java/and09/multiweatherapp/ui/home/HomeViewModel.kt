package and09.multiweatherapp.ui.home

import and09.multiweatherapp.R
import and09.multiweatherapp.weatherapi.*
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.FileNotFoundException
import java.net.ConnectException
import java.net.URL
import java.net.UnknownHostException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _location: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val location: LiveData<String> = _location

    private val _iconBitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }
    val iconBitmap: LiveData<Bitmap> = _iconBitmap

    private val _temperature: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val temperature: LiveData<String> = _temperature

    private val _provider: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val provider: LiveData<String> = _provider

    private val _description: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val description: LiveData<String> = _description

    fun retrieveWeatherData() {
        CoroutineScope(Dispatchers.Main).launch {
            var weather : WeatherAPI? = null
            var bitmap : Bitmap? = null
            var errorMessage = ""
            withContext(Dispatchers.IO) {
                val app = getApplication() as Application
                val prefs = PreferenceManager.getDefaultSharedPreferences(app)
                val locationName = prefs.getString(app.getString(R.string.location_name), "Berlin")
                val providerClassName = prefs.getString(app.getString(R.string.weather_provider), OpenWeatherMapAPI.Values.NAME)
                try {
                    // Reflection API (dependencies n??tig) "meta- programming" -> Vorteil hinzuf??gen neuer Wetterdienste ohne retrieveWeatherData() ??ndern/erg??nzen zu m??ssen
                    val cls = Class.forName("${WeatherAPI::class.java.`package`?.name}.$providerClassName").kotlin
                    val func = cls.companionObject?.declaredFunctions?.find { it.hasAnnotation<FromLocationName>() }
                    val locationManager = app.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val providers = locationManager.getProviders(true)
                    // Server Adresse f??r die SpringWeather API setzen
                    if (providerClassName == "SpringWeatherAPI") {
                        val serverAdress = prefs.getString(app.getString(R.string.server_adress), "unknown")
                        SpringWeatherAPI.setServerAdress(serverAdress)
                    }
                    // Bei Aktivierung von automatischer Standortbestimmung
                    if (prefs.getBoolean(app.getString(R.string.use_gps), false)) {
                        var lastLocation: Location? = null
                        if (providers.isNullOrEmpty()) {
                            Log.d(javaClass.simpleName, "erforderliche Berechtigung nicht erteilt.")
                            return@withContext
                        }
                        if (providers.contains("gps")) {
                            val providerName = providers.get(providers.indexOf("gps"))
                            try {
                                lastLocation = locationManager.getLastKnownLocation(providerName)
                            } catch (ex: SecurityException) {
                                Log.e(javaClass.simpleName, "erforderliche Berechtigung ${ex.toString()} nicht erteilt")
                            }
                            if (lastLocation != null) {
                                val func = cls.companionObject?.declaredFunctions?.find { it.hasAnnotation<FromLatLon>() }
                                weather = func?.call(cls.companionObjectInstance, lastLocation.latitude, lastLocation.longitude) as WeatherAPI
                                Log.d(javaClass.simpleName, "LastknownLocation = ${lastLocation.latitude},${lastLocation.longitude}")
                            }
                        }
                    }
                    // Fall keine Geo-Daten vorliegen
                    if (weather == null) {
                        weather = func?.call(cls.companionObjectInstance, locationName) as WeatherAPI
                    }

                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Description: ${weather?.description}")
                    Log.d(javaClass.simpleName, "Icon-URL: ${weather?.iconUrl}")
                    Log.d(javaClass.simpleName, "Provider: ${weather?.providerUrl}")
                    val iconUrl = URL(weather?.iconUrl) // java.net!
                    val inputStream = iconUrl.openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                } catch (ex: Exception) {
                    Log.e(javaClass.simpleName, ex.cause.toString())
                    errorMessage = when(ex.cause) {
                        is UnknownHostException -> "Keine Internetverbindung. Bitte ??berpr??fen Sie Ihre Netzwerkeinstellungen"
                        is ConnectException -> "Netzwerkdienst antwortet nicht. Bitte schalten Sie auf einen anderen Dienst um"
                        is FileNotFoundException -> "Es wurde kein Datum zum gew??hlten Standort zur??ckgeliefert"
                        else -> "Unbekannter Fehler"
                    }
                }
            }
            if (weather != null) updateValues(weather, bitmap)
            else Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun updateValues(weather: WeatherAPI?, bitmap: Bitmap?) {
        try {
            _location.value = weather?.location
            _temperature.value = "${weather?.temperature} ??C"
            _description.value = weather?.description
            _provider.value = weather?.providerUrl
            _iconBitmap.value = bitmap
        } catch (ex: JSONException) {
            Log.e(javaClass.simpleName, ex.toString())
        }
    }
}