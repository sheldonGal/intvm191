package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.FvmFacade;
import il.ac.bgu.cs.fvm.automata.Automaton;
import il.ac.bgu.cs.fvm.automata.MultiColorAutomaton;
import il.ac.bgu.cs.fvm.channelsystem.ChannelSystem;
import il.ac.bgu.cs.fvm.channelsystem.InterleavingActDef;
import il.ac.bgu.cs.fvm.channelsystem.ParserBasedInterleavingActDef;
import il.ac.bgu.cs.fvm.circuits.Circuit;
import il.ac.bgu.cs.fvm.impl.pg.PG;
import il.ac.bgu.cs.fvm.impl.pg.algorithms.PG_Reachable;
import il.ac.bgu.cs.fvm.impl.ts.TS;
import il.ac.bgu.cs.fvm.impl.ts.algorithms.Interleave;
import il.ac.bgu.cs.fvm.impl.ts.algorithms.Reachable;
import il.ac.bgu.cs.fvm.impl.ts.queries.ExecutionType;
import il.ac.bgu.cs.fvm.impl.ts.queries.IsDet;
import il.ac.bgu.cs.fvm.impl.ts.queries.Iter;
import il.ac.bgu.cs.fvm.ltl.LTL;
import il.ac.bgu.cs.fvm.nanopromela.NanoPromelaFileReader;
import il.ac.bgu.cs.fvm.nanopromela.NanoPromelaParser;
import il.ac.bgu.cs.fvm.nanopromela.NanoPromelaParser.StmtContext;
import il.ac.bgu.cs.fvm.programgraph.*;
import il.ac.bgu.cs.fvm.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.fvm.util.Pair;
import il.ac.bgu.cs.fvm.verification.VerificationResult;

import java.io.InputStream;
import java.util.*;

import static il.ac.bgu.cs.fvm.nanopromela.NanoPromelaFileReader.pareseNanoPromelaString;
import static il.ac.bgu.cs.fvm.nanopromela.NanoPromelaFileReader.parseNanoPromelaStream;

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

	//<editor-fold desc="PG_Reachable States">

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
		return new PG<>();
	}

	@Override
	public <L1, L2, A> ProgramGraph<Pair<L1, L2>, A> interleave(ProgramGraph<L1, A> pg1, ProgramGraph<L2, A> pg2) {
		ProgramGraph<Pair<L1, L2>, A> ans = new PG<>();

		Set<L1> initLocations1 = pg1.getInitialLocations();
		Set<L2> initLocations2 = pg2.getInitialLocations();
		for (L1 loc1 : pg1.getLocations()) {
			for (L2 loc2 : pg2.getLocations()) {
				Pair<L1,L2> newLoc = new Pair<>(loc1,loc2);
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
						ans.addTransition(new PGTransition<>(location1, tran.getCondition(), tran.getAction(), location2));
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
						ans.addTransition(new PGTransition<>(location1, tran.getCondition(), tran.getAction(), location2));
					}
				}
			}
		}

		for(List<String> init1 : pg1.getInitalizations()) {
			for(List<String> init2 : pg2.getInitalizations()) {
				List<String> mergedInitializations= new ArrayList<>();
				mergedInitializations.addAll(init1);
				mergedInitializations.addAll(init2);
				ans.addInitalization(mergedInitializations);
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
		CreateAllPossibleValues(inputs, new HashMap<>(), inputNames, 0);
		CreateAllPossibleValues(regs, new HashMap<>(), regNames, 0);
		AddLabelAPAndStatesToTSFromCircuit(ans, inputs, regs, c);
		AddTransitionsToTSFromCircuit(ans,inputs, c);
		removeUnreachableTransitionAndStates(ans);
		return ans;

	}
	private void AddTransitionsToTSFromCircuit(TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> ans, Set<Map<String, Boolean>> inputs, Circuit c) {
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
	}
	private void AddLabelAPAndStatesToTSFromCircuit(TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> ans, Set<Map<String, Boolean>> inputs, Set<Map<String, Boolean>> regs, Circuit c) {
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

				ans.setInitial(state, !init);

			}
		}
	}
	private void CreateAllPossibleValues(Set<Map<String, Boolean>> ans, Map<String, Boolean> map, ArrayList<String> inputNames, int inputIndex){
		if (inputIndex==inputNames.size()){
			ans.add(map);
		}else{
			HashMap<String,Boolean> inputTrue = new HashMap<>(map);
			HashMap<String,Boolean> inputFalse = new HashMap<>(map);

			inputTrue.put(inputNames.get(inputIndex),true);
			inputFalse.put(inputNames.get(inputIndex),false);

			CreateAllPossibleValues(ans,inputTrue,inputNames,inputIndex+1);
			CreateAllPossibleValues(ans,inputFalse,inputNames,inputIndex+1);
		}

	}
	private <S,A,AP> void removeUnreachableTransitionAndStates(TransitionSystem<S, A, AP> ts){
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
	public <L, A> TransitionSystem<Pair<L, Map<String, Object>>, A, String> transitionSystemFromProgramGraph
			(ProgramGraph<L, A> pg,Set<ActionDef> actionDefs, Set<ConditionDef> conditionDefs)
	{
		TransitionSystem<Pair<L, Map<String, Object>>, A, String> ans = createTransitionSystem();
		AddStatesToTSFromPG(ans,pg,actionDefs);
		Queue<Pair<L, Map<String, Object>>> currents = new LinkedList<>(ans.getInitialStates());
		Set<Pair<L, Map<String, Object>>> visited = new HashSet<>();

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

	private <L, A> void AddStatesToTSFromPG
			(TransitionSystem<Pair<L, Map<String, Object>>,A, String> ans, ProgramGraph<L,A> pg, Set<ActionDef> actionDefs) {
		boolean emptyInits = false;
		if (pg.getInitalizations().isEmpty()) {
			emptyInits = true;
			pg.addInitalization(new ArrayList<>());
		}
		for (L initLoc: pg.getInitialLocations()){
			Map<String, Object> evaluateVars = new HashMap<>();
			for (List<String> initList: pg.getInitalizations()){
				for (String s: initList){
					for (ActionDef a: actionDefs){
						if (a.isMatchingAction(s))
							evaluateVars = a.effect(evaluateVars, s);
					}
				}
				Pair<L, Map<String, Object>> initState = new Pair<>(initLoc, evaluateVars);
				ans.addState(initState);
				ans.setInitial(initState, true);
			}
		}
		if (emptyInits)
			pg.getInitalizations().clear();
	}

	@Override
	public <L, A> TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> transitionSystemFromChannelSystem(ChannelSystem<L, A> cs) {
		TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> ans = createTransitionSystem();
		List<ProgramGraph<L, A>> programGraphs = cs.getProgramGraphs();
		Set<ActionDef> setActDefs = new HashSet<>();
		InterleavingActDef actionDef = new ParserBasedInterleavingActDef();
		setActDefs.add(actionDef);
		setActDefs.add(new ParserBasedActDef());
		Set<ActionDef> complexActionDefSet = new HashSet<>();
		complexActionDefSet.add(new ParserBasedInterleavingActDef());
		ConditionDef conditionDef = new ParserBasedCondDef();
		Set<ConditionDef> setCondDefs = new HashSet<>();
		setCondDefs.add(conditionDef);

		List<Set<L>> initialLoc = new ArrayList<>();
		for (ProgramGraph<L, A> pg : programGraphs)
			initialLoc.add(pg.getInitialLocations());
		List<List<L>> allInitialLoc = wrapperCreateAllPossibleLists(initialLoc);


		List<Set<List<String>>> initializations = new ArrayList<>();
		for (ProgramGraph<L, A> pg : programGraphs)
			initializations.add(pg.getInitalizations());

		List<List<String>> mixedInitializations = new ArrayList<>();
		wrapperCreateAllPossibleLists(new ArrayList<>(initializations)).forEach(mixedInitializations::addAll);
		Set<Map<String, Object>> initials = new HashSet<>();
		for (List<String> initialization : mixedInitializations)
		{
			Map<String, Object> eval = new HashMap<>();
			for (String action : initialization)
				eval = ActionDef.effect(setActDefs, eval, action);
			initials.add(eval);
		}
		if (initials.size() == 0)
			initials.add(new HashMap<>());

		Set<Pair<List<L>, Map<String, Object>>> initStates = new HashSet<>();
		for (List<L> location : allInitialLoc)
			for (Map<String, Object> init : initials)
				initStates.add(new Pair<>(location, init));

		Queue<Pair<List<L>, Map<String, Object>>> currents = new LinkedList<>();
		for (Pair<List<L>, Map<String, Object>> state : initStates)
		{
			ans.addState(state);
			ans.setInitial(state, true);
			currents.add(state);

			addAPAndLabelToTSFromChanel(ans, state);

		}

		while (!currents.isEmpty())
		{
			Pair<List<L>, Map<String, Object>> currentNewLocation = currents.poll();
			Map<Integer, List<PGTransition<L, A>>> oneSideActions = new HashMap<>();
			for (int i = 0; i < programGraphs.size(); i++)
			{
				ProgramGraph<L, A> currentPg = programGraphs.get(i);
				L currentLocation = currentNewLocation.getFirst().get(i);

				for (PGTransition<L, A> pgTransition : currentPg.getTransitions())
				{
					if (pgTransition.getFrom().equals(currentLocation)) {
						if (ConditionDef.evaluate(setCondDefs, currentNewLocation.second, pgTransition.getCondition())) {
							A action = pgTransition.getAction();
							if (!actionDef.isOneSidedAction(action.toString())) {
								List<L> newLocation = new ArrayList<>(currentNewLocation.first);
								newLocation.set(i, pgTransition.getTo());
								addActAndTranToTSFromChanel(ans, setActDefs, currents, currentNewLocation, action, newLocation);
							} else {
								if (!oneSideActions.containsKey(i)) {
									oneSideActions.put(i, new ArrayList<>());
								}
								oneSideActions.get(i).add(pgTransition);
							}
						}
					}
				}
				if (oneSideActions.size() > 0)
				{
					List<Set<Pair<Integer, PGTransition<L, A>>>> allComplexTransitions = new ArrayList<>();
					for (Integer key : oneSideActions.keySet())
					{
						List<PGTransition<L, A>> transitions = oneSideActions.get(key);
						Set<Pair<Integer, PGTransition<L, A>>> set = new HashSet<>();
						for (PGTransition<L, A> transition : transitions)
						{
							set.add(new Pair<>(key, transition));
						}
						allComplexTransitions.add(set);
					}
					List<List<Pair<Integer, PGTransition<L, A>>>> allComplexTransitionPermutations = wrapperCreateAllPossibleLists(allComplexTransitions);
					for (List<Pair<Integer, PGTransition<L, A>>> complexTransition : allComplexTransitionPermutations)
					{
						StringBuilder action = new StringBuilder();
						List<L> newLocation = new ArrayList<>(currentNewLocation.first);
						List<A> actions = new ArrayList<>();
						for (Pair<Integer, PGTransition<L, A>> pair : complexTransition)
						{
							if (action.length() != 0)
								action.append("|");
							action.append(pair.second.getAction());
							actions.add(pair.second.getAction());
							newLocation.set(pair.first, pair.second.getTo());
						}
						if (!actionDef.isOneSidedAction(actions.toString()) && complexTransition.size() > 1)
							addActAndTranToTSFromChanel(ans, complexActionDefSet, currents, currentNewLocation, (A) action.toString(), newLocation);
					}
				}
			}
		}
		return ans;
	}


	private <L, A> void addActAndTranToTSFromChanel(TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> ans, Set<ActionDef> setActDefs, Queue<Pair<List<L>, Map<String, Object>>> currents, Pair<List<L>, Map<String, Object>> state, A action, List<L> new_location)
	{
		Map<String, Object> newEval = ActionDef.effect(setActDefs, state.second, action);
		if (newEval != null )
		{
			Pair<List<L>, Map<String, Object>> newState = new Pair<>(new_location, newEval);
			Transition<Pair<List<L>, Map<String, Object>>, A> transition = new Transition<>(state, action, newState);
			if (!ans.getStates().contains(newState))
			{
				currents.add(newState);
				ans.addState(newState);
			}
			ans.addAction(action);
			ans.addTransition(transition);
			addAPAndLabelToTSFromChanel(ans, newState);
		}
	}

	private <L, A> void addAPAndLabelToTSFromChanel(TransitionSystem<Pair<List<L>, Map<String, Object>>, A, String> ans, Pair<List<L>, Map<String, Object>> state) {
		for (L loc : state.first) {
			ans.addAtomicProposition(loc.toString());
			ans.addToLabel(state, loc.toString());
		}
		for (Map.Entry<String, Object> entry : state.second.entrySet()) {
			String ap = entry.getKey() + " = " + entry.getValue().toString();
			ans.addAtomicProposition(ap);
			ans.addToLabel(state, ap);
		}
	}

	private <T> List<List<T>> wrapperCreateAllPossibleLists(List<Set<T>> items){
		List<List<T>> ans = new ArrayList<>();
		createAllPossibleLists(items,ans,new ArrayList<>(),0);
		return ans;
	}

	private <T> void createAllPossibleLists(List<Set<T>> items, List<List<T>> ans, List<T> currList, int pos){
		if(pos == items.size()){
			ans.add(currList);
		}else {
			Set<T> indexList = items.get(pos);

			if (indexList.isEmpty())
				createAllPossibleLists(items, ans, currList, pos + 1);

			for (T item : indexList) {
				List<T> currentList = new ArrayList<>(currList);
				currentList.add(item);
				createAllPossibleLists(items, ans, currentList, pos + 1);
			}
		}
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromela(String filename) throws Exception {
		return programGraphFromStmtContext(pareseNanoPromelaString(filename));
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromelaString(String nanopromela) throws Exception {
		return programGraphFromStmtContext(pareseNanoPromelaString(nanopromela));
	}

	@Override
	public ProgramGraph<String, String> programGraphFromNanoPromela(InputStream inputStream) throws Exception {
		return programGraphFromStmtContext(parseNanoPromelaStream(inputStream));
	}

	private ProgramGraph<String, String> programGraphFromStmtContext(NanoPromelaParser.StmtContext context) {
		ProgramGraph<String, String> ans = createProgramGraph();
		Set<String> locations = new HashSet<>();
		locations = sub(context, locations, ans);

		ans.addLocation(context.getText());
		ans.setInitial(context.getText(), true);
		ans.getLocations().addAll(locations);

		Set<String> reachable = new PG_Reachable<String>().reach(ans);

		ans.getLocations().clear();
		ans.getLocations().addAll(reachable);

		Set<PGTransition<String, String>> transitions = new HashSet<>(ans.getTransitions());

		for (PGTransition<String, String> tran : transitions) {
			if (!(ans.getLocations().contains(tran.getFrom()) && ans.getLocations().contains(tran.getTo()))) {
				ans.removeTransition(tran);
			}
		}

		return ans;
	}

	private Set<String> sub(NanoPromelaParser.StmtContext context, Set<String> locations, ProgramGraph<String, String> pg) {
		if (context.assstmt() != null || context.chanreadstmt() != null || context.chanwritestmt() != null
				|| context.atomicstmt() != null || context.skipstmt() != null) subBasicStmt(locations, context, pg);
		else if (context.ifstmt() != null) subIfStmt(locations, context, pg);
		else if (context.dostmt() != null) subDoStmt(locations, context, pg);
		else subComplexStmt(locations, context, pg);
		return locations;
	}
	private void subBasicStmt(Set<String> locations, StmtContext context, ProgramGraph<String, String> pg) {
		locations.add(context.getText());
		String from = context.getText();
		String to = "";
		String condition = "";
		String action;

		if (context.assstmt() == null && context.chanreadstmt() == null && context.chanwritestmt() == null && context.atomicstmt() == null) {
			action = "skip";
		}
		else
			action = context.getText();

		pg.addLocation(from);
		pg.addLocation(to);
		pg.addTransition(new PGTransition<>(from, condition, action, to));
	}
	private void subComplexStmt(Set<String> locations, StmtContext context, ProgramGraph<String, String> pg) {
		Set<String> temp = sub(context.stmt(0), new HashSet<>(), pg);
		temp.remove("");
		locations.addAll(sub(context.stmt(1), new HashSet<>(), pg));
		for (String str : temp) {
			locations.add(str + ";" + context.stmt(1).getText());
			addTransition(NanoPromelaFileReader
					.pareseNanoPromelaString(addSpaces(str) + " ; " + addSpaces(context.stmt(1).getText())), pg);
		}
		addTransition(context, pg);
	}
	private void subDoStmt(Set<String> locations, StmtContext context, ProgramGraph<String, String> pg) {
		locations.add("");
		locations.add(context.getText());
		pg.addLocation(context.getText());
		pg.addLocation("");
		List<NanoPromelaParser.OptionContext> options = context.dostmt().option();

		handleDoOptions(context, locations, pg, options);
	}
	private void subIfStmt(Set<String> locations, StmtContext context, ProgramGraph<String, String> pg) {
		locations.add(context.getText());
		pg.addLocation(context.getText());
		List<NanoPromelaParser.OptionContext> options = context.ifstmt().option();
		for (NanoPromelaParser.OptionContext option : options)
			locations.addAll(sub(option.stmt(), new HashSet<>(), pg));
		handleIfOptions(context, pg, options);
	}
	private void handleIfOptions(NanoPromelaParser.StmtContext context, ProgramGraph<String, String> pg, List<NanoPromelaParser.OptionContext> options) {
		Set<PGTransition<String, String>> transitions = new HashSet<>(pg.getTransitions());

		for (NanoPromelaParser.OptionContext option : options) {
			String from = option.stmt().getText();
			for (PGTransition<String, String> transition : transitions) {
				if (transition.getFrom().equals(from)) {
					String condition;
					if (transition.getCondition().equals(""))
						condition = "(" + option.boolexpr().getText() + ")";
					else
						condition = "(" + option.boolexpr().getText() + ") && (" + transition.getCondition() + ")";

					pg.addLocation(transition.getTo());
					pg.addTransition(new PGTransition<>(context.getText(), condition, transition.getAction(),
							transition.getTo()));
				}
			}
		}
	}
	private void handleDoOptions(StmtContext context, Set<String> locations, ProgramGraph<String, String> pg, List<NanoPromelaParser.OptionContext> options) {
		for (NanoPromelaParser.OptionContext option : options) {
			Set<String> temp = sub(option.stmt(), new HashSet<>(), pg);
			temp.remove("");

			String doStmt = addSpaces(context.getText());
			for (String s : temp) {
				locations.add(s + ";" + context.getText());
				addTransition(NanoPromelaFileReader.pareseNanoPromelaString(addSpaces(s) + " ; " + doStmt),
						pg);
			}
		}

		String rules = "";
		for (NanoPromelaParser.OptionContext option : options)
			rules += "(" + option.boolexpr().getText() + ")||";

		pg.addTransition(new PGTransition<>(context.getText(),
				"!(" + rules.substring(0, rules.length() - 2) + ")", "", ""));

		Set<PGTransition<String, String>> transitions = new HashSet<>(pg.getTransitions());

		for (NanoPromelaParser.OptionContext option : options) {
			for (PGTransition<String, String> transition : transitions) {
				if (transition.getFrom().equals(option.stmt().getText())) {
					String from = context.getText();
					String condition;
					if(transition.getCondition().equals(""))
						condition = "(" + option.boolexpr().getText() + ")";
					else
						condition = "(" + option.boolexpr().getText() + ") && (" + transition.getCondition() + ")";
					String action = transition.getAction();
					String to;
					if (transition.getTo().equals(""))
						to = context.getText();
					else
						to = transition.getTo() + ";" + context.getText();

					pg.addLocation(from);
					pg.addLocation(to);
					pg.addTransition(new PGTransition<>(from, condition, action, to));
				}
			}
		}
	}
	private void addTransition(StmtContext context, ProgramGraph<String, String> pg) {
		pg.addLocation(context.getText());
		Set<PGTransition<String, String>> transitions = new HashSet<>(pg.getTransitions());
		for (PGTransition<String, String> transition : transitions) {
			if (transition.getFrom().equals(context.stmt(0).getText())) {
				String to = transition.getTo().equals("") ? context.stmt(1).getText() : (transition.getTo() + ";" + context.stmt(1).getText()) ;
				pg.addLocation(to);
				pg.addTransition(new PGTransition<>(context.getText(), transition.getCondition(), transition.getAction(), to));
			}
		}
	}
	private String addSpaces(String str) {
		str = str.replace("atomic", "atomic ");
		str = str.replace("skip", " skip");
		str = str.replace("fi", " fi");
		str = str.replace("if", "if ");
		str = str.replace("od", " od");
		str = str.replace("do", "do ");
		str = str.replace("->", " -> ");
		str = str.replace("::", ":: ");
		return str;
	}



	@Override
	public <Sts, Saut, A, P> TransitionSystem<Pair<Sts, Saut>, A, Saut> product(TransitionSystem<Sts, A, P> ts, Automaton<Saut, P> aut) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement product
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
