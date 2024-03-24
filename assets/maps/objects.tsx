<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.2" name="objects" tilewidth="32" tileheight="32" tilecount="1" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0" type="EntityDef">
  <properties>
   <property name="GameObject" propertytype="GameObject" value="FROG"/>
   <property name="entityTags" propertytype="EntityTag" value="PLAYER,CAMERA_FOCUS"/>
   <property name="hasAnimation" type="bool" value="true"/>
   <property name="hasState" type="bool" value="true"/>
   <property name="jumpHeight" type="float" value="3.2"/>
   <property name="life" type="int" value="4"/>
   <property name="speed" type="float" value="8"/>
   <property name="timeToMaxSpeed" type="float" value="4.5"/>
  </properties>
  <image width="32" height="32" source="../graphics/object/frog.png"/>
  <objectgroup draworder="index" id="2">
   <object id="3" type="FixtureDef" x="7" y="11" width="18" height="20">
    <properties>
     <property name="isSensor" type="bool" value="true"/>
     <property name="userData" value="hitbox"/>
    </properties>
   </object>
   <object id="7" x="9" y="11">
    <polyline points="0,2 0,18"/>
   </object>
   <object id="8" type="FixtureDef" x="24" y="11">
    <polyline points="0,2 0,18"/>
   </object>
   <object id="13" type="FixtureDef" x="9" y="28" width="15" height="4">
    <properties>
     <property name="friction" type="float" value="1"/>
     <property name="isChain" type="bool" value="true"/>
    </properties>
   </object>
   <object id="14" type="FixtureDef" x="9" y="13">
    <properties>
     <property name="restitution" type="float" value="0.2"/>
    </properties>
    <polyline points="0,0 8,-2 15,0"/>
   </object>
  </objectgroup>
 </tile>
</tileset>
