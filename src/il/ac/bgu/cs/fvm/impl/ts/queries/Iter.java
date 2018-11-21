package il.ac.bgu.cs.fvm.impl.ts.queries;

import il.ac.bgu.cs.fvm.impl.ts.util.TS_Validator;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Iter {

    //<editor-fold desc="Singleton">

    private static Iter ourInstance = new Iter();

    public static Iter getInstance() {
        return ourInstance;
    }

    private Iter() {
    }

    //</editor-fold>

    //<editor-fold desc="Public Methods:">

    //<editor-fold desc="Is State Terminal:">

    public <S,A> boolean isStateTerminal(TransitionSystem<S,A,?> ts, S s, boolean validate){
        validate(validate,ts,s);
        return _isStateTerminal(ts,s);
    }

    //</editor-fold>

    //<editor-fold desc="Post:">

    public <S,A> Set<S> post(TransitionSystem<S,A,?> ts, S s, boolean validate){
        validate(validate,ts,s);
        return _post(ts,s);
    }

    public <S,A> Set<S> post(TransitionSystem<S,A,?> ts, Set<S> c, boolean validate){
        if (validate)
            return multiStep(c, s -> post(ts,s,true));
        return multiStep(c, s -> _post(ts,s));
    }

    public <S,A> Set<S> post(TransitionSystem<S,A,?> ts, S s, A a, boolean validate){
        validate(validate,ts,s,a);
        return _post(ts,s,a);
    }

    public <S,A> Set<S> post(TransitionSystem<S,A,?> ts, Set<S> c, A a, boolean validate){
        if (validate)
            return multiStep(c, s -> post(ts,s,a,true));
        return multiStep(c, s -> _post(ts,s,a));
    }

    //</editor-fold>

    //<editor-fold desc="Pre:">

    public <S,A> Set<S> pre(TransitionSystem<S,A,?> ts, S s, boolean validate){
        validate(validate,ts,s);
        return _pre(ts,s);
    }

    public <S,A> Set<S> pre(TransitionSystem<S,A,?> ts, Set<S> c, boolean validate){
        if (validate)
            return multiStep(c, s -> pre(ts,s,true));
        return multiStep(c, s -> _pre(ts,s));
    }

    public <S,A> Set<S> pre(TransitionSystem<S,A,?> ts, S s, A a, boolean validate){
        validate(validate,ts,s,a);
        return _pre(ts,s,a);
    }

    public <S,A> Set<S> pre(TransitionSystem<S,A,?> ts, Set<S> c, A a, boolean validate){
        if (validate)
            return multiStep(c, s -> pre(ts,s,a,true));
        return multiStep(c, s -> _pre(ts,s,a));
    }

    //</editor-fold>

    //</editor-fold>



    //<editor-fold desc="Private Methods">

    //<editor-fold desc="Algorithms: ">
    private <S,Data> Set<S> step(Supplier<Set<Data>> data,
                                 Predicate<Data> pred,
                                 Function<Data,S> mapper){
        return data.get()
                .parallelStream()
                .filter(pred)
                .map(mapper)
                .collect(Collectors.toSet());
    }



    private <S> Set<S> multiStep(Set<S> c,
                                   Function<S, Set<S>> expand){
        return c.parallelStream()
                .flatMap(s -> expand.apply(s).stream())
                .collect(Collectors.toSet());
    }
    //</editor-fold>

    //<editor-fold desc="Validations:">

    private <S> void validate(boolean validate, TransitionSystem<S,?,?> ts, S state){
        if (validate)
            TS_Validator.getInstance().states(ts,true, state);
    }

    private <S,A> void validate(boolean validate, TransitionSystem<S,A,?> ts, S state, A action){
        validate(validate,ts,state);
        if (validate)
            TS_Validator.getInstance().actions(ts,true, action);
    }

    //</editor-fold>

    //<editor-fold desc="Implementations:">

    private <S> boolean _isStateTerminal(TransitionSystem<S,?,?> ts, S s){
        return _post(ts,s).isEmpty();
    }

    private <S> Set<S> _post(TransitionSystem<S,?,?> ts, S s){
        return step(ts::getTransitions,
                transition -> transition.getFrom().equals(s),
                Transition::getTo);
    }

    private <S,A> Set<S> _post(TransitionSystem<S,A,?> ts, S s, A a){
        return step(ts::getTransitions,
                transition ->  transition.getFrom().equals(s) && transition.getAction().equals(a),
                Transition::getTo);
    }

    private <S> Set<S> _pre(TransitionSystem<S,?,?> ts, S s){
        return step(ts::getTransitions,
                transition -> transition.getTo().equals(s),
                Transition::getFrom);
    }

    private <S,A> Set<S> _pre(TransitionSystem<S,A,?> ts, S s, A a){
        return step(ts::getTransitions,
                transition -> transition.getTo().equals(s) && transition.getAction().equals(a),
                Transition::getFrom);
    }

    //</editor-fold>

    //</editor-fold>

}
