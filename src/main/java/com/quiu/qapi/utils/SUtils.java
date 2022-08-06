package com.quiu.qapi.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SUtils
{
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yy");

    private static final NumberFormat COMMA_FORMAT = NumberFormat.getInstance();
    private static final List<ChatColor> CRIT_SPECTRUM = Arrays.asList(ChatColor.WHITE, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GOLD,
            ChatColor.RED, ChatColor.RED);
    private static final List<ChatColor> VISIBLE_COLOR_SPECTRUM = Arrays.asList(ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.DARK_RED,
            ChatColor.DARK_PURPLE, ChatColor.GOLD, ChatColor.GREEN, ChatColor.AQUA, ChatColor.RED, ChatColor.LIGHT_PURPLE, ChatColor.YELLOW, ChatColor.WHITE);

    static
    {
        COMMA_FORMAT.setGroupingUsed(true);
    }

    public static String commaify(int i)
    {
        return COMMA_FORMAT.format(i);
    }

    public static String commaify(double d)
    {
        return COMMA_FORMAT.format(d);
    }

    public static String commaify(long l)
    {
        return COMMA_FORMAT.format(l);
    }

    public static List<String> getPlayerNameList()
    {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
            names.add(player.getName());
        return names;
    }

    public static int random(int min, int max)
    {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static double random(double min, double max)
    {
        return Math.random() * (max - min) + min;
    }


    public static List<String> splitByWordAndLength(String string, int splitLength, String separator)
    {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\G" + separator + "*(.{1," + splitLength + "})(?=\\s|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);
        while (matcher.find())
            result.add(matcher.group(1));
        return result;
    }

    public static ItemStack applyColorToLeatherArmor(ItemStack stack, Color color)
    {
        if (!(stack.getItemMeta() instanceof LeatherArmorMeta)) return stack;
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);
        return stack;
    }

    public static String color(String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    // not my code; found on stack overflow
    public static String toRomanNumeral(int num)
    {
        StringBuilder sb = new StringBuilder();
        int times;
        String[] romans = new String[] { "I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M" };
        int[] ints = new int[] { 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000 };
        for (int i = ints.length - 1; i >= 0; i--)
        {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0)
            {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }

    public static String rainbowize(String string)
    {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String c : string.split(""))
        {
            if (i > CRIT_SPECTRUM.size() - 1) i = 0;
            builder.append(CRIT_SPECTRUM.get(i)).append(c);
            i++;
        }
        return builder.toString();
    }

    public static ItemStack createNamedItemStack(Material material, String name)
    {
        ItemStack stack = new ItemStack(material);
        if (name != null)
        {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(name);
            stack.setItemMeta(meta);
        }
        return stack;
    }


    public static String getDate()
    {
        return DATE_FORMAT.format(new Date());
    }

    // stackoverflow code lol
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static <T> boolean addIf(T t, List<T> list, boolean test)
    {
        if (test)
            list.add(t);
        return test;
    }

    public static ItemStack setStackAmount(ItemStack stack, int amount)
    {
        stack.setAmount(amount);
        return stack;
    }


    public static double roundTo(double d, int decimalPlaces)
    {
        if (decimalPlaces < 1)
            throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder()
                .append("#.");
        for (int i = 0; i < decimalPlaces; i++)
            builder.append("#");
        DecimalFormat df = new DecimalFormat(builder.toString());
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(df.format(d));
    }

    public static void toggleAllowFlightNoCreative(UUID uuid, boolean flight)
    {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        GameMode gameMode = player.getGameMode();
        if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) return;
        player.setAllowFlight(flight);
    }

    // not my code
    public static List<Block> getNearbyBlocks(Location location, int radius, Material type)
    {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++)
        {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++)
            {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++)
                {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == type || type == null)
                        blocks.add(block);
                }
            }
        }
        return blocks;
    }


    public static ItemStack getStack(String name, Material material, short data, int amount, List<String> lore)
    {
        ItemStack stack = new ItemStack(material, data);
        stack.setDurability(data);
        ItemMeta meta = stack.getItemMeta();
        if (name != null)
            meta.setDisplayName(name);
        stack.setAmount(amount);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack getStack(String name, Material material, short data, int amount, String... lore)
    {
        return getStack(name, material, data, amount, Arrays.asList(lore));
    }


    public static boolean isAir(ItemStack is)
    {
        if (is == null) return true;
        return is.getType() == Material.AIR;
    }

    public static List<String> combineElements(List<String> list, String separator, int perElement)
    {
        List<String> n = new ArrayList<>();
        for (int i = 0; i < list.size(); i += perElement)
        {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < perElement; j++)
            {
                if (i + j > list.size() - 1)
                    break;
                builder.append(j != 0 ? separator : "").append(list.get(i + j));
            }
            n.add(builder.toString());
        }
        return n;
    }

    // not my code

    public static void setBlocks(Location c1, Location c2, Material material, boolean applyPhysics)
    {
        if (!c1.getWorld().getName().equals(c1.getWorld().getName()))
            return;
        int sy = Math.min(c1.getBlockY(), c2.getBlockY()),
                ey = Math.max(c1.getBlockY(), c2.getBlockY()),
                sx = Math.min(c1.getBlockX(), c2.getBlockX()),
                ex = Math.max(c1.getBlockX(), c2.getBlockX()),
                sz = Math.min(c1.getBlockZ(), c2.getBlockZ()),
                ez = Math.max(c1.getBlockZ(), c2.getBlockZ());
        World world = c1.getWorld();
        for (int y = sy; y <= ey; y++)
        {
            for (int x = sx; x <= ex; x++)
            {
                for (int z = sz; z <= ez; z++)
                {
                    world.getBlockAt(x, y, z).setType(material, applyPhysics);
                }
            }
        }
    }

    public static <T> T instance(Class<T> clazz, Object... params)
    {
        Class<?>[] paramClasses = new Class[params.length];
        for (int i = 0; i < paramClasses.length; i++)
            paramClasses[i] = params[i].getClass();
        try
        {
            Constructor<T> constructor = clazz.getConstructor(paramClasses);
            constructor.setAccessible(true);
            return constructor.newInstance(params);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex)
        {
            return null;
        }
    }

    public static <C, T> T getDeclaredField(C instance, String name, Class<T> type)
    {
        try
        {
            Field f = instance.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return type.cast(f.get(instance));
        }
        catch (NoSuchFieldException | IllegalAccessException ex)
        {
            return null;
        }
    }

    public static ChatColor getRandomVisibleColor()
    {
        return VISIBLE_COLOR_SPECTRUM.get(random(0, VISIBLE_COLOR_SPECTRUM.size() - 1));
    }


    public static ItemStack enchant(ItemStack stack)
    {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, true);
        stack.setItemMeta(meta);
        return stack;
    }


    // NOT MY CODE
    public static byte[] gzipCompress(byte[] uncompressedData)
    {
        byte[] result = new byte[]{};
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressedData.length);
             GZIPOutputStream gzipOS = new GZIPOutputStream(bos))
        {
            gzipOS.write(uncompressedData);
            // You need to close it before using bos
            gzipOS.close();
            result = bos.toByteArray();
        }
        catch (IOException ignored) {}
        return result;
    }

    // NOT MY CODE
    public static byte[] gzipUncompress(byte[] compressedData)
    {
        byte[] result = new byte[]{};
        try (ByteArrayInputStream bis = new ByteArrayInputStream(compressedData);
             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream gzipIS = new GZIPInputStream(bis))
        {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipIS.read(buffer)) != -1)
            {
                bos.write(buffer, 0, len);
            }
            result = bos.toByteArray();
        }
        catch (IOException ignored) {}
        return result;
    }

    public static double midpoint(int x, int y)
    {
        return (double) (x + y) / 2.0;
    }

    public static double midpoint(double x, double y)
    {
        return (x + y) / 2.0;
    }


    public static <T> T getOrDefault(List<T> list, int index, T def)
    {
        if (index < 0 || index >= list.size())
            return def;
        return list.get(index);
    }

    public static <T> T getOrDefault(T[] array, int index, T def)
    {
        if (index < 0 || index >= array.length)
            return def;
        return array[index];
    }

    public static String zeroed(long l)
    {
        return l > 9 ? "" + l : "0" + l;
    }

    public static String getFormattedTime(long t, int div)
    {
        long seconds = t / div; // 86400
        long hours = seconds / 3600; // 24
        seconds -= hours * 3600; // 86400 - 84600 = 0
        long minutes = seconds / 60; // 0
        seconds -= minutes * 60; // 59 * 60 = 3540
        return (hours != 0 ? hours + ":" : "") + zeroed(minutes) + ":" + zeroed(seconds);
    }

    public static String getFormattedTime(long ticks)
    {
        return getFormattedTime(ticks, 20);
    }

    public static String getSlayerFormattedTime(long millis)
    {
        long seconds = millis / 1000; // 86400
        long hours = seconds / 3600; // 24
        seconds -= hours * 3600; // 86400 - 84600 = 0
        long minutes = seconds / 60; // 0
        seconds -= minutes * 60; // 59 * 60 = 3540
        return (hours != 0 ? zeroed(hours) + "h" : "") + zeroed(minutes) + "m" + zeroed(seconds) + "s";
    }

    public static double quadrt(double d)
    {
        return Math.pow(d, 1.0 / 4.0);
    }


    // not my code
    public static float getLookAtYaw(Vector motion)
    {
        double dx = motion.getX();
        double dz = motion.getZ();
        double yaw = 0.0D;
        if (dx != 0.0D)
        {
            if (dx < 0.0D)
                yaw = 4.71238898038469D;
            else
                yaw = 1.5707963267948966D;
            yaw -= Math.atan(dz / dx);
        }
        else if (dz < 0.0D)
        {
            yaw = Math.PI;
        }
        return (float) (-yaw * 180.0D / Math.PI - 90.0D);
    }

    public static String ntify(int i)
    {
        if (i == 11 || i == 12 || i == 13)
            return i + "th";
        String s = String.valueOf(i);
        char last = s.charAt(s.length() - 1);
        switch (last)
        {
            case '1': return i + "st";
            case '2': return i + "nd";
            case '3': return i + "rd";
            default: return i + "th";
        }
    }

    public static String pad(String s, int length)
    {
        return String.format("%-" + length + "s", s);
    }

    // not my code cuz i was lazy lol
    public static <T> List<T> shuffle(List<T> list)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = list.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            T t = list.get(index);
            list.set(index, list.get(i));
            list.set(i, t);
        }
        return list;
    }

    public static <T> int deepLength(T[][] array2d)
    {
        int c = 0;
        for (T[] array : array2d)
            c += array.length;
        return c;
    }

    public static <T> T[] unnest(T[][] array2d, Class<T> clazz)
    {
        T[] array = (T[]) Array.newInstance(clazz, deepLength(array2d));
        for (int i = 0, c = 0; i < array2d.length; i++)
        {
            for (int j = 0; j < array2d[i].length; j++, c++)
                array[c] = array2d[i][j];
        }
        return array;
    }


    public static <T> T[] toArray(List<T> list, Class<T> clazz)
    {
        T[] array = (T[]) Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); i++)
            array[i] = list.get(i);
        return array;
    }

    public static boolean canFitStack(Inventory inventory, ItemStack fit)
    {
        for (ItemStack stack : inventory)
        {
            if (stack == null)
                continue;
            if (!fit.equals(stack))
                continue;
            if (stack.getAmount() + fit.getAmount() > 64)
                continue;
            return true;
        }
        return false;
    }

    public static int blackMagic(double d)
    {
        return ((Double) d).intValue();
    }

    public static String prettify(Object obj)
    {
        Class<?> clazz = obj.getClass();
        if (clazz == Location.class)
        {
            Location location = (Location) obj;
            return location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getWorld().getName() + ", " +
                    location.getYaw() + ", " + location.getPitch();
        }
        return "No pretty!";
    }

    public static String getAuctionFormattedTime(long millis)
    {
        if (millis == 0)
            return "Ended!";
        if (millis >= 8.64E7)
            return Math.round(millis / 8.64E7) + "d";
        if (millis >= 2.16E7)
            return Math.round(millis / 3.6E6) + "h";
        long seconds = millis / 1000; // 86400
        long hours = seconds / 3600; // 24
        seconds -= hours * 3600; // 86400 - 84600 = 0
        long minutes = seconds / 60; // 0
        seconds -= minutes * 60; // 59 * 60 = 3540
        StringBuilder builder = new StringBuilder();
        if (hours > 0)
            builder.append(hours).append("h ");
        builder.append(minutes).append("m ").append(seconds).append("s");
        return builder.toString();
    }

    public static String getAuctionSetupFormattedTime(long millis)
    {
        String dur;
        if (millis >= 8.64E7)
        {
            long days = Math.round(millis / 8.64E7);
            dur = days + " Day";
            if (days != 1) dur += "s";
        }
        else if (millis >= 3600000)
        {
            long hours = Math.round(millis / 3600000.0);
            dur = hours + " Hour";
            if (hours != 1) dur += "s";
        }
        else
        {
            long minutes = Math.round(millis / 60000.0);
            dur = minutes + " Minute";
            if (minutes != 1) dur += "s";
        }
        return dur;
    }

    public static Location stringToLocation(String locString) {
        if (locString == null) {
            return null;
        }

        String[] locData = locString.split(", ");
        World world = Bukkit.getWorld(locData[0]);

        Objects.requireNonNull(world, "world");

        return new Location(world, Double.parseDouble(locData[1]), Double.parseDouble(locData[2]), Double.parseDouble(locData[3]),
                Float.parseFloat(locData[4]), Float.parseFloat(locData[5]));
    }

    public static void redirectToServer(JavaPlugin plugin, Player player, String serverName){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(player.getName());
        out.writeUTF(serverName);
        plugin.getServer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public static ArmorStand throwItem(Player owner, ItemStack head, Location loc, Vector vec){
        ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
        armorStand.setItemInHand(head);
        armorStand.setVelocity(vec);
        armorStand.setVisible(false);
        return armorStand;
    }

    public static ItemStack getSingleLoreStack(String name, Material material, short data, int amount, String lore)
    {
        List<String> l = new ArrayList<>();
        for (String line : SUtils.splitByWordAndLength(lore, 30, "\\s"))
            l.add(ChatColor.GRAY + line);
        return getStack(name, material, data, amount, l.toArray(new String[]{}));
    }

}