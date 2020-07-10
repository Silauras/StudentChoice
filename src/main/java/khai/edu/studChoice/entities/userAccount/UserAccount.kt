package khai.edu.studChoice.entities.userAccount

import khai.edu.studChoice.config.DataBaseConfiguration
import khai.edu.studChoice.config.UserAccountSQLScripts
import khai.edu.studChoice.domain.Domain
import khai.edu.studChoice.entities.userProfile.UserProfile
import java.lang.IllegalArgumentException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*
import java.io.*

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
            val preparedStatement = getConnection().prepareStatement(UserAccountSQLScripts.SAVE)
            var placeHolder = 1

            preparedStatement.setObject(placeHolder++, userId, java.sql.Types.OTHER)
            preparedStatement.setObject(placeHolder++, email.toString())
            preparedStatement.setObject(placeHolder++, fullName.surname)
            preparedStatement.setObject(placeHolder++, fullName.name)
            preparedStatement.setObject(placeHolder++, fullName.patronymic)
            preparedStatement.setObject(placeHolder++, phone?.phoneToString() ?: "")
            preparedStatement.setObject(placeHolder, reserveEmail ?: "")
            preparedStatement.execute()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    public fun updateEmail(email: Email) {
        getConnection().prepareStatement(UserAccountSQLScripts.UPDATE_EMAIL).executeUpdate()
    }

    public fun updateSurname(surname: String) {
        val sql = UserAccountSQLScripts.UPDATE_SURNAME.replace("{surname}", surname).replace("{id}", userId.toString())
        getConnection().prepareStatement("UPDATE account SET surname = '$surname' WHERE id = '$userId';").executeUpdate()
    }

    public fun updateName(name: String) {
        getConnection().prepareStatement("UPDATE account SET name = '$name' WHERE id = '$userId';").executeUpdate()
    }

    public fun updatePatronymic(patronymic: String) {
        getConnection().prepareStatement("UPDATE account SET patronymic = '$patronymic' WHERE id = '$userId';").executeUpdate()
    }

    fun updateFullName(fullName: FullName) {
        getConnection().prepareStatement("UPDATE account SET name = '${fullName.name}', surname = '${fullName.surname}', patronymic = '${fullName.patronymic}' WHERE id = '$userId';").executeUpdate()
    }

    fun updatePhone(phone: Phone) {
        getConnection().prepareStatement("UPDATE account SET phone = '$phone' WHERE id = '$userId';").executeUpdate()
    }

    fun updateReserveEmail(reserveEmail: String) {
        getConnection().prepareStatement("UPDATE account SET reserve_email = '$reserveEmail' WHERE id = '$userId';").execute()
    }

    fun updateAll(user: UserAccount) {
        getConnection().prepareStatement("UPDATE account SET email = '${user.email}', name = '${user.fullName.name}', surname = '${user.fullName.surname}' , patronymic = '${user.fullName.patronymic}' , phone = '${user.phone}' , reserve_email = '${user.reserveEmail}' WHERE id = '$userId';").executeUpdate()
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

            val sql = "SELECT * FROM account WHERE $quest"
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

            val sql = "SELECT * FROM account WHERE $quest"
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
                phone = if (resultSet.getString("phone") == "") {
                    null
                } else {
                    Phone.getPhoneFromString(resultSet.getString("phone"))
                }
                reserveEmail = Email(resultSet.getString("reserve_email"))
                //so returned for best practices by Olego
                if (userId != null) {
                    val user = Builder()
                    user.userId(userId).email(email).fullName(fullName)
                    if (phone != null) {
                        user.phone(phone)
                    }
                    user.reserveEmail(reserveEmail)
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