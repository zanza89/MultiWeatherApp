package and09.multiweatherapp.ui.home

import and09.multiweatherapp.weatherapi.OpenWeatherMapAPI
import and09.multiweatherapp.weatherapi.WeatherAPI
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class HomeViewModel : ViewModel() {

    /*private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text*/

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
            withContext(Dispatchers.IO) {
                try {
                    weather = OpenWeatherMapAPI.fromLocationName("Berlin")
                    Log.d(javaClass.simpleName, "Temp: ${weather?.temperature}")
                    Log.d(javaClass.simpleName, "Description: ${weather?.description}")
                    Log.d(javaClass.simpleName, "Icon-URL: ${weather?.iconUrl}")
                    Log.d(javaClass.simpleName, "Provider: ${weather?.providerUrl}")
                    val iconUrl = URL(weather?.iconUrl) // java.net!
                    val inputStream = iconUrl.openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream.close()
                } catch (ex: Exception) {
                    Log.e(javaClass.simpleName, ex.toString())
                }
            }
            updateValues(weather, bitmap)
        }
    }

    fun updateValues(weather: WeatherAPI?, bitmap: Bitmap?) {
        _location.value = weather?.location
        _temperature.value = "${weather?.temperature} °C"
        _description.value = weather?.description
        _provider.value = weather?.providerUrl
        _iconBitmap.value = bitmap
    }
    /*fun doAction() {
        CoroutineScope(Dispatchers.Main).launch() {
            while(true) {
                _text.value = java.util.Date().toString()
                delay(1000L)
            }
        }
        Log.d(javaClass.simpleName, "Nach Aufruf von doAction()")
    }*/
}