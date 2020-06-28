package khai.edu.StudChoice.Entities.userProfile


import khai.edu.StudChoice.config.DataBaseConfiguration
import khai.edu.StudChoice.domain.Domain
import khai.edu.StudChoice.Entities.userAccount.UserAccount
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Types
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Silauras on 09.04.2020
 */
class UserProfile private constructor(
        val department: String,
        val role: Role,
        val subRole: SubRole?,
        val userOwner: UserAccount,
        val profileID: UUID
) : Domain() {

    fun save() {
        try {
            val connection = getConnection()
            val SQL = "INSERT INTO profiles(id, department, role, subrole, owner_id)VALUES(?,?,?,?,?)"

            val preparedStatement = connection?.prepareStatement(SQL)
            var placeholder = 1

            preparedStatement.setObject(placeholder++, profileID, Types.OTHER)
            preparedStatement.setObject(placeholder++, department)
            preparedStatement.setObject(placeholder++, role, Types.OTHER)
            preparedStatement.setObject(placeholder++, subRole, Types.OTHER)
            preparedStatement.setObject(placeholder++, userOwner.userId, Types.OTHER)

            preparedStatement.execute()

        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateDepartment(department: String) {
        try {
            val connection = getConnection()
            val SQL = "UPDATE profiles SET department = '$department' WHERE id = '$profileID';"
            connection.prepareStatement(SQL).execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateRole(role: Role) {
        try {
            val connection = getConnection()
            val SQL = "UPDATE profiles SET department = '$role' WHERE id = '$profileID';"
            connection.prepareStatement(SQL).execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateSubRole(subRole: SubRole) {
        try {
            val connection = getConnection()
            val SQL = "UPDATE profiles SET department = '$subRole' WHERE id = '$profileID';"
            connection.prepareStatement(SQL).execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateUserOwner(userOwner: UserAccount) {
        try {
            val connection = getConnection()
            val SQL = "UPDATE profiles SET department = '" + userOwner.userId + "' WHERE id = '$profileID';"
            connection.prepareStatement(SQL).execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateProfileID(profileID: UUID) {
        try {
            val connection = getConnection()
            val SQL = "UPDATE profiles SET department = '$profileID' WHERE id = '" + this.profileID + "';"
            connection.prepareStatement(SQL).execute()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun updateAll(userProfile: UserProfile) {
        updateDepartment(userProfile.department)
        updateRole(userProfile.role)
        userProfile.subRole?.let { updateSubRole(it) }
        updateUserOwner(userProfile.userOwner)
    }

    companion object {
        private fun getConnection(): Connection {
            return DriverManager.getConnection(
                    DataBaseConfiguration.DB_URL,
                    DataBaseConfiguration.DB_USER,
                    DataBaseConfiguration.DB_PASSWORD)
        }

        private fun fromDataBaseFromSQL(quest: String): UserProfile? {
            try {
                val SQL = "SELECT * FROM profiles where $quest"
                val statement = getConnection().createStatement()
                val resultSet = statement.executeQuery(SQL)

                var profileID: UUID?
                var department: String?
                var role: Role?
                var subRole: SubRole?
                var userOwner: UserAccount?

                while (resultSet.next()) {
                    profileID = UUID.fromString(resultSet.getString("id"))
                    department = resultSet.getString("department")
                    val roleString = resultSet.getString("role")
                    role = Role.valueOf(roleString)
                    val subRoleStr = resultSet.getString("subrole")
                    if (subRoleStr == SubRole.REGULAR.toString())
                        subRole = SubRole.REGULAR
                    else if (subRoleStr == SubRole.CAPTAIN.toString())
                        subRole = SubRole.CAPTAIN
                    else if (subRoleStr == SubRole.PROF_ORGANIZER.toString())
                        subRole = SubRole.PROF_ORGANIZER
                    else
                        subRole = SubRole.NONE

                    userOwner = UserAccount.findByUserID(UUID.fromString(resultSet.getString("owner_id")))

                    if (profileID != null && department != null && role != null && subRole != null && userOwner != null)
                        return Builder(department, role, subRole, userOwner, profileID).build()
                    else {
                        return null
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun listFromDataBaseBySQL(quest: String): List<UserProfile>? {
            val SQL = "SELECT * FROM profiles where $quest"
            val statement = getConnection().createStatement()
            val resultSet = statement.executeQuery(SQL)

            var profileID: UUID? = null
            var department: String? = null
            var role: Role? = null
            var subRole: SubRole? = null
            var userOwner: UserAccount? = null
            var list: ArrayList<UserProfile> = ArrayList<UserProfile>()

            while (resultSet.next()) {
                profileID = UUID.fromString(resultSet.getString("id"))
                department = resultSet.getString("department")
                val roleString = resultSet.getString("role")
                role = Role.valueOf(roleString)
                val subRoleStr: String? = resultSet.getString("subrole")
                subRole = SubRole.valueOf(subRoleStr?:"NONE")
                userOwner = UserAccount.findByUserID(UUID.fromString(resultSet.getString("owner_id")))

                if (profileID != null && department != null && role != null && subRole != null && userOwner != null)
                    list.add(Builder(department, role, subRole, userOwner, profileID).build())
            }
            if (!list.isEmpty()) {
                return list.toList()
            } else {
                return null
            }

        }

        fun findByID(profileID: UUID): UserProfile? {
            return fromDataBaseFromSQL("id = '$profileID'")
        }

        fun findByDepartment(department: String): List<UserProfile>? {
            return listFromDataBaseBySQL("department = '$department'")
        }

        fun findByRole(role: Role): List<UserProfile>? {
            return listFromDataBaseBySQL("role = '$role'")
        }

        fun findBySubrole(subRole: SubRole): List<UserProfile>? {
            return listFromDataBaseBySQL("subrole = '$subRole'")
        }

        fun findByUserOwner(userOwner: UserAccount): List<UserProfile>? {
            return listFromDataBaseBySQL("owner_id = '" + userOwner.userId + "'")
        }
    }


    data class Builder(
            var department: String? = null,
            var role: Role? = null,
            var subRole: SubRole? = null,
            var userOwner: UserAccount? = null,
            var profileID: UUID? = null
    ) {
        fun department(department: String) = apply { this.department = department }
        fun role(role: Role) = apply { this.role = role }
        fun subRole(subRole: SubRole) = apply { this.subRole = subRole }
        fun userOwner(userOwner: UserAccount) = apply { this.userOwner = userOwner }
        fun profileID(profileID: UUID) = apply { this.profileID = profileID }
        fun build(): UserProfile {
            if (department == null)
                throw IllegalArgumentException("UserProfile must have department data")
            if (role == null)
                throw IllegalArgumentException("UserProfile must have role data")
            if (userOwner == null)
                throw IllegalArgumentException("UserProfile must have userOwner data")
            if (role == Role.STUDENT)
                subRole ?: SubRole.REGULAR
            else
                subRole = SubRole.NONE
            return UserProfile(department!!, role!!, subRole, userOwner!!, profileID ?: UUID.randomUUID())
        }
    }
}