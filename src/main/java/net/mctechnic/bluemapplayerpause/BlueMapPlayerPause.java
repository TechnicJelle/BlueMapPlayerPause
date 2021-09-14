package net.mctechnic.bluemapplayerpause;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.renderer.RenderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class BlueMapPlayerPause extends JavaPlugin implements Listener {

	void renderCheck() {
//		getLogger().info("check");
		if (Bukkit.getOnlinePlayers().size() < 1)
			renderStart();
		else
			renderPause();
	}

	void renderStart() {
		BlueMapAPI.getInstance().ifPresent(blueMapAPI -> {
			RenderAPI renderAPI = blueMapAPI.getRenderAPI();
			if(!renderAPI.isRunning()) {
				getLogger().info("BlueMap Renderer started");
				renderAPI.start();
			}
		});
	}

	void renderPause() {
		BlueMapAPI.getInstance().ifPresent(blueMapAPI -> {
			RenderAPI renderAPI = blueMapAPI.getRenderAPI();
			if(renderAPI.isRunning()) {
				getLogger().info("BlueMap Renderer paused");
				renderAPI.pause();
			}
		});
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("BlueMap Player Pause plugin enabled!");

		BlueMapAPI.onEnable(api -> {
			getLogger().info("BlueMap API ready!");
			renderCheck();

			//every minute do a check
			getServer().getScheduler().runTaskTimerAsynchronously(this, this::renderCheck, 0L, 20*60);
		});

		BlueMapAPI.onDisable(api -> {
			getLogger().info("BlueMap API ready!");
			renderStart();
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, this::renderPause, 20);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		//delay with 10 seconds to account for relogging
		Bukkit.getScheduler().runTaskLaterAsynchronously(this, this::renderCheck, 20*10);
	}

	@Override
	public void onDisable() {
		getLogger().info("BlueMap Player Pause plugin disabled!");
	}
}
