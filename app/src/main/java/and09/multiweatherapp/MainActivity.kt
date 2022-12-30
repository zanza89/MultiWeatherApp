package and09.multiweatherapp

import and09.multiweatherapp.databinding.ActivityMainBinding
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val prefsChangedListener = object : SharedPreferences.OnSharedPreferenceChangeListener {
        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            println("prefs changed, key: $key")
            if (key == "use_gps") {
                val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (prefs.getBoolean(getString(R.string.use_gps), false)) {
                    val providers = locationManager.getProviders(true)
                    if (providers.isNullOrEmpty()) {
                        Log.d(javaClass.simpleName, "erforderliche Berechtigung nicht erteilt.")
                        return
                    }
                    Log.i(javaClass.simpleName, "vefügbare Provider:")
                    providers.forEach {
                        Log.i(javaClass.simpleName, "Provider: $it")
                    }
                    if (providers.contains("gps")) {
                        val providerName = providers.get(providers.indexOf("gps"))
                        try {
                            locationManager.requestLocationUpdates(
                                providerName,
                                4000L,
                                25.0f,
                                locationListener
                            )
                            Log.d(javaClass.simpleName, "Lokalisierung gestartet:")
                            Log.d(
                                javaClass.simpleName,
                                showProperties(locationManager, providerName)
                            )
                        } catch (ex: SecurityException) {
                            Log.e(javaClass.simpleName, "erforderliche Berechtigung ${ex.toString()} nicht erteilt")
                        }
                    }
                } else {
                    locationManager.removeUpdates(locationListener)
                    Log.d(javaClass.simpleName, "Lokalisierung beendet")
                }
            }
        }
    }

    inner class WeatherLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d(javaClass.simpleName, "Empfangene Geodaten:\n$location")
        }

    }

    val locationListener = WeatherLocationListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefs.registerOnSharedPreferenceChangeListener(prefsChangedListener)

        if (prefs.getBoolean(getString(R.string.use_gps), false)) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = locationManager.getProviders(true)
            if (providers.isNullOrEmpty()) {
                Log.d(javaClass.simpleName, "erforderliche Berechtigung nicht erteilt.")
                return
            }
            Log.i(javaClass.simpleName, "vefügbare Provider:")
            providers.forEach {
                Log.i(javaClass.simpleName, "Provider: $it")
            }
            if (providers.contains("gps")) {
                val providerName = providers.get(providers.indexOf("gps"))
                try {
                    locationManager.requestLocationUpdates(
                        providerName,
                        4000L,
                        25.0f,
                        locationListener
                    )
                    Log.d(javaClass.simpleName, "Lokalisierung gestartet:")
                    Log.d(javaClass.simpleName, showProperties(locationManager, providerName))
                } catch (ex: SecurityException) {
                    Log.e(javaClass.simpleName, "erforderliche Berechtigung ${ex.toString()} nicht erteilt")
                }
            }
        }
    }

    private fun showProperties(manager: LocationManager, providerName: String): String {
        val locationProvider = manager.getProvider(providerName)
            ?: return "Kein Provider unter dem Namen $providerName"

        return String.format(
            "Provider: %s\nHorizontale Genauigkeit:" + "%s\nUnterstützt Höhenermittlung: %s\nErfordert Satellit: %s",
            providerName,
            if (locationProvider.accuracy == 1) "FINE" else "COARSE",
            locationProvider.supportsAltitude(),
            locationProvider.requiresSatellite()
        )
    }

}