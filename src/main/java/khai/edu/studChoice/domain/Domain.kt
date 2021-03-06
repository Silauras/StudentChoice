package khai.edu.studChoice.domain

import khai.edu.studChoice.config.DataBaseConfiguration
import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by Silauras on 18.03.2020
 */
abstract class Domain {
    protected fun getConnection(): Connection {
        return DriverManager.getConnection(
                DataBaseConfiguration.DB_URL,
                DataBaseConfiguration.DB_USER,
                DataBaseConfiguration.DB_PASSWORD)
    }
}