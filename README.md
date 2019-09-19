Javadocs(https://tchristofferson.github.io/PagedInventories/)
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
    <version>2.0-SNAPSHOT</version>
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
iPagedInventory.addHandler(new PagedInventoryClickHandler() {
    @Override
    public void handle(PagedInventoryClickHandler.Handler handler) {
        //Handle click here
    }
});
```
You can also add global handlers that get handled no matter which paged inventory was associated with it. So if you want a single handler to run for every paged inventory that gets clicked you would do:
```
registrar.addGlobalHandler(new PagedInventoryClickHandler() {
    @Override
    public void handle(PagedInventoryClickHandler.Handler handler) {
        //Handle click here, handle will be called regardless of which paged inventory it was
    }
});
```
