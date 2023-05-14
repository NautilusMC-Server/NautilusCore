package nautiluscore;

import nautiluscore.commands.*;
import nautiluscore.data.DataAccessor;
import nautiluscore.data.DataManager;
import nautiluscore.runnable.AFKManager;
import nautiluscore.runnable.SleepManager;
import nautiluscore.runnable.TablistManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class NautilusCore extends JavaPlugin {

    protected String pluginName;
    protected DataManager dataManager;
    protected DataAccessor dataAccessor;
    private EventManager eventManager;
    private TablistManager tablistManager;
    private AFKManager afkManager;
    private SleepManager sleepManager;
    private ArrayList<CommandStem> commands;

    public NautilusCore() {
        this.pluginName = "NautilusCore";
        commands = new ArrayList<>();
        registerCommands();
    }

    @Override
    public void onEnable() {
        //data
        dataManager = new DataManager(this);
        dataAccessor = new DataAccessor(this);

        //runnable
        this.tablistManager = new TablistManager(this);
        tablistManager.onTab();
        this.afkManager = new AFKManager(this);
        afkManager.afk();
        this.sleepManager = new SleepManager(this);
        sleepManager.slow();
        sleepManager.quick();

        //events
        this.eventManager = new EventManager(this);
        registerEvents();

        //commands

        getLogger().info("NautilusCore has started.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        for(CommandStem c : commands){
            if(c.getCommand().equalsIgnoreCase(label) || (c.getAliases() != null && c.getAliases().contains(label))){
                c.execute(sender, args);
            }
        }
        return true;
    }

    protected void registerEvents() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(eventManager, this);
        pm.registerEvents(afkManager, this);
    }

    protected void registerCommands() {
        commands.add(new AfkCommand(this));
        commands.add(new ColorsCommand(this));
        commands.add(new ConfigCommand(this));
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public DataAccessor d() {
        return dataAccessor;
    }

    public AFKManager getAfkManager() {
        return afkManager;
    }

}
