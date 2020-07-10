package khai.edu.studChoice.config

/**
 * Created by Silauras on 09.07.2020 at 12:27
 */
object UserAccountSQLScripts : SQLScripts() {
    val SAVE                 : String  = getProperty("saveUserAccount")
    val UPDATE_EMAIL         : String  = getProperty("updateUserAccountEmail")
    val UPDATE_SURNAME       : String  = getProperty("updateUserAccountSurname")
    val UPDATE_NAME          : String  = getProperty("updateUserAccountName")
    val UPDATE_PATRONYMIC    : String  = getProperty("updateUserAccountPatronymic")
    val UPDATE_FULLNAME      : String  = getProperty("updateUserAccountFullName")
    val UPDATE_PHONE         : String  = getProperty("updateUserAccountPhone")
    val UPDATE_RESERVE_EMAIL : String  = getProperty("updateUserAccountReserveEmail")
    val UPDATE_ALL           : String  = getProperty("updateUserAccountAll")
}