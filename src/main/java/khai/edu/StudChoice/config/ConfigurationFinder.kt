package khai.edu.StudChoice.config

import java.util.*

/**
 * Created by Silauras on 18.03.2020
 */
class ConfigurationFinder {
    companion object {
        private val filename = "application.properties";
        private val properties: Properties = Properties();

        fun getProperty(propetryName: String): String {
            javaClass.classLoader.getResourceAsStream(filename).use { my ->
                properties.load(my)
            }
            return properties.getProperty(propetryName).toString()
        }
    }
}