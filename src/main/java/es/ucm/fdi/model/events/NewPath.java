package es.ucm.fdi.model.events;

import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.model.objects.Road;
import es.ucm.fdi.model.objects.RoadMap;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.model.objects.Junction;
import es.ucm.fdi.model.objects.Path;
import es.ucm.fdi.model.objects.RoadMap.ConexionCruces;

public class NewPath extends NewRoad {

	public static class NewPathBuilder implements EventBuilder {

		@Override
		public Event parse(IniSection sec) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public NewPath() {

	}

	public NewPath(int time, String id, String src, String dest, int l, int mSpeed) {
		super(time, id, src, dest, l, mSpeed);
	}

	public void execute(RoadMap map) throws IllegalArgumentException {
		if (!map.duplicatedId(road_id)) {
			try {
				if (map.validJuctionsForRoad(junctionIniId, junctionDestId)) {
					Junction junc = map.getJunction(junctionDestId);
					Junction ini = map.getJunction(junctionIniId);
					Road road = new Path(road_id, maxSpeed, length, junc, ini);
					map.addRoad(road);
					junc.añadirCarreteraEntrante(road);
					ConexionCruces conJunct = new ConexionCruces(road_id, junctionDestId);

					if (map.getConectionMap().containsKey(junctionIniId)) {
						map.getConectionMap().get(junctionIniId).add(conJunct);
					} else {
						List<ConexionCruces> connect = new ArrayList<ConexionCruces>();
						connect.add(conJunct);
						map.getConectionMap().put(junctionIniId, connect);
					}
				}
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Algo ha fallado con los cruces especificados.\n"
								+ e.getMessage(),
						e);
			}
		} else {
			throw new IllegalArgumentException(
					"Ya existe un objeto con el id " + road_id + '.');
		}
	}
}
