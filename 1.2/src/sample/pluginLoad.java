package sample;

import com.test.plugin.plugin;
import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class pluginLoad {

    private plugin[] Plugins;
    private String[] PluginsNames;

    pluginLoad(){
        File pluginDir = new File("plugins");

        File[] jars = pluginDir.listFiles(file -> file.isFile() && file.getName().endsWith(".jar"));

        Plugins = new plugin[jars.length];
        PluginsNames = new String[jars.length];
        for (int i = 0; i < jars.length; i++) {
            try {
                URL jarURL = jars[i].toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL});
                Class pluginClass = classLoader.loadClass("com.test.pluginBase");
                Plugins[i]=(plugin) pluginClass.newInstance();
                PluginsNames[i] = Plugins[i].pluginName();
            } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] encoding(byte[] text, int PluginIndex)
    {
        byte[] encode = null;
        try {
            encode = Plugins[PluginIndex].encode(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    public String encoding(String text, int PluginIndex)
    {
        String encode = null;
        try {
            encode = Plugins[PluginIndex].encode(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    public byte[] decoding(byte[] text, int PluginIndex)
    {
        byte[] encode = null;
        try {
            encode = Plugins[PluginIndex].decode(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    public String decoding(String text, int PluginIndex)
    {
        String decode = null;
        try {
            decode = Plugins[PluginIndex].decode(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decode;
    }

    public String[] getPluginsNames (){
        return PluginsNames;
    }
}
