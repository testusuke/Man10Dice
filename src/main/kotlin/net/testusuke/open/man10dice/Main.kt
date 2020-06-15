package net.testusuke.open.man10dice

import org.bukkit.plugin.java.JavaPlugin
import java.lang.NullPointerException
import kotlin.properties.Delegates

class Main: JavaPlugin() {

    companion object{
        lateinit var plugin:Main
    }

    var prefix = "§e[§dMan10§bDice§e]§f"
    val pluginName = "Man10Dice"
    val version = "1.0.3"

    //  local
    var radius:Int = 50

    override fun onEnable() {
        //  main
        plugin = this
        //  Logger
        val loggerMessage = """
            ===================================
            Name: $pluginName
            Author: testusuke Version: $version
            ===================================
        """
        logger.info(loggerMessage)
        //  Config
        this.saveDefaultConfig()
        //  loadConfig
        radius = try {
            config.getInt("r")
        }catch (e:NullPointerException){
            e.printStackTrace()
            50
        }
        //  Command
        getCommand("mdice")?.setExecutor(Command)
    }

    override fun onDisable() {

        //  save
        config.set("r",radius)
        this.saveConfig()
    }

}