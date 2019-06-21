[![Javadocs](https://javadoc.io/badge/com.tchristofferson.pagedinventories/PagedInventories.svg)](https://javadoc.io/doc/com.tchristofferson.pagedinventories/PagedInventories)
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
    <version>1.2-SNAPSHOT</version>
</dependency>
```
### How to use
Create a PagedInventoryAPI instance (You should only create one):
```
PagedInventoryAPI api = new PagedInventoryAPI(plugin);
```
Create a PagedInventory like so (the buttons are item stacks you want to use for navigation buttons between pages):
```
PagedInventory pagedInventory = api.createPagedInventory(nextButton, previousButton, closeButton);
```
Add pages like so:
```
pagedInventory.addPage(inventory);
pagedInventory.addPage(contents, title, inventorySize);
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
