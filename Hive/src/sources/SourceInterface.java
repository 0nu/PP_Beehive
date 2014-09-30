package sources;

import hive.Bee;
import hive.Beehive;

import java.util.ArrayList;

/**
 * Interface for interaction bee <-> source.
 * 
 * @author ole
 * 
 */
public interface SourceInterface {

	/**
	 * The bee takes some food from the source.
	 * 
	 * @param source
	 *            source to take food from
	 * @param bee
	 *            bee that takes food from the source
	 */
	public void getStuff(Source source);

	

}
