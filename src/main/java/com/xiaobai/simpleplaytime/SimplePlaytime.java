package com.xiaobai.simpleplaytime;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class SimplePlaytime extends JavaPlugin implements Listener {

    private final Map<UUID, Long> totalPlaytime = new HashMap<>(); // 总时间存储
    private final Map<UUID, Long> sessionStart = new HashMap<>();  // 本次登录时间
    private final File dataFile = new File(getDataFolder(), "data.json");
    private final Gson gson = new Gson();
    
    // 排行榜缓存
    private List<Map.Entry<UUID, Long>> topCache = new ArrayList<>();

    @Override
    public void onEnable() {
        loadData();
        Bukkit.getPluginManager().registerEvents(this, this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaytimeExpansion().register();
            getLogger().info("PlaceholderAPI 变量已注册！");
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveData, 6000L, 6000L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::updateTopCache, 20L, 1200L);

        long now = System.currentTimeMillis();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            if (!isBot(p.getName())) {
                sessionStart.put(p.getUniqueId(), now);
            }
        }
    }

    @Override
    public void onDisable() {
        long now = System.currentTimeMillis();
        for (UUID uuid : sessionStart.keySet()) {
            long start = sessionStart.get(uuid);
            totalPlaytime.put(uuid, totalPlaytime.getOrDefault(uuid, 0L) + (now - start));
        }
        sessionStart.clear();
        saveData();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (isBot(e.getPlayer().getName())) return;
        sessionStart.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (sessionStart.containsKey(uuid)) {
            long start = sessionStart.remove(uuid);
            long sessionTime = System.currentTimeMillis() - start;
            totalPlaytime.put(uuid, totalPlaytime.getOrDefault(uuid, 0L) + sessionTime);
            Bukkit.getScheduler().runTaskAsynchronously(this, this::saveData);
        }
    }

    private boolean isBot(String name) {
        return name.toLowerCase().startsWith("bot_");
    }

    public long getPlayerTime(OfflinePlayer player) {
        long saved = totalPlaytime.getOrDefault(player.getUniqueId(), 0L);
        if (player.isOnline() && sessionStart.containsKey(player.getUniqueId())) {
            return saved + (System.currentTimeMillis() - sessionStart.get(player.getUniqueId()));
        }
        return saved;
    }

    private void updateTopCache() {
        Map<UUID, Long> currentStats = new HashMap<>(totalPlaytime);
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, Long> entry : sessionStart.entrySet()) {
            currentStats.put(entry.getKey(), 
                currentStats.getOrDefault(entry.getKey(), 0L) + (now - entry.getValue()));
        }

        topCache = currentStats.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    // --- 【这里是修改后的格式化逻辑】 ---
    private String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        if (totalSeconds == 0) return "0分"; // 如果完全没玩过，显示0分

        long YEAR_SECONDS = 365L * 24 * 3600;
        long MONTH_SECONDS = 30L * 24 * 3600;
        long DAY_SECONDS = 24L * 3600;
        long HOUR_SECONDS = 3600L;

        long years = totalSeconds / YEAR_SECONDS;
        long remaining = totalSeconds % YEAR_SECONDS;

        long months = remaining / MONTH_SECONDS;
        remaining %= MONTH_SECONDS;

        long days = remaining / DAY_SECONDS;
        remaining %= DAY_SECONDS;

        long hours = remaining / HOUR_SECONDS;
        long minutes = (remaining % HOUR_SECONDS) / 60;

        StringBuilder sb = new StringBuilder();
        
        // 只有大于0才会加入字符串，否则完全不显示
        if (years > 0) sb.append(years).append("年");
        if (months > 0) sb.append(months).append("月");
        if (days > 0) sb.append(days).append("天");
        if (hours > 0) sb.append(hours).append("时");
        if (minutes > 0) sb.append(minutes).append("分");
        
        // 如果上面的都为0（比如只玩了30秒，不足1分钟），为了防止显示空白，显示“0分”
        if (sb.length() == 0) return "0分";
        
        return sb.toString();
    }

    private void loadData() {
        if (!dataFile.exists()) return;
        try (Reader reader = new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8)) {
            Map<UUID, Long> data = gson.fromJson(reader, new TypeToken<Map<UUID, Long>>(){}.getType());
            if (data != null) totalPlaytime.putAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdirs();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(dataFile), StandardCharsets.UTF_8)) {
                gson.toJson(totalPlaytime, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class PlaytimeExpansion extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() { return "spt"; }
        @Override
        public @NotNull String getAuthor() { return "XiaoBai"; }
        @Override
        public @NotNull String getVersion() { return "1.0.0"; }
        @Override
        public boolean persist() { return true; }

        @Override
        public String onRequest(OfflinePlayer player, @NotNull String params) {
            if (params.equalsIgnoreCase("time")) {
                return formatTime(getPlayerTime(player));
            }
            if (params.startsWith("top_name_")) {
                try {
                    int rank = Integer.parseInt(params.replace("top_name_", ""));
                    if (rank < 1 || rank > topCache.size()) return "暂无数据";
                    UUID uuid = topCache.get(rank - 1).getKey();
                    OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
                    return p.getName() != null ? p.getName() : "未知玩家";
                } catch (NumberFormatException e) { return "错误"; }
            }
            if (params.startsWith("top_time_")) {
                try {
                    int rank = Integer.parseInt(params.replace("top_time_", ""));
                    if (rank < 1 || rank > topCache.size()) return "---";
                    return formatTime(topCache.get(rank - 1).getValue());
                } catch (NumberFormatException e) { return "错误"; }
            }
            return null;
        }
    }
}
