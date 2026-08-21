<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.2" name="colony_tileset" tilewidth="32" tileheight="32" tilecount="64" columns="8">
 <image source="../textures/space_station.png" width="256" height="256"/>
 <tile id="0">
  <properties>
   <property name="solid" type="bool" value="true"/>
   <property name="friction" type="float" value="0.8"/>
  </properties>
 </tile>
 <tile id="7">
  <animation>
   <frame tileid="7" duration="150"/>
   <frame tileid="8" duration="150"/>
   <frame tileid="9" duration="150"/>
  </animation>
 </tile>
</tileset>
