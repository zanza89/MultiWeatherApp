package and09.multiweatherapp

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

class HttpRequest {
    companion object {
        @Throws(IOException::class)
        fun request(urlString: String): String {
            val url = URL(urlString)
            val connection = url.openConnection()
            val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
            val builder = StringBuilder()
            reader.forEachLine {
                builder.append(it)
            }
            reader.close()
            return builder.toString()
        }
    }
}