package il.ac.bgu.cs.fvm.impl.pg.util;


import il.ac.bgu.cs.fvm.impl.pg.PG;
import il.ac.bgu.cs.fvm.programgraph.PGTransition;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;

public class PG_Validator {
	private static PG_Validator ourInstance = new PG_Validator();

	public static PG_Validator getInstance() {
		return ourInstance;
	}

	private PG_Validator() {
	}


	public <L,A> void setInitial(ProgramGraph<L,A> pg, Object location, boolean isInitial) throws IllegalArgumentException
	{
		if (!pg.getLocations().contains(location))
			throw new IllegalArgumentException();
		if (!isInitial && pg.getInitialLocations().contains(location))
			throw new IllegalArgumentException();
	}

	/*public <L,A> void addTransition(PG<L, A> pg, PGTransition t) {
		if (!pg.getLocations().contains(t.getFrom()) || !pg.getLocations().contains(t.getTo()))
			throw new RuntimeException();

	}*/

	public <L,A> void removeLocation(PG<L, A> pg, L l) {
		if (pg.getInitialLocations().contains(l))
			throw new RuntimeException();
		for (PGTransition<L, A> t: pg.getTransitions()) {
			if (t.getFrom().equals(l) || t.getTo().equals(l))
				throw new RuntimeException();
		}
	}




}
