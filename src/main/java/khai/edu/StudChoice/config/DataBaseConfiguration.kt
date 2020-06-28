package khai.edu.StudChoice.config

import khai.edu.StudChoice.config.ConfigurationFinder.Companion.getProperty

object DataBaseConfiguration {
    val DB_NAME = getProperty("DB_NAME")
    val URL = getProperty("URL")
    val DB_URL = URL + "/" + DB_NAME
    val DB_USER = getProperty("DB_USER")
    val DB_PASSWORD = getProperty("DB_PASSWORD")

}