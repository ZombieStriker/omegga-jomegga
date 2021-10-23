# JOmegga
Adds the ability to load Java plugins for Omegga.

## How to create plugins
Add the following to your POM file.
```
    <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
    <dependencies>
        <dependency>
            <groupId>com.github.ZombieStriker</groupId>
            <artifactId>omegga-jomegga</artifactId>
            <version>main-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
```

From there, you just need to create your main class, and call the JOmegga.init(); method. Your code should look like:
```\
    public static void main(String... args){
        JOmegga.init();
        //Your code here
    }
```
    
## Credits

Zombie_Striker - JOmegga creator

smallguy - Java-BRS util creator.

cake - Omegga

