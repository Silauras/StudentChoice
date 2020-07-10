package khai.edu.studChoice.config

import java.util.*

/**
 * Created by Silauras on 09.07.2020 at 12:18
 */
abstract class ConfigurationFinder(private val filename: String) {

    private val properties = Properties()

    fun getProperty(propetryName: String): String {
        javaClass.classLoader.getResourceAsStream(filename).use { my ->
            properties.load(my)
        }
        return properties.getProperty(propetryName)
    }
}