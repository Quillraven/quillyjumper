<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.2" name="objects" tilewidth="32" tileheight="32" tilecount="1" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="GameObject" value="PLAYER"/>
  </properties>
  <image width="32" height="32" source="../graphics/object/frog.png"/>
  <objectgroup draworder="index" id="2">
   <object id="3" x="8" y="10" width="17" height="16"/>
   <object id="6" x="8" y="15" width="17" height="17">
    <ellipse/>
   </object>
   <object id="7" x="7" y="2" width="18" height="6">
    <ellipse/>
   </object>
   <object id="8" x="4" y="3">
    <polygon points="0,0 -3,3 -2,9 -1,5"/>
   </object>
   <object id="9" x="27" y="4">
    <polygon points="0,0 0,14 2,6"/>
   </object>
   <object id="10" x="1" y="15">
    <polyline points="0,0 6,17"/>
   </object>
  </objectgroup>
 </tile>
</tileset>
