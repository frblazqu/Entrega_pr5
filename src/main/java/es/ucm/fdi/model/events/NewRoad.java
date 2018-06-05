package es.ucm.fdi.model.events;

import java.security.InvalidParameterException;
import java.util.Map;

import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.util.StringParser;

public class NewRoad extends Event 
{
	protected String road_id;
	protected String junctionIniId;
	protected String junctionDestId;
	protected int length;
	protected int maxSpeed;

	public NewRoad(int time, String id, String iniId, String destId, int size, int mSpeed) 
	{
		super(time);
		
		road_id = id;
		junctionIniId = iniId;
		junctionDestId = destId;
		length = size;
		maxSpeed = mSpeed;
	}
	
	public String getTag() {
		return "new_road";
	}
	public void describe(Map<String, String> out) {
		super.describe(out);
		out.put("Type", "New Road " + road_id);
	}
	public void execute(RoadMap map) throws IllegalArgumentException 
	{
		if (map.duplicatedId(road_id))
			throw new IllegalArgumentException("Ya existe un objeto con el id " + road_id);

		// Cogemos el cruce de destino de la carretera
		Junction dest = map.getJunction(junctionDestId);
		Junction ini = map.getJunction(junctionIniId);
		
		if(dest == null || ini == null)
			throw new InvalidParameterException("Cruce no existente.");
		
		// Creamos la carretera, la añadimos al mapa y al cruce de destino
		Road road = new Road(road_id, maxSpeed, length, dest, ini);
		map.addRoad(road);
		dest.añadirCarreteraEntrante(road);
	}

	public static class NewRoadBuilder implements EventBuilder {
		public Event parse(IniSection sec) throws IllegalArgumentException 
		{
			if (!sec.getTag().equals("new_road")) 
				return null;
			else 
			{
				try {
					int time 	= StringParser.parseTime(sec.getValue("time"));
					String id 	= StringParser.parseId(sec.getValue("id"));
					String src 	= StringParser.parseId(sec.getValue("src"));
					String dest = StringParser.parseId(sec.getValue("dest"));
					int mSpeed 	= StringParser.parseIntValue(sec.getValue("max_speed"));
					int l 		= StringParser.parseIntValue(sec.getValue("length"));

					if (sec.getValue("type") == null)
						return new NewRoad(time, id, src, dest, l, mSpeed);
					else if (sec.getValue("type").equals("dirt"))
						return new NewPath(time, id, src, dest, l, mSpeed);
					else if (sec.getValue("type").equals("lanes")) {
						int lanes = StringParser.parseIntValue(sec.getValue("lanes"));
						return new NewFreeway(time, id, src, dest, l, mSpeed, lanes);
					} else
						return null;
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException(
							"Algo ha fallado con los atributos.\n" + e.getMessage(), e);
				}
			}
		}
	}	
}