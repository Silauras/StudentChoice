package khai.edu.StudChoice.Entities.userAccount

import khai.edu.StudChoice.config.DataBaseConfiguration
import khai.edu.StudChoice.domain.Domain
import khai.edu.StudChoice.Entities.userProfile.UserProfile
import java.lang.IllegalArgumentException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

/**
 * Created by Silauras on 18.03.2020
 */
class UserAccount private constructor(
        val email: Email,
        val fullName: FullName,
        val phone: Phone? = null,
        val reserveEmail: Email? = null,
        val userId: UUID = UUID.randomUUID()
) : Domain() {
    fun save() {
        try {
            val connection = getConnection()
            val SQL = "INSERT INTO accounts (id, email, surname, name, patronymic, phone, reserve_email )" +
                    "VALUES(?,?,?,?,?,?,?)"

            val preparedStatement = connection.prepareStatement(SQL)
            var placeHolder: Int = 1

            preparedStatement.setObject(placeHolder++, userId, java.sql.Types.OTHER)
            preparedStatement.setObject(placeHolder++, email.email)
            preparedStatement.setObject(placeHolder++, fullName.surname)
            preparedStatement.setObject(placeHolder++, fullName.name)
            preparedStatement.setObject(placeHolder++, fullName.patronymic ?: "")
            preparedStatement.setObject(placeHolder++, phone?.phoneToString() ?: "")
            preparedStatement.setObject(placeHolder, reserveEmail?.email ?: "")
            preparedStatement.execute()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateEmail(email: Email){
        getConnection().prepareStatement("UPDATE accounts SET email = '${email.email}' WHERE id = '$userId';").executeUpdate()
    }

    fun updateSurname(surname: String){
        getConnection().prepareStatement("UPDATE accounts SET surname = '$surname' WHERE id = '$userId';").executeUpdate()
    }

    fun updateName(name: String){
        getConnection().prepareStatement("UPDATE accounts SET name = '$name' WHERE id = '$userId';").executeUpdate()
    }


    fun updatePatronymic(patronymic: String) {
        val connection = getConnection()
        val SQL = "UPDATE accounts SET patronymic = '$patronymic' WHERE id = '$userId';"
        connection.prepareStatement(SQL).execute()
    }

    //transaction it's not for me xD
    fun updateFullName(fullName: FullName) {
        updateSurname(fullName.surname)
        updateName(fullName.name)
        //java check for null in method with nullsave...
        if (fullName.patronymic.isEmpty())
            updatePatronymic(fullName.patronymic)
    }

    fun updatePhone(phone: Phone) {
        val connection = getConnection()
        val SQL = "UPDATE accounts SET phone = '$phone' WHERE id = '$userId';"
        //I see, IDE warnings for losers
        connection.prepareStatement(SQL).execute()
    }

    fun updateReserveEmail(reserveEmail: String) {
        val connection = getConnection()
        val SQL = "UPDATE accounts SET reserve_email = '$reserveEmail' WHERE id = '$userId';"
        connection.prepareStatement(SQL).execute()
    }

    fun updateAll(user: UserAccount) {
        updateEmail(user.email)
        updateFullName(fullName)

        //read about elvis operator on the Kotlin
        if (user.phone != null)
            updatePhone(user.phone)
        if (user.reserveEmail != null)
            updateReserveEmail(user.reserveEmail.email)
    }

    fun findProfiles(): List<UserProfile>? {
        return UserProfile.findByUserOwner(this)
    }

    companion object {
        private fun getConnection(): Connection {
            return DriverManager.getConnection(
                    DataBaseConfiguration.DB_URL,
                    DataBaseConfiguration.DB_USER,
                    DataBaseConfiguration.DB_PASSWORD)
        }

        fun fromDataBaseBySQL(quest: String): UserAccount? {

            val sql = "SELECT * FROM accounts WHERE $quest"
            //duplicates... I love it mb better some duplicate code move to the separate methods.
            //Also, A lot of dublicates of code I see in firsts methods too
            val statement = getConnection().createStatement()
            val resultSet = statement.executeQuery(sql)

            var userId: UUID? = null
            var email: Email? = null
            var fullName: FullName? = null
            var phone: Phone? = null
            var reserveEmail: Email? = null

            while (resultSet.next()) {
                userId = UUID.fromString(resultSet.getString("id"))
                email = Email(resultSet.getString("email"))
                fullName = FullName(resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("patronymic"))

                //isEmpty() method? and also elvis operator
                if (resultSet.getString("phone") == "") {
                    phone = null
                } else {
                    phone = Phone.getPhoneFromString(resultSet.getString("phone"))
                }
                reserveEmail = Email(resultSet.getString("reserve_email"))
            }
            if (userId != null && email != null) {
                //use !! after check for null...for losers, I remembered
                val user = Builder().userId(userId).email(email).fullName(fullName!!)
                if (phone != null) {
                    user.phone(phone)
                }
                if (reserveEmail != null) {
                    user.reserveEmail(reserveEmail)
                }
                return user.build()
            } else {
                return null
            }
        }

        fun listFromDataBaseBySQL(quest: String): List<UserAccount>? {

            val sql = "SELECT * FROM accounts WHERE " + quest
            val statement = getConnection().createStatement()
            val resultSet = statement.executeQuery(sql)

            var userId: UUID? = null
            var email: Email? = null
            var fullName: FullName? = null
            var phone: Phone? = null
            var reserveEmail: Email? = null
            //WHY???
            val list = ArrayList<UserAccount>()

            while (resultSet.next()) {
                userId = UUID.fromString(resultSet.getString("id"))
                email = Email(resultSet.getString("email"))
                fullName = FullName(resultSet.getString("name"),
                        resultSet.getString("surname"),
                        resultSet.getString("patronymic"))
                if (resultSet.getString("phone") == "") {
                    phone = null
                } else {
                    phone = Phone.getPhoneFromString(resultSet.getString("phone"))
                }
                reserveEmail = Email(resultSet.getString("reserve_email"))
                //so returned for best practices by Olego
                if (userId != null && email != null) {
                    val user = Builder()
                    user.userId(userId!!).email(email!!).fullName(fullName!!)

                    if (phone != null) {
                        user.phone(phone)
                    }
                    if (reserveEmail != null) {
                        user.reserveEmail(reserveEmail)
                    }
                    list.add(user.build())
                }
            }

            if (!list.isEmpty()) {
                return list.toList()
            } else {
                return null
            }
        }

        fun findByUserID(userId: UUID): UserAccount? {

            return fromDataBaseBySQL("id = '$userId'")

        }

        fun findByEmail(email: Email): UserAccount? {
            return fromDataBaseBySQL("email = '${email.email}'")
        }

        fun findByFullName(fullName: FullName): List<UserAccount>? {
            if (fullName.patronymic != "")
                return listFromDataBaseBySQL("name = '" + fullName.name +
                        "' , surname =" + fullName.surname +
                        "' , patronymic = '" + fullName.patronymic + "'")
            else
                return listFromDataBaseBySQL("name = '" + fullName.name + "' , surname = '" + fullName.surname + "'")
        }


        //mb better create one method for searches?
        fun findByName(name: String): List<UserAccount>? {
            return listFromDataBaseBySQL("name = '$name'")
        }

        fun findBySurname(surname: String): List<UserAccount>? {
            return listFromDataBaseBySQL("surname = '$surname'")
        }

        fun findByPatronymic(patronymic: String): List<UserAccount>? {
            return listFromDataBaseBySQL("patronymic = '$patronymic'")
        }

        fun findByPhone(phone: Phone): List<UserAccount>? {
            return listFromDataBaseBySQL("phone = '$phone'")
        }

        fun findByReserveEmail(email: Email): List<UserAccount>? {
            return listFromDataBaseBySQL("reserve_email '= $email'")
        }
    }

    data class Builder(
            var email: Email? = null,
            var fullName: FullName? = null,
            var phone: Phone? = null,
            var reserveEmail: Email? = null,
            var userId: UUID? = null) {
        fun email(email: Email) = apply { this.email = email }
        fun fullName(fullName: FullName) = apply { this.fullName = fullName }
        fun phone(phone: Phone?) = apply { this.phone = phone }
        fun reserveEmail(reserveEmail: Email) = apply { this.reserveEmail = reserveEmail }
        fun userId(userId: UUID) = apply { this.userId = userId }
        fun build(): UserAccount {
            if (email == null || fullName == null) {
                throw IllegalArgumentException("UserAccount must have email and full name data");
            }
            return UserAccount(email!!, fullName!!, phone, reserveEmail, userId ?: UUID.randomUUID())

        }
    }

}