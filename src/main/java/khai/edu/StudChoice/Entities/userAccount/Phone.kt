package khai.edu.StudChoice.Entities.userAccount

/**
 * Created by Silauras on 18.03.2020
 */

data class Phone(val code: String, val number: String) {

    fun phoneToString(): String? {
        return if (code != null && number != null || code !== "" && number !== "") "$code-$number" else ""
    }

    companion object {
        fun getPhoneFromString(inputString: String): Phone? {
            if (inputString.contains("-")) {
                val phone = inputString.split("-".toRegex()).toTypedArray()
                return Phone(phone[0], phone[1])
            }
            return null
        }
    }




}