package khai.edu.StudChoice.servlet.api

import khai.edu.StudChoice.Entities.userAccount.UserAccount
import java.util.*
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Silauras on 11.04.2020
 */

@WebServlet(urlPatterns = ["/api/getProfiles"])
class APIGetProfilesServlet : HttpServlet() {

    override fun doGet(req: HttpServletRequest?, resp: HttpServletResponse?) {
        try {
            resp?.addHeader("Access-Control-Allow-Origin", "*")
            resp?.contentType = "application/json"
            var UserID: String? = null
            if (req?.cookies != null) {
                for (i in req?.cookies!!) {
                    if (i.name == "StCUserID")
                        UserID = i.value
                }
            }
            if (UserID == null && UserAccount.findByUserID(UUID.fromString(UserID)) == null) {
                resp?.status = HttpServletResponse.SC_UNAUTHORIZED
            } else {
                resp?.status = HttpServletResponse.SC_OK


                val userProfiles = UserAccount.findByUserID(UUID.fromString(UserID))?.findProfiles()
                if (userProfiles != null) {
                    resp?.writer?.print("{array:[")
                    for (i in userProfiles) {
                        resp?.writer?.print("""{"id":"${i.profileID}","department ":"${i.department}","role ":"${i.role}","subrole ":"${i.subRole.toString()}",}""")
                    }
                    resp?.writer?.print("]}")

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            resp?.writer?.close()
        }
    }
}