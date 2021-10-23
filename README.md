# JOmegga
Adds the ability to load Java plugins for Omegga.

## Installation

`omegga install gh:ZombieStriker/jomegga`

## How to create plugins
First, you will need to include JOmegga to your project's build path. (I don't have a repository set up for JOmegga yet, so you will need to include it manually copy it to a /libs/ folder in your working directory and use the following maven code):
```
    <dependencies>
        <dependency>
            <groupId>me.zombie_striker</groupId>
            <artifactId>JOmegga</artifactId>
            <version>1.0-SNAPSHOT</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/libs/JOmegga.jar</systemPath>
        </dependency>
    </dependencies>
```

Now, to create a plugin, simply create a a new class in your jar (it can have any any, recommended to not use "Main.class"), have it extend OmeggaPlugin, and give it the annotation "OmeggaMain(author="NAME", pluginname)" Here's the absolute basics for a plugin
```

@OmeggaMain(author = "Zombie_Striker", pluginname = "TestPlugin")
public class BlockDrop extends OmeggaPlugin{

	public void onInit(){
		JOmegga.broadcast("hello world");
	}
	
	public void onStop(){
		//Called when the plugin is stopped. 
	}
	

}
```
Now, if you want to listen for events, you will need to create a new Listener and register it. To do that, add the following line:
```
        JOmegga.registerListener(new TestListener());
```

After that, you can start listening for events by adding a new method in the listener class, with the @EventHandler annotation, and with the **only** parameter being the event you want to listen for. For example, heres an example listener that broadcasts the message "pong" when someone says "ping":
```
public class TestListener implements Listener{
	
	
	@EventHandler
	public void onChat(ChatEvent event){
		if(event.getMessage().equalsIgnoreCase("ping")){
			JOmegga.broadcast("pong");
		}
	}
}
```

Finally, build your jar and place it in its own folder in the /plugins/ directory. EZ.

## Credits

Zombie_Striker - JOmegga creator

smallguy - Java-BRS util creator.

cake - Omegga

