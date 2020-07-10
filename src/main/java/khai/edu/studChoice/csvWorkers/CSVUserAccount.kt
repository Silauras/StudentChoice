package khai.edu.studChoice.csvWorkers

import khai.edu.studChoice.config.DataBaseConfiguration.DB_PASSWORD
import khai.edu.studChoice.config.DataBaseConfiguration.DB_URL
import khai.edu.studChoice.config.DataBaseConfiguration.DB_USER
import khai.edu.studChoice.entities.userAccount.Email
import khai.edu.studChoice.entities.userAccount.FullName
import khai.edu.studChoice.entities.userAccount.Phone
import khai.edu.studChoice.entities.userAccount.UserAccount
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.sql.DriverManager
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Silauras on 09.07.2020 at 16:31
 */
object CSVUserAccount {

    private fun readDataBase(): ArrayList<UserAccount> {
        val sql = "SELECT * FROM account"
        val statement = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD).createStatement()
        val resultSet = statement.executeQuery(sql)
        val list = ArrayList<UserAccount>()

        while (resultSet.next()) {
            val builder = UserAccount.Builder()
            builder.userId = UUID.fromString(resultSet.getString("id"))
            builder.email = Email(resultSet.getString("email"))
            builder.fullName = FullName(resultSet.getString("name"),
                    resultSet.getString("surname"),
                    resultSet.getString("patronymic"))
            builder.phone = if (resultSet.getString("phone") == "") {
                null
            } else {
                Phone.getPhoneFromString(resultSet.getString("phone"))
            }
            builder.reserveEmail = Email(resultSet.getString("reserve_email"))
            if (builder.userId != null) {
                list.add(builder.build())
            }
        }
        return list
    }

    fun saveCSV() {
        val scv = FileWriter("user accounts.csv")
        for (i in readDataBase()) {
            scv.write("${i.userId},${i.email},${i.fullName.surname},${i.fullName.name},${i.fullName.patronymic},${i.phone.toString()},${i.reserveEmail}\n")
        }
        scv.close()

    }

    fun saveXLS() {
        val list = readDataBase()

        val book: Workbook = HSSFWorkbook()
        val sheet = book.createSheet("User Account")

        var row = sheet.createRow(0)
        row.createCell(0).setCellValue("UUID")
        row.createCell(1).setCellValue("Email")
        row.createCell(2).setCellValue("Surname")
        row.createCell(3).setCellValue("Name")
        row.createCell(4).setCellValue("Patronymic")
        row.createCell(5).setCellValue("Phone")
        row.createCell(6).setCellValue("Reserve Email")


        for (i in 0 until list.size) {
            row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(list[i].userId.toString())
            row.createCell(1).setCellValue(list[i].email.toString())
            row.createCell(2).setCellValue(list[i].fullName.surname)
            row.createCell(3).setCellValue(list[i].fullName.name)
            row.createCell(4).setCellValue(list[i].fullName.patronymic)
            if (list[i].phone == null)
                row.createCell(5).setCellValue("")
            else
                row.createCell(5).setCellValue(list[i].phone.toString())
            row.createCell(6).setCellValue(list[i].reserveEmail.toString())
        }

        book.write(FileOutputStream(File("student choice database.xls")))
        book.close()
    }
}