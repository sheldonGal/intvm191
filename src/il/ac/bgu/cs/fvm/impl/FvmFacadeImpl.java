package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.FvmFacade;
import il.ac.bgu.cs.fvm.automata.Automaton;
import il.ac.bgu.cs.fvm.automata.MultiColorAutomaton;
import il.ac.bgu.cs.fvm.channelsystem.ChannelSystem;
import il.ac.bgu.cs.fvm.circuits.Circuit;
//import il.ac.bgu.cs.fvm.impl.ts.algorithms.*;
import il.ac.bgu.cs.fvm.impl.ts.TS;
import il.ac.bgu.cs.fvm.impl.ts.algorithms.Interleave;
import il.ac.bgu.cs.fvm.impl.ts.algorithms.Reachable;
import il.ac.bgu.cs.fvm.impl.ts.queries.ExecutionType;
import il.ac.bgu.cs.fvm.impl.ts.queries.IsDet;
import il.ac.bgu.cs.fvm.impl.ts.queries.Iter;
import il.ac.bgu.cs.fvm.ltl.LTL;
import il.ac.bgu.cs.fvm.programgraph.ActionDef;
import il.ac.bgu.cs.fvm.programgraph.ConditionDef;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;
import il.ac.bgu.cs.fvm.transitionsystem.AlternatingSequence;
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
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement createProgramGraph
    }

    @Override
    public <L1, L2, A> ProgramGraph<Pair<L1, L2>, A> interleave(ProgramGraph<L1, A> pg1, ProgramGraph<L2, A> pg2) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement interleave
    }

    @Override
    public TransitionSystem<Pair<Map<String, Boolean>, Map<String, Boolean>>, Map<String, Boolean>, Object> transitionSystemFromCircuit(Circuit c) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement transitionSystemFromCircuit
    }

    @Override
    public <L, A> TransitionSystem<Pair<L, Map<String, Object>>, A, String> transitionSystemFromProgramGraph(ProgramGraph<L, A> pg, Set<ActionDef> actionDefs, Set<ConditionDef> conditionDefs) {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement transitionSystemFromProgramGraph
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
