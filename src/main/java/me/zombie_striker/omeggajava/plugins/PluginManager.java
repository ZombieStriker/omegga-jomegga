package me.zombie_striker.omeggajava.plugins;

import me.zombie_striker.omeggajava.JOmegga;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginManager {

    private List<OmeggaPlugin> omeggaPlugins = new LinkedList<>();

    public void importJar(File file) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        addURL.setAccessible(true);
        addURL.invoke(URLClassLoader.getSystemClassLoader(), new Object[] {file.toURI().toURL()});
    }

    public List<OmeggaPlugin> findOmeggaPlugins(List<File> files) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        ArrayList<OmeggaPlugin> omeggaPlugins = new ArrayList<OmeggaPlugin>();

        URL[] urlarray = new URL[files.size()];
        for(int i = 0; i < files.size();i++){
            File file = files.get(i);
            urlarray[i] = file.toURI().toURL();
        }
        URLClassLoader classLoader = URLClassLoader.newInstance(urlarray);
        for(File file : files) {
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    name = name.substring(0, name.lastIndexOf('.'));
                    Class<?> cls;
                    try {
                        cls = Class.forName(name.replaceAll("/","."), true, classLoader);
                    }catch(ClassNotFoundException e){
                        JOmegga.log("ERROR: "+e.getMessage());
                        cls = Class.forName(name.substring(name.lastIndexOf("/")+1), true, classLoader);
                    }
                    if (cls.isAnnotationPresent(OmeggaMain.class)) {
                        try {
                            omeggaPlugins.add((OmeggaPlugin) cls.newInstance());
                            OmeggaMain annotation = cls.getAnnotation(OmeggaMain.class);
                            JOmegga.log("Loading plugin " + annotation.pluginname() + " by " + annotation.author());
                        } catch (ClassCastException e) {
                            JOmegga.log("Failed to load class " + cls.getName() + " because it is not an OmeggaPlugin");
                        }
                    }
                }
            }
        }
        return omeggaPlugins;
    }

    public void scanForJars(File pluginDir,File jomeggafile){
        List<File> jars = new LinkedList<>();
        for(File dir : pluginDir.listFiles()){
            for(File file : dir.listFiles()){
                if(file.getName().endsWith(".jar") && !file.equals(jomeggafile)){
                    jars.add(file);
                }
            }
        }

        try {
            List<OmeggaPlugin> plugins = findOmeggaPlugins(jars);
            if(plugins.size() > 0) {
                omeggaPlugins.addAll(plugins);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    public void loadPlugins(){
        for(OmeggaPlugin plugins : omeggaPlugins){
            plugins.onInit();
        }
    }
    public void shutdownPlugins(){
        for(OmeggaPlugin plugin : omeggaPlugins){
            plugin.onStop();
        }
    }

}
