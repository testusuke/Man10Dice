package net.testusuke.open.man10dice

import net.testusuke.open.man10dice.Main.Companion.plugin
import org.apache.commons.lang.math.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.lang.NumberFormatException
import java.util.concurrent.ThreadLocalRandom
import java.util.regex.Pattern

object Command:CommandExecutor {

    enum class DiceType{
        NORMAL,LOCAL,GLOBAL
    }

    val permission = "man10dice.general"
    val adminPermission = "man10dice.admin"

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        //  player,pex
        if(sender !is Player || !sender.hasPermission(permission))return false

        if(args.isEmpty()){
            sendHelp(sender)
            return false
        }

        when(args[0]){
            "r" -> {
                if(!sender.hasPermission(adminPermission))return false
                var r:Int
                try {
                    r = args[1].toInt()
                }catch (e:NumberFormatException){
                    sender.sendMessage("${plugin.prefix}§6エラーです。")
                    return false
                }
                if(r <= 0){
                    sender.sendMessage("${plugin.prefix}§c1以上で指定してください。")
                    return false
                }
                plugin.radius = r
                sender.sendMessage("${plugin.prefix}§a半径を${r}に設定しました。")
            }
            "help" -> sendHelp(sender)
            "local" -> {
                if(!canDice(args,sender,1))return false
                val dice:Int
                //  Int check
                try {
                    dice = args[1].toInt()
                }catch (e:NumberFormatException){
                    sendDiceRule(sender)
                    return false
                }
                //  run
                runDice(DiceType.LOCAL,dice,sender)
            }
            "global" -> {
                if(!canDice(args,sender,1))return false
                val dice:Int
                //  Int check
                try {
                    dice = args[1].toInt()
                }catch (e:NumberFormatException){
                    sendDiceRule(sender)
                    return false
                }
                //  run
                runDice(DiceType.GLOBAL,dice,sender)
            }
            else -> {
                if(!canDice(args,sender,0))return false
                val dice:Int
                //  Int check
                try {
                    dice = args[0].toInt()
                }catch (e:NumberFormatException){
                    sendDiceRule(sender)
                    return false
                }
                //  run
                runDice(DiceType.NORMAL,dice,sender)
            }
        }

        return false
    }

    private fun sendHelp(player: Player){
        val helpMessage = """
            §e=========================================
            §6/mdice <vault> <- 指定された数のサイコロを振ります。
            §6/mdice local <vault> <- 半径${plugin.radius}メートルのプレイヤーのみ見れるサイコロを振ります。
            §6/mdice global <vault> <- 全員が見れるサイコロを振ります。
            §6/mdice help <- ヘルプを表示します。
            §c/mdice r <vault> <- 半径を設定します。
            §d§lCreated by testusuke Version: ${plugin.version}
            §e=========================================
        """
        player.sendMessage(helpMessage)
    }

    private fun canDice(args: Array<out String>,sender: Player,start:Int):Boolean{
        val max = start + 1
        if(args.size == max){
            sendDiceRule(sender)
            return false
        }
        //  正規表現
        if(!checkNumber(args[start])){
            sendDiceRule(sender)
            return false
        }

        return true
    }

    /**
     * 半角数値チェック
     *
     * @param s 文字列
     * @return true 正常  false エラー
     */
    private fun checkNumber(s: String): Boolean {
        return NumberUtils.isNumber(s)
    }

    //  Diceの不正利用
    private fun sendDiceRule(player: Player){
        player.sendMessage("${plugin.prefix}§c半角数字で2以上${Int.MAX_VALUE}以下を指定してください。")
    }

    //  Run
    private fun runDice(diceType: DiceType,dice:Int,player: Player){
        if(dice <= 1){
            sendDiceRule(player)
            return
        }
        //  Random
        val random = ThreadLocalRandom.current().nextInt(1,dice)
        val message = "${plugin.prefix}§6§l${player.name}§a§lさんが§6§l${dice}§a§l面のサイコロを振って§d§l${random}§a§lが出ました"
        when(diceType) {
            DiceType.NORMAL -> {
                Bukkit.broadcastMessage(message)
            }
            DiceType.LOCAL -> {
                for (players in player.getNearbyEntities(plugin.radius.toDouble(), plugin.radius.toDouble(), plugin.radius.toDouble())) {
                    if (players is Player) {
                        players.sendMessage(message)
                    }
                }
            }
            DiceType.GLOBAL -> {
                Bukkit.broadcastMessage(message)
            }
        }
    }
}
