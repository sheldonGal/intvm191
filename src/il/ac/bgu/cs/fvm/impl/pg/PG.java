package il.ac.bgu.cs.fvm.impl.pg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import il.ac.bgu.cs.fvm.impl.pg.util.PG_Validator;
import il.ac.bgu.cs.fvm.programgraph.PGTransition;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;

public class PG<L, A> implements ProgramGraph<L, A> {
	private String name;
	private HashSet<L> locations;
	private HashSet<L> initialLocation;
	private HashSet<PGTransition<L, A>> transitions;
	private HashSet<List<String>> initalizations;
    
    public PG(){
        name = "";
    	locations = new HashSet<L>();
    	initialLocation = new HashSet<L>();
    	transitions = new HashSet<PGTransition<L, A>>();
    	initalizations = new HashSet<List<String>>();
    }
	

	@Override
	public void addInitalization(List<String> init) {
		this.initalizations.add(init);
	}

	@Override
	public void setInitial(L location, boolean isInitial) {
        PG_Validator.getInstance().setInitial(this, location, isInitial);
        if(isInitial)
            this.initialLocation.remove(location);
        else
            this.initialLocation.add(location);


	}

	@Override
	public void addLocation(L l) {
		this.locations.add(l);
		
	}

	@Override
	public void addTransition(PGTransition<L,A> t) {
        //PG_Validator.getInstance().addTransition(this, t);
		this.transitions.add(t);
		
	}

	@Override
	public Set<List<String>> getInitalizations() {
		return this.initalizations;
	}

	@Override
	public HashSet<L> getInitialLocations() {
		return this.initialLocation;

	}

	@Override
	public HashSet<L> getLocations() {
		return this.locations;

	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Set<PGTransition<L, A>> getTransitions() {
		return this.transitions;
	}

	@Override
	public void removeLocation(L l) {
        PG_Validator.getInstance().removeLocation(this, l);
		this.locations.remove(l);		
	}

	@Override
	public void removeTransition(PGTransition<L,A> t) {
		this.transitions.remove(t);
		
	}

	@Override
	public void setName(String name) {
		this.name = name;		
	}

}
