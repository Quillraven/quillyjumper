<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.2" name="objects" tilewidth="64" tileheight="64" tilecount="6" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0" type="EntityDef">
  <properties>
   <property name="entityTags" propertytype="EntityTag" value="PLAYER,CAMERA_FOCUS"/>
   <property name="gameObject" propertytype="GameObject" value="FROG"/>
   <property name="hasAnimation" type="bool" value="true"/>
   <property name="hasState" type="bool" value="true"/>
   <property name="initialState" value="IDLE"/>
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
   <object id="13" type="FixtureDef" x="9" y="28" width="15" height="5">
    <properties>
     <property name="friction" type="float" value="1"/>
     <property name="isChain" type="bool" value="true"/>
     <property name="userData" value="feet"/>
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
   <property name="bodyType" propertytype="BodyType" value="KinematicBody"/>
   <property name="damage" type="int" value="1"/>
   <property name="entityTags" propertytype="EntityTag" value=""/>
   <property name="gameObject" propertytype="GameObject" value="SAW"/>
   <property name="hasAnimation" type="bool" value="true"/>
   <property name="hasTrack" type="bool" value="true"/>
   <property name="initialState" value=""/>
   <property name="speed" type="float" value="3"/>
  </properties>
  <image width="38" height="38" source="../graphics/object/saw.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="FixtureDef" x="3" y="3" width="32" height="32">
    <properties>
     <property name="density" type="float" value="100"/>
     <property name="isChain" type="bool" value="true"/>
     <property name="isSensor" type="bool" value="true"/>
     <property name="userData" value="hitbox"/>
    </properties>
    <ellipse/>
   </object>
  </objectgroup>
 </tile>
 <tile id="2" type="EntityDef">
  <properties>
   <property name="bodyType" propertytype="BodyType" value="DynamicBody"/>
   <property name="damage" type="int" value="1"/>
   <property name="gameObject" propertytype="GameObject" value="ROCK_HEAD"/>
   <property name="gravityScale" type="float" value="0"/>
   <property name="hasAggro" type="bool" value="true"/>
   <property name="hasAnimation" type="bool" value="true"/>
   <property name="initialState" value="ROCK_HEAD_IDLE"/>
   <property name="speed" type="float" value="9"/>
   <property name="timeToMaxSpeed" type="float" value="3"/>
  </properties>
  <image width="42" height="42" source="../graphics/object/rock-head.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="FixtureDef" x="6" y="6" width="30" height="30">
    <properties>
     <property name="density" type="float" value="100"/>
     <property name="userData" value="hitbox"/>
    </properties>
   </object>
   <object id="2" type="FixtureDef" x="6" y="-64" width="30" height="170">
    <properties>
     <property name="isChain" type="bool" value="false"/>
     <property name="isSensor" type="bool" value="true"/>
     <property name="userData" value="aggroSensor"/>
    </properties>
   </object>
   <object id="3" type="FixtureDef" x="-64" y="6" width="170" height="30">
    <properties>
     <property name="isChain" type="bool" value="false"/>
     <property name="isSensor" type="bool" value="true"/>
     <property name="userData" value="aggroSensor"/>
    </properties>
   </object>
  </objectgroup>
 </tile>
 <tile id="3" type="EntityDef">
  <properties>
   <property name="bodyType" propertytype="BodyType" value="KinematicBody"/>
   <property name="entityTags" propertytype="EntityTag" value="COLLECTABLE"/>
   <property name="gameObject" propertytype="GameObject" value="CHERRY"/>
   <property name="gravityScale" type="float" value="0"/>
   <property name="hasAnimation" type="bool" value="true"/>
   <property name="initialState" value=""/>
  </properties>
  <image width="32" height="32" source="../graphics/object/cherry.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="FixtureDef" x="11" y="15" width="10" height="6">
    <properties>
     <property name="isChain" type="bool" value="true"/>
     <property name="isSensor" type="bool" value="true"/>
    </properties>
   </object>
  </objectgroup>
 </tile>
 <tile id="4" type="EntityDef">
  <properties>
   <property name="bodyType" propertytype="BodyType" value="KinematicBody"/>
   <property name="gameObject" propertytype="GameObject" value="START_FLAG"/>
   <property name="gravityScale" type="float" value="0"/>
  </properties>
  <image width="19" height="36" source="../graphics/object/start-flag.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="FixtureDef" x="0" y="0" width="19" height="36">
    <properties>
     <property name="isChain" type="bool" value="true"/>
     <property name="isSensor" type="bool" value="true"/>
    </properties>
   </object>
  </objectgroup>
 </tile>
 <tile id="6" type="EntityDef">
  <properties>
   <property name="bodyType" propertytype="BodyType" value="KinematicBody"/>
   <property name="entityTags" propertytype="EntityTag" value="COLLECTABLE"/>
   <property name="gameObject" propertytype="GameObject" value="FINISH_FLAG"/>
   <property name="gravityScale" type="float" value="0"/>
   <property name="hasAnimation" type="bool" value="true"/>
  </properties>
  <image width="64" height="64" source="../graphics/object/finish-flag.png"/>
  <objectgroup draworder="index" id="2">
   <object id="1" type="FixtureDef" x="22" y="19" width="3" height="45">
    <properties>
     <property name="isChain" type="bool" value="true"/>
     <property name="isSensor" type="bool" value="true"/>
    </properties>
   </object>
  </objectgroup>
 </tile>
</tileset>
