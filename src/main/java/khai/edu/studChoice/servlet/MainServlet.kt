package khai.edu.studChoice.servlet

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Created by Silauras on 18.03.2020
 */

@WebServlet(urlPatterns = ["/main"])
class MainServlet : HttpServlet() {

    private var responseTemplate: String? = null

    /*
    override fun init() {
        responseTemplate = try {
            val templateURI = javaClass.getResource("/demo.html").toURI()
            val bytes = Files.readAllBytes(Paths.get(templateURI))
            String(bytes, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            throw ServletException(e)
        } catch (e: URISyntaxException) {
            throw ServletException(e)
        }
        println("!!! Demo Servlet Initialized !!!")
    } */

    protected override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        try {
            val templateURI = javaClass.getResource("/main.html").toURI()
            //val templateURI = javaClass.getResource("src/main/webapp/templates/main.html").toURI();
            val bytes = Files.readAllBytes(Paths.get(templateURI))
            responseTemplate = String(bytes, StandardCharsets.UTF_8)
            resp.contentType = "text/html"
            resp.status = HttpServletResponse.SC_OK
            resp.writer.print(responseTemplate)
        }catch (e : Exception){
            e.printStackTrace()
        }finally {
            resp.writer.close()
        }
    }

    public override fun destroy() {
        System.out.println("!!! Demo Servlet Destroyed !!!");
        super.destroy()
    }
}