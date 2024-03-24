<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.2" name="objects" tilewidth="38" tileheight="38" tilecount="2" columns="0">
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
   <object id="3" type="FixtureDef" x="7" y="10" width="18" height="23">
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
 <tile id="1" type="EntityDef">
  <properties>
   <property name="GameObject" propertytype="GameObject" value="SAW"/>
   <property name="bodyType" propertytype="BodyType" value="KinematicBody"/>
   <property name="damage" type="int" value="1"/>
   <property name="entityTags" propertytype="EntityTag" value=""/>
   <property name="hasAnimation" type="bool" value="true"/>
   <property name="hasTrack" type="bool" value="true"/>
   <property name="speed" type="float" value="3"/>
  </properties>
  <image width="38" height="38" source="../graphics/object/saw.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="FixtureDef" x="3" y="3" width="32" height="32">
    <properties>
     <property name="density" type="float" value="100"/>
     <property name="isChain" type="bool" value="true"/>
     <property name="userData" value="hitbox"/>
    </properties>
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
</tileset>
