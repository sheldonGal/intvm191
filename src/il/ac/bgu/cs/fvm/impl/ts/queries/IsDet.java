package il.ac.bgu.cs.fvm.impl.ts.queries;

import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

import java.util.function.Predicate;

public class IsDet {

    //<editor-fold desc="Singleton">

    private static IsDet ourInstance = new IsDet();

    public static IsDet getInstance() {
        return ourInstance;
    }

    private IsDet() {
    }

    //</editor-fold>

    public <S, A> boolean isActionDeterministic(TransitionSystem<S, A, ?> ts) {
        return check(ts, this::isActDet);
    }


    public <S, A> boolean isAPDeterministic(TransitionSystem<S, A, ?> ts) {
        return check(ts, this::isApDet);
    }

    //<editor-fold desc="Private Methods:">

    private <S,A> boolean check(TransitionSystem<S,A,?> ts, Predicate<TransitionSystem<S,A,?>> algorithm){
        if (ts.getInitialStates().size() > 1)
            return false;
        return algorithm.test(ts);
    }

    private <S,A> boolean isActDet(TransitionSystem<S,A,?> ts){
        return ts.getStates().parallelStream()
                .noneMatch(s ->
                        ts.getActions().parallelStream()
                                .anyMatch(a -> Iter.getInstance().post(ts,s,a,false).size() > 1));
    }

    private <S,A> boolean isApDet(TransitionSystem<S,A,?> ts){
        return ts.getStates().parallelStream()
                .noneMatch(s ->
                        Iter.getInstance().post(ts,s,false).parallelStream()
                                .filter(si -> ts.getLabel(s).equals(ts.getLabel(si)))
                                .count() > 1);
    }

    //</editor-fold>

}
