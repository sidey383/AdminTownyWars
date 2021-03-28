<h1>AdminTownyWars</h1>
<p>Minecraft bukkit plugin that extends the functionality of <a href="https://github.com/TownyAdvanced/Towny">Towny</a>.</p>
<p>Adds admin-controlled wars.</p>
<h2>Functional</h2>
<p>Players can capture chunks using white flags in time of battle.</p>
<p>Sets pvp enabled in cities, disables teleportation, custom respawn location during the battle.</p>
<h2>Commands</h2>
<p><b>/war help</b> - Description of commands.</p>
<p><b>/war declare &ltFirst Town&gt &ltSecond Town&gt</b> - Start war. The battle does not begin</p>
<p><b>/war list</b> - Get list of wars and battles.</p>
<p><b>/war battle start &ltAttacker&gt &ltDefender&gt</b> - Start the battle. To do this, you need to start a war between these towns.</p>
<p><b>/war battle start &ltWar id&gt</b> - End battle.</p>
<p><b>/war setRespawn</b> - Set default respawn location.</p>
<p><b>/war removeRespawn</b> - Remove default respawn location.</p>
<p><b>/war setRespawn &ltTown&gt</b> - Set respawn location for town</p>
<p><b>/war removeRespawn &ltTown&gt</b> - remove respawn location for town</p>
<p><b>/war reload</b> - Relod plugin configuration.</p>
<h2>Permissions</h2>
<table>
<tr><th><h3>description</h3></th><th><h3>Permission</h3></th></tr>
<tr><th>use /war battle</th><th>townywars.command.war.battle</th></tr>
<tr><th>use /war list</th><th>townywars.command.war.list</th></tr>
<tr><th>use /war declare</th><th>townywars.command.war.declare</th></tr>
<tr><th>use /war end</th><th>townywars.command.war.end</th></tr>
<tr><th>use /war help</th><th>townywars.command.war.help</th></tr>
<tr><th>use /war reload</th><th>townywars.command.war.reload</th></tr>
<tr><th>use /war removeRespawn</th><th>townywars.command.war.respawn</th></tr>
<tr><th>use /war setRespawn</th><th>townywars.command.war.respawn</th></tr>
<tr><th>Ignore the ban on toggle pvp in the town during the war</th><th>townywars.toggle.pvp</th></tr>
<tr><th>Ignore the ban on leaving the town during the war</th><th>townywars.leavetown</th></tr>
<tr><th>Ignore the ban on teleport during the war</th><th>townywars.teleport</th></tr>
</table>
