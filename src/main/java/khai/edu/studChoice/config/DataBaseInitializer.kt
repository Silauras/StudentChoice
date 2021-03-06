package khai.edu.studChoice.config


import khai.edu.studChoice.config.DataBaseConfiguration.DB_NAME
import khai.edu.studChoice.config.DataBaseConfiguration.DB_PASSWORD
import khai.edu.studChoice.config.DataBaseConfiguration.DB_URL
import khai.edu.studChoice.config.DataBaseConfiguration.DB_USER
import khai.edu.studChoice.config.DataBaseConfiguration.URL
import org.postgresql.util.PSQLException
import java.sql.DriverManager

class DataBaseInitializer {
    companion object {
        fun initDataBase() {
            try {
                val SQL = "CREATE DATABASE \"$DB_NAME\" " +
                        "    WITH " +
                        "    OWNER = ${DB_USER}" +
                        "    ENCODING = 'UTF8'" +
                        "    LC_COLLATE = 'Russian_Ukraine.1251'" +
                        "    LC_CTYPE = 'Russian_Ukraine.1251'" +
                        "    TABLESPACE = pg_default" +
                        "    CONNECTION LIMIT = -1;"

                val statement = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD).prepareStatement(SQL)
                statement.executeUpdate()
            } catch (e: PSQLException) {
                e.printStackTrace()
            }
        }

        fun initUserAccountsTable() {
            val SQL = "create table if not exists account\n" +
                    "(\n" +
                    "    id            uuid    not null\n" +
                    "        constraint users_pkey\n" +
                    "            primary key,\n" +
                    "    email         varchar not null,\n" +
                    "    surname       varchar not null,\n" +
                    "    name          varchar not null,\n" +
                    "    patronymic    varchar,\n" +
                    "    phone         varchar,\n" +
                    "    reserve_email varchar\n" +
                    ");\n" +
                    "\n" +
                    "alter table accounts\n" +
                    "    owner to $DB_USER;\n" +
                    "\n" +
                    "create unique index if not exists users_email_uindex\n" +
                    "    on accounts (email);\n"

            var connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            val statement = connection.prepareStatement(SQL)
            statement.executeUpdate()
        }

        fun initDepartmentsTable() {
            val SQL = "create table if not exists department(    id          uuid    not null        constraint departments_pk            primary key,    number      varchar not null,    name        varchar not null,    description varchar);alter table departments    owner to $DB_USER;create unique index if not exists departments_id_uindex    on departments (id);create unique index if not exists departments_name_uindex    on departments (name);create unique index if not exists departments_number_uindex    on departments (number);"

            var connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            val statement = connection.prepareStatement(SQL)
            statement.executeUpdate()
        }

        fun initUserProfilesTable() {
            val SQL = "create table if not exists profiles\n" +
                    "(\n" +
                    "    id         uuid not null\n" +
                    "        constraint profiles_pk\n" +
                    "            primary key,\n" +
                    "    department uuid,\n" +
                    "    role       role not null,\n" +
                    "    subrole    subrole,\n" +
                    "    owner_id   uuid not null\n" +
                    ");\n" +
                    "\n" +
                    "alter table profiles\n" +
                    "    owner to $DB_USER;\n" +
                    "\n" +
                    "create unique index if not exists profiles_id_uindex\n" +
                    "    on profiles (id);\n" +
                    "\n\n"

            var connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            val statement = connection.prepareStatement(SQL)
            statement.executeUpdate()
        }

        fun initTables() {
            initUserAccountsTable()
            initDepartmentsTable()
            initUserProfilesTable()
        }

        fun initRoleEnum() {
            val SQL = "create type role as enum ('STUDENT', 'TEACHER', 'DEAN', 'CONTROLLING_PERSON', 'ADMIN');\n" +
                    "\n" +
                    "alter type role owner to $DB_USER;\n"

            var connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            val statement = connection.prepareStatement(SQL)
            statement.executeUpdate()
        }

        fun initSubRoleEnum() {
            val SQL = "create type subrole as enum ('REGULAR', 'CAPTAIN', 'PROF_ORGANIZER', 'NONE');\n" +
                    "\n" +
                    "alter type subrole owner to $DB_USER;\n"

            var connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            val statement = connection.prepareStatement(SQL)
            statement.executeUpdate()
        }

        fun initEnums() {
            initRoleEnum()
            initSubRoleEnum()
        }

        fun initSchema() {
            initEnums()
            initTables()
        }

        fun initFullDatabase() {
            initDataBase()
            initSchema()
        }
    }
}