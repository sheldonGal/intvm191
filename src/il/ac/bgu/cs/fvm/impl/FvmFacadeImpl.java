package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.FvmFacade;
import il.ac.bgu.cs.fvm.automata.Automaton;
import il.ac.bgu.cs.fvm.automata.MultiColorAutomaton;
import il.ac.bgu.cs.fvm.channelsystem.ChannelSystem;
import il.ac.bgu.cs.fvm.circuits.Circuit;
import il.ac.bgu.cs.fvm.impl.pg.PG;
import il.ac.bgu.cs.fvm.impl.ts.TS;
import il.ac.bgu.cs.fvm.impl.ts.algorithms.Interleave;
import il.ac.bgu.cs.fvm.impl.ts.algorithms.Reachable;
import il.ac.bgu.cs.fvm.impl.ts.queries.ExecutionType;
import il.ac.bgu.cs.fvm.impl.ts.queries.IsDet;
import il.ac.bgu.cs.fvm.impl.ts.queries.Iter;
import il.ac.bgu.cs.fvm.ltl.LTL;
import il.ac.bgu.cs.fvm.programgraph.ActionDef;
import il.ac.bgu.cs.fvm.programgraph.ConditionDef;
import il.ac.bgu.cs.fvm.programgraph.PGTransition;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;
import il.ac.bgu.cs.fvm.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.fvm.util.Pair;
import il.ac.bgu.cs.fvm.verification.VerificationResult;
import java.io.InputStream;
import java.util.*;

/**
 * Implement the methods in this class. You may add additional classes as you
 * want, as long as they live in the {@code impl} package, or one of its 
 * sub-packages.
 */
public class FvmFacadeImpl implements FvmFacade {

	public FvmFacadeImpl(){
		var processors = Runtime.getRuntime().availableProcessors();
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + processors);
	}
	//<editor-fold desc="Transition System Methods">

	//<editor-fold desc="Initialization">
	@Override
	public <S, A, P> TransitionSystem<S, A, P> createTransitionSystem() {
		return new TS<>();
	}

	//</editor-fold>

	//<editor-fold desc="isDeterministic :: isAPDet, isActDet">
	@Override
	public <S, A, P> boolean isActionDeterministic(TransitionSystem<S, A, P> ts) {
		return IsDet.getInstance().isActionDeterministic(ts);
	}

	@Override
	public <S, A, P> boolean isAPDeterministic(TransitionSystem<S, A, P> ts) {
		return IsDet.getInstance().isAPDeterministic(ts);
	}
	//</editor-fold>

	//<editor-fold desc="Path Type :: execution, max, initial, ...">
	@Override
	public <S, A, P> boolean isExecution(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return ExecutionType.getInstance().isExecution(ts,e);
	}

	@Override
	public <S, A, P> boolean isExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return ExecutionType.getInstance().isExecutionFragment(ts,e);
	}

	@Override
	public <S, A, P> boolean isInitialExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return ExecutionType.getInstance().isInitialExecutionFragment(ts,e);
	}

	@Override
	public <S, A, P> boolean isMaximalExecutionFragment(TransitionSystem<S, A, P> ts, AlternatingSequence<S, A> e) {
		return ExecutionType.getInstance().isMaximalExecutionFragment(ts,e);
	}
	//</editor-fold>

	//<editor-fold desc="Is State Terminal?">
	@Override
	public <S, A> boolean isStateTerminal(TransitionSystem<S, A, ?> ts, S s) {
		return Iter.getInstance().isStateTerminal(ts,s,true);
	}
	//</editor-fold>

	//<editor-fold desc="Post and Pre methods">
	@Override
	public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, S s) {
		return Iter.getInstance().post(ts,s,true);
	}

	@Override
	public <S> Set<S> post(TransitionSystem<S, ?, ?> ts, Set<S> c) {
		return Iter.getInstance().post(ts,c,true);
	}

	@Override
	public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, S s, A a) {
		return Iter.getInstance().post(ts,s,a,true);
	}

	@Override
	public <S, A> Set<S> post(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
		return Iter.getInstance().post(ts,c,a,true);
	}

	@Override
	public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, S s) {
		return Iter.getInstance().pre(ts,s,true);
	}

	@Override
	public <S> Set<S> pre(TransitionSystem<S, ?, ?> ts, Set<S> c) {
		return Iter.getInstance().pre(ts,c,true);
	}

	@Override
	public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, S s, A a) {
		return Iter.getInstance().pre(ts,s,a,true);
	}

	@Override
	public <S, A> Set<S> pre(TransitionSystem<S, A, ?> ts, Set<S> c, A a) {
		return Iter.getInstance().pre(ts,c,a,true);
	}

	//</editor-fold>

	//<editor-fold desc="Reachable States">

	@Override
	public <S, A> Set<S> reach(TransitionSystem<S, A, ?> ts) {

		Reachable<S> iter = new Reachable<>(ts);
		return iter.reachable();
	}

	//</editor-fold>

	//</editor-fold>


	@Override
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2) {
		return new Interleave<>(ts1,ts2,new HashSet<>()).interleave();
	}


	@Override
	public <S1, S2, A, P> TransitionSystem<Pair<S1, S2>, A, P> interleave(TransitionSystem<S1, A, P> ts1, TransitionSystem<S2, A, P> ts2, Set<A> handShakingActions) {
		return new Interleave<>(ts1,ts2,handShakingActions).interleave();
	}

	@Override
	public <L, A> ProgramGraph<L, A> createProgramGraph() {
		return new PG<L,A>();
	}

	@Override
	public <L1, L2, A> ProgramGraph<Pair<L1, L2>, A> interleave(ProgramGraph<L1, A> pg1, ProgramGraph<L2, A> pg2) {
		ProgramGraph<Pair<L1, L2>, A> ans = new PG<Pair<L1, L2>, A>();

		Set<L1> initLocations1 = pg1.getInitialLocations();
		Set<L2> initLocations2 = pg2.getInitialLocations();
		for (L1 loc1 : pg1.getLocations()) {
			for (L2 loc2 : pg2.getLocations()) {
				Pair<L1,L2> newLoc = new Pair<L1,L2>(loc1,loc2);
				ans.addLocation(newLoc);

				boolean isInitial = initLocations1.contains(loc1) && initLocations2.contains(loc2);
				ans.setInitial(newLoc, isInitial);
			}
		}

		if (pg1.getName()!=null && pg2.getName()!=null)
			ans.setName(pg1.getName()+'_'+pg2.getName());


		for (PGTransition<L1, A> tran: pg1.getTransitions()) {
			for (Pair<L1,L2> location1 : ans.getLocations()) {
				for (Pair<L1,L2> location2 : ans.getLocations()) {
					if (location1.equals(location2))
						continue;
					if (location1.getFirst().equals(tran.getFrom()) && location2.getFirst().equals(tran.getTo()) 
							&& location1.getSecond().equals(location2.getSecond())) {
						ans.addTransition(new PGTransition<Pair<L1,L2>, A>(location1, tran.getCondition(), tran.getAction(), location2));
					}
				}
			}
		}

		for (PGTransition<L2, A> tran: pg2.getTransitions()) {
			for (Pair<L1,L2> location1 : ans.getLocations()) {
				for (Pair<L1,L2> location2 : ans.getLocations()) {
					if (location1.equals(location2))
						continue;
					if (location1.getSecond().equals(tran.getFrom()) && location2.getSecond().equals(tran.getTo()) && location1.getFirst().equals(location2.getFirst())) {
						ans.addTransition(new PGTransition<Pair<L1,L2>, A>(location1, tran.getCondition(), tran.getAction(), location2));
					}
				}
			}
		}

		List<String> tempList;
		for(List<String> init1 : pg1.getInitalizations()) {
			for(List<String> init2 : pg2.getInitalizations()) {
				tempList = new ArrayList<String>();
				tempList.addAll(init1);
				tempList.addAll(init2);
				ans.addInitalization(tempList);
			}
		}

		return ans;    	


	}

	@Override
	public TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> transitionSystemFromCircuit(Circuit c) {
		TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> ans = new TS<>();

		ArrayList<String> inputNames = new ArrayList<>();
		ArrayList<String> regNames = new ArrayList<>();
		for (String s: c.getInputPortNames()) {
			inputNames.add(s);
			ans.addAtomicProposition(s);
		}
		for (String s: c.getRegisterNames()) {
			regNames.add(s);
			ans.addAtomicProposition(s);
		}

		Set<Map<String, Boolean>> inputs = new HashSet<>();
		Set<Map<String, Boolean>> regs = new HashSet<>();
		createMap(inputs, new HashMap<>(), inputNames, 0);
		createMap(regs, new HashMap<>(), regNames, 0);

		for (Map<String, Boolean> input: inputs) {
			ans.addAction(input);
			for (Map<String, Boolean> reg : regs) {
				Pair<Map<String, Boolean>, Map<String, Boolean>> state = new Pair<>(input, reg);
				ans.addState(state);

				for (String s: input.keySet()){
					if (input.get(s))
						ans.addToLabel(state, s);
				}
				boolean init = false;
				for (String s: reg.keySet()){
					if (reg.get(s)) {
						ans.addToLabel(state, s);
						init = true;
					}
				}
				Map<String, Boolean> outputs = c.computeOutputs(input, reg);
				for (String s: outputs.keySet()){
					ans.addAtomicProposition(s);
					if (outputs.get(s))
						ans.addToLabel(state, s);
				}
				ans.setInitial(state, init);

			}
		}

		for (Pair<Map<String, Boolean>, Map<String, Boolean>> state: ans.getStates()){
			Map<String, Boolean> nextRegs = c.updateRegisters(state.getFirst(), state.getSecond());
			for (Map<String, Boolean> input: inputs) {
				for (Pair<Map<String, Boolean>, Map<String, Boolean>> newState : ans.getStates()) {
					if (newState.getFirst().equals(input) && newState.getSecond().equals(nextRegs)) {						
						ans.addTransition(new Transition<>(state, input, newState));
					}
				}
			}
		}


		cleanUnreachable(ans);
		return ans;
	}
	
	private void createMap(Set<Map<String, Boolean>> ans, Map<String, Boolean> map, ArrayList<String> inputNames, int inputIndex){
		if (inputIndex==inputNames.size()){
			ans.add(map);
		}else{
			HashMap<String,Boolean> inputTrue = new HashMap<>(map);
			HashMap<String,Boolean> inputFalse = new HashMap<>(map);

			inputTrue.put(inputNames.get(inputIndex),true);
			inputFalse.put(inputNames.get(inputIndex),false);

			createMap(ans,inputTrue,inputNames,inputIndex+1);
			createMap(ans,inputFalse,inputNames,inputIndex+1);
		}
	}
	private <S,A,AP> void cleanUnreachable(TransitionSystem<S, A, AP> ts){
		Set<S> reach = reach(ts);
		HashSet<S> sToRemove = new HashSet<>();
		HashSet<Transition<S, A>> tToRemove = new HashSet<>();
		for (S state1 : ts.getStates()) {
			if (!reach.contains(state1)) {
				sToRemove.add(state1);
			}
		}

		for (S state1 : sToRemove) {
			for (Transition<S, A> tran: ts.getTransitions()) {
				if (tran.getFrom().equals(state1) || tran.getTo().equals(state1))
					tToRemove.add(tran);
			}
		}

		for (Transition<S, A> tran: tToRemove)
			ts.removeTransition(tran);
		for (S state1 : sToRemove) {
			ts.getLabel(state1).removeAll(ts.getLabel(state1));
			ts.removeState(state1);
		}
	}
	
	
	
	

	@Override
	public <L, A> TransitionSystem<Pair<L, Map<String, Object>>, A, String> transitionSystemFromProgramGraph(ProgramGraph<L, A> pg, Set<ActionDef> actionDefs, Set<ConditionDef> conditionDefs) {
		boolean emptyInits = false;
		if (pg.getInitalizations().isEmpty()) {
			emptyInits = true;
			pg.addInitalization(new ArrayList<>());
		}
		TransitionSystem<Pair<L, Map<String, Object>>, A, String> ans = new TS<>();
		for (L initLoc: pg.getInitialLocations()){
			Map<String, Object> eta = new HashMap<>();
			for (List<String> initList: pg.getInitalizations()){
				for (String s: initList){
					for (ActionDef a: actionDefs){
						if (a.isMatchingAction(s))
							eta = a.effect(eta, s);
					}
				}
				Pair<L, Map<String, Object>> initState = new Pair<>(initLoc, eta);
				ans.addState(initState);
				ans.setInitial(initState, true);
			}
		}
		if (emptyInits)
			pg.getInitalizations().clear();

		Queue<Pair<L, Map<String, Object>>> currents = new LinkedList<>();
		Set<Pair<L, Map<String, Object>>> visited = new HashSet<>();
		currents.addAll(ans.getInitialStates());

		while (!currents.isEmpty()){
			Pair<L, Map<String, Object>> currentState = currents.poll();
			if (!visited.contains(currentState)) {
				visited.add(currentState);

				for (Map.Entry<String, Object> var : currentState.getSecond().entrySet()){
					ans.addAtomicProposition(var.getKey() + " = " + var.getValue());
					ans.addToLabel(currentState, var.getKey() + " = " + var.getValue());
				}
				ans.addAtomicProposition(currentState.getFirst().toString());
				ans.addToLabel(currentState, currentState.getFirst().toString());
				for (PGTransition<L, A> tran : pg.getTransitions()) {
					Map<String, Object> eta = new HashMap<>(currentState.getSecond());
					if (tran.getFrom().equals(currentState.getFirst())) {
						for (ConditionDef cond : conditionDefs) {
							if (cond.evaluate(currentState.getSecond(), tran.getCondition())){
								for(ActionDef a : actionDefs){
									if(a.isMatchingAction(tran.getAction())) {
										eta = a.effect(eta, tran.getAction());
									}
								}
								Pair<L,Map<String,Object>> state = new Pair<>(tran.getTo(),eta);
								ans.addState(state);
								currents.add(state);

								ans.addAction(tran.getAction());
								ans.addTransition(new Transition<>(currentState,tran.getAction(),state));
							}
						}
					}
				}

			}
		}
		return ans;
		}

	@Override
	public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(ChannelSystem<L, A> cs) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement transitionSystemFromChannelSystem
	}

	@Override
	public <Sts, Saut, A, P> TransitionSystem<Pair<Sts, Saut>, A, Saut> product(TransitionSystem<Sts, A, P> ts, Automaton<Saut, P> aut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement product
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromela(String filename) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement programGraphFromNanoPromela
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromelaString(String nanopromela) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement programGraphFromNanoPromelaString
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromela(InputStream inputStream) throws Exception {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement programGraphFromNanoPromela
	}

	//STOP HERE
	@Override
	public <S, A, P, Saut> VerificationResult<S> verifyAnOmegaRegularProperty(TransitionSystem<S, A, P> ts, Automaton<Saut, P> aut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement verifyAnOmegaRegularProperty
	}

	@Override
	public <L> Automaton<?, L> LTL2NBA(LTL<L> ltl) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement LTL2NBA
	}

	@Override
	public <L> Automaton<?, L> GNBA2NBA(MultiColorAutomaton<?, L> mulAut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement GNBA2NBA
	}

}
