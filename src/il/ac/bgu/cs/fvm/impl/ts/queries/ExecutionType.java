package il.ac.bgu.cs.fvm.impl.ts.queries;

import il.ac.bgu.cs.fvm.impl.ts.sequence_iterator.AlternatingSeqIterator;
import il.ac.bgu.cs.fvm.impl.ts.util.TS_Validator;
import il.ac.bgu.cs.fvm.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

public class ExecutionType {

    //<editor-fold desc="Singleton:">

    private static ExecutionType ourInstance = new ExecutionType();

    public static ExecutionType getInstance() {
        return ourInstance;
    }

    private ExecutionType() {
    }

    //</editor-fold>

    //<editor-fold desc="Public methods:">

    public <S, A> boolean isExecution(TransitionSystem<S,A,?> ts, AlternatingSequence<S, A> e) {
        return isInitial(ts,e) &&
                isTerminal(ts,e.last()) &&
                isExecutionFragment(ts,e);
    }

    public <S,A> boolean isExecutionFragment(TransitionSystem<S,A,?> ts, AlternatingSequence<S, A> e) {
        return isExecutionFragment(e,ts);
    }

    public  <S,A> boolean isInitialExecutionFragment(TransitionSystem<S,A,?> ts, AlternatingSequence<S, A> e) {
        return isInitial(ts,e) &&
                isExecutionFragment(ts,e);
    }

    public  <S,A> boolean isMaximalExecutionFragment(TransitionSystem<S,A,?> ts, AlternatingSequence<S, A> e) {
        return isTerminal(ts,e.last()) &&
                isExecutionFragment(ts,e);
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods:">

    private <S,A> boolean isInitial(TransitionSystem<S,A,?> ts, AlternatingSequence<S,A> e){
        return ts.getInitialStates().contains(e.head());
    }

    private <S> boolean isTerminal(TransitionSystem<S,?,?> ts, S state){
        return Iter.getInstance().isStateTerminal(ts, state, true);
    }

    private <S,A> boolean isExecutionFragment(AlternatingSequence<S,A> e, TransitionSystem<S,A,?> ts){
        AlternatingSeqIterator<S,A> iterator = new AlternatingSeqIterator<>(e);

        if (e.size() == 1 &&
                TS_Validator.getInstance().states(ts,true,e.head())) // in case of a sequence of a single state.
            return true;

        while(iterator.hasNext()){

            iterator.next().validate(ts);

            if (ts.getTransitions()
                    .parallelStream()
                    .noneMatch(iterator::eq))
                return false;
        }
        return true;
    }

    //</editor-fold>





}
