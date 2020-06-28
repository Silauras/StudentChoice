package khai.edu.StudChoice

import khai.edu.StudChoice.config.DataBaseInitializer
import org.apache.catalina.WebResourceRoot
import org.apache.catalina.core.StandardContext
import org.apache.catalina.startup.Tomcat
import org.apache.catalina.webresources.DirResourceSet
import org.apache.catalina.webresources.StandardRoot
import java.io.File


/**
 * Created by Silauras on 18.03.2020
 */

fun main() {
    DataBaseInitializer.initFullDatabase()

    AwakeFuckingMachine()
}
fun AwakeFuckingMachine(){
    val webappDirLocation = "src/main/webapp/"
    val tomcat = Tomcat()

    //The port that we should run on can be set into an environment variable
    //Look for that variable and default to 8080 if it isn't there.
    var webPort = System.getenv("PORT")
    if (webPort == null || webPort.isEmpty()) {
        webPort = "8080"
    }

    tomcat.setPort(Integer.valueOf(webPort))

    val ctx = tomcat.addWebapp("/", File(webappDirLocation).getAbsolutePath()) as StandardContext
    System.out.println("configuring app with basedir: " + File("./$webappDirLocation").getAbsolutePath())

    // Declare an alternative location for your "WEB-INF/classes" dir
    // Servlet 3.0 annotation will work
    val additionWebInfClasses = File("target/classes")
    val resources: WebResourceRoot = StandardRoot(ctx)
    resources.addPreResources(DirResourceSet(resources, "/WEB-INF/classes",
            additionWebInfClasses.getAbsolutePath(), "/"))
    ctx.resources = resources

    tomcat.start()

    print("Init complete!")
    tomcat.server.await()
}