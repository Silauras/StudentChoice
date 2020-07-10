package khai.edu.studChoice.config

object DataBaseConfiguration : ConfigurationFinder("application.properties") {

    val DB_NAME     : String = getProperty("DB_NAME")
    val URL         : String = getProperty("URL")
    val DB_URL      : String = "$URL/$DB_NAME"
    val DB_USER     : String = getProperty("DB_USER")
    val DB_PASSWORD : String = getProperty("DB_PASSWORD")

}