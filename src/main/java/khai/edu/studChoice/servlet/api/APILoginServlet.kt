package khai.edu.studChoice.servlet.api

import khai.edu.studChoice.entities.userAccount.Email
import khai.edu.studChoice.entities.userAccount.UserAccount
import javax.servlet.annotation.WebServlet
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Silauras on 07.04.2020
 */
@WebServlet(urlPatterns = ["/api/login"])
class APILoginServlet : HttpServlet() {
    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            val email = req?.getParameter("email")

            var UserID: String? = null
            if (req?.cookies != null) {
                for (i in req?.cookies!!) {
                    if (i.name == "StCUserID")
                        UserID = i.value
                }
            }
            //if (UserID != null && UserAccount.findByUserID(UUID.fromString(UserID)) != null) {
            //    resp?.status = HttpServletResponse.SC_MOVED_TEMPORARILY
            //    resp?.addHeader("Location", "/")
            //} else {
            resp?.contentType = "application/json"
            resp?.status = HttpServletResponse.SC_OK
            resp?.addHeader("Access-Control-Allow-Origin", "*")

            val loginEmail = email?.let { Email(it) }
            val userAccount = loginEmail?.let { UserAccount.findByEmail(it) }
            if (userAccount != null) {
                resp?.addCookie(Cookie("StCUserID", userAccount.userId.toString()))
                resp?.writer?.print("{\"status\":true}")
            } else
                resp?.writer?.print("{\"status\":false}")
            //}
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            resp?.writer?.close()
        }
    }
}