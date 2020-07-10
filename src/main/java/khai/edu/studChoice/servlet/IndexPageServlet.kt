package khai.edu.studChoice.servlet

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by Silauras on 07.04.2020
 */

@WebServlet(urlPatterns = ["/"])
class IndexPageServlet: HttpServlet() {
    protected override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        try {
            val path = Paths.get("src/main/webapp/templates/index.html")
            //val templateURI = javaClass.getResource("src/main/webapp/templates/main.html").toURI();
            val bytes = Files.readAllBytes(path)
            val response = String(bytes, StandardCharsets.UTF_8)
            if (resp!= null){
                resp.contentType = "text/html"
                resp.status = HttpServletResponse.SC_OK
                resp.writer.print(response)
            }
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            resp.writer.close()
        }
    }
}