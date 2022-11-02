package io.runebox.deobfuscator

import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.swing.JFrame

class TestClient(private val deobJar: File, private val vanillaJar: File) {

    private val params = hashMapOf<String, String>()

    fun start() {
        fetchParams()

        val classLoader = URLClassLoader(arrayOf(deobJar.toURI().toURL()))
        val main = params["initial_class"]!!.replace(".class", "")
        val applet = classLoader.loadClass(main).getDeclaredConstructor().newInstance() as Applet

        applet.background = Color.BLACK
        applet.layout = null
        applet.size = Dimension(params["applet_minwidth"]!!.toInt(), params["applet_minheight"]!!.toInt())
        applet.preferredSize = applet.size
        applet.setStub(applet.createSub())
        applet.isVisible = true
        applet.init()

        val frame = JFrame("Test Client - ${deobJar.name}")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = GridLayout(1, 0)
        frame.add(applet)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.minimumSize = frame.size
        frame.isVisible = true
    }

    private fun fetchParams() {
        val lines = URL(JAV_CONFIG_URL).readText().split("\n")
        lines.forEach {
            var line = it
            if(line.startsWith("param=")) {
                line = line.substring(6)
            }
            val idx = line.indexOf("=")
            if(idx >= 0) {
                params[line.substring(0, idx)] = line.substring(idx + 1)
            }
        }
    }

    private fun Applet.createSub() = object : AppletStub {
        override fun isActive(): Boolean = true
        override fun getAppletContext(): AppletContext? = null
        override fun getCodeBase(): URL = URL(params["codebase"]!!)
        override fun getDocumentBase(): URL = URL(params["codebase"])
        override fun getParameter(name: String): String? = params[name]
        override fun appletResize(width: Int, height: Int) {
            this@createSub.size = Dimension(width, height)
            this@createSub.preferredSize = this@createSub.size
        }
    }

    companion object {
        private const val JAV_CONFIG_URL = "http://oldschool1.runescape.com/jav_config.ws"
    }
}