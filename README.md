# PagedInventories
API for easily creating multi paged inventory GUIs in Spigot/Bukkit (Minecraft)

### Requirements
Java 8 and Spigot / Bukkit

### Adding Paged Inventories as a dependency
Is on Maven Central
#### Maven
```
<repository>
    <id>pagedinventories-repo</id>
    <url>https://oss.sonatype.org/content/groups/public/</url>
</repository>
```
```
<dependency>
    <groupId>com.tchristofferson.pagedinventories</groupId>
    <artifactId>PagedInventories</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
#### Gradle Groovy
```
implementation 'com.tchristofferson.pagedinventories:PagedInventories:1.0'
```
#### Gradle Kotlin
```
compile("com.tchristofferson.pagedinventories:PagedInventories:1.0")
```
### How to use
Create a PagedInventoryAPI instance (You should only create one):
```
PagedInventoryAPI api = new PagedInventoryAPI(plugin);
```
#### Handlers
Handlers are similar to events. In this example a PagedInventorySwitchHandler is created. The handle method is called when a player goes to the next/previous page in a paged inventory.

Creation and registration:
```
registrar.addSwitchHandler(new PagedInventorySwitchPageHandler() {
            @Override
            public void handle(Handler handler) {
                System.out.println("\n\nSWITCH HANDLER\n\n");
            }
        });
```
