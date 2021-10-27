# JOmegga
Adds the ability to load Java plugins for Omegga.

## How to create plugins
//TODO: Upload the project to a maven repo.

For now, you will need to clone this repository and run ```mvn install``` to install a version of JOmegga on your system to include in the jar.

Add the following to your POM file.
``` 
<repositories>
        <repository>
            <id>local-maven-repo</id>
            <url>file:///${user.dir}/.m2</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>me.zombie_striker</groupId>
            <artifactId>JOmegga</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
```

From there, you just need to create your main class, and call the JOmegga.init(); method. Your code should look like:
```\
    public static void main(String... args){
        JOmegga.registerListener(/*YOUR LISTENER*/);
        JOmegga.init("PluginName");//Replace PluginName with your plugin's name
       
       //Your code
       
       //So it doesn't overload your system.
        while (JOmegga.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
```
    
## Credits

Zombie_Striker - JOmegga creator

smallguy - Java-BRS util creator.

cake - Omegga

