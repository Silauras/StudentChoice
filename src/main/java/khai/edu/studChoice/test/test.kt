package khai.edu.studChoice.test

import khai.edu.studChoice.csvWorkers.CSVUserAccount
import khai.edu.studChoice.entities.userAccount.Email
import khai.edu.studChoice.entities.userAccount.FullName
import khai.edu.studChoice.entities.userAccount.UserAccount
import org.jetbrains.annotations.TestOnly

/**
 * Created by Silauras on 09.07.2020 at 15:56
 */



fun main(){
    val builder = UserAccount.Builder()
    val userAccount = builder.email(Email("tml.cm"))
            .fullName(FullName(" roll me", "Soold me ")).build()
    userAccount.save()
    CSVUserAccount.saveCSV()
    CSVUserAccount.saveXLS()
}