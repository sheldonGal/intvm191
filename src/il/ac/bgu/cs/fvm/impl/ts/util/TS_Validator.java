package il.ac.bgu.cs.fvm.impl.ts.util;

import il.ac.bgu.cs.fvm.exceptions.*;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;
import java.util.HashSet;

public class TS_Validator {
    private static TS_Validator ourInstance = new TS_Validator();

    public static TS_Validator getInstance() {
        return ourInstance;
    }

    private TS_Validator() {
    }


    @SafeVarargs
    public final <S> boolean states(TransitionSystem<S,?,?> ts, boolean throwE, S... states) throws StateNotFoundException
    {
        for (S state : states){
            if (!ts.getStates().contains(state))
                if (throwE)
                    throw new StateNotFoundException(state);
                else
                    return false;
        }
        return true;
    }

    @SafeVarargs
    public final <A> boolean actions(TransitionSystem<?,A,?> ts, boolean throwE, A... actions) throws ActionNotFoundException
    {
        for (A action : actions){
            if (!ts.getActions().contains(action))
                if (throwE)
                    throw new ActionNotFoundException(action);
                else
                    return false;
        }
        return true;
    }

    @SafeVarargs
    private final <P> boolean aps(TransitionSystem<?, ?, P> ts, P... aps)
    {
        for (P ap : aps){
            if (!ts.getAtomicPropositions().contains(ap))
                    return false;
        }
        return true;
    }

    public <S,A> void addTransition(TransitionSystem<S,A,?> ts, Transition<S,A> transition) throws InvalidTransitionException
    {
        if (!states(ts,false, transition.getFrom(),transition.getTo()) ||
            !actions(ts,false,transition.getAction()))
            throw new InvalidTransitionException(transition);
    }

    public <S,P> void addStateLabeling(TransitionSystem<S,?,P> ts, S state, P ap) throws FVMException
    {
        states(ts,true,state);
        if (!aps(ts, ap))
            throw new InvalidLablingPairException(state,ap);
    }

    public <A> void removeAction(TransitionSystem<?,A,?> ts, A action) throws DeletionOfAttachedActionException
    {
        if (ts.getTransitions()
                .parallelStream()
                .anyMatch(t -> t.getAction().equals(action)))
            throw new DeletionOfAttachedActionException(action, TransitionSystemPart.TRANSITIONS);
    }

    public <P> void removeAP(TransitionSystem<?,?,P> ts, P ap) throws DeletionOfAttachedAtomicPropositionException
    {
        if (ts.getLabelingFunction()
                .values()
                .parallelStream()
                .anyMatch(lbl -> lbl.contains(ap)))
            throw new DeletionOfAttachedAtomicPropositionException(ap, TransitionSystemPart.LABELING_FUNCTION);
    }

    public <S,P> void removeState(TransitionSystem<S,?,P> ts, S state) throws DeletionOfAttachedStateException
    {
        if (ts.getInitialStates().contains(state))
            throw new DeletionOfAttachedStateException(state, TransitionSystemPart.INITIAL_STATES);
        if (!ts.getLabelingFunction().getOrDefault(state, new HashSet<>(0)).isEmpty())
            throw new DeletionOfAttachedStateException(state, TransitionSystemPart.LABELING_FUNCTION);
        if (ts.getTransitions()
                .parallelStream()
                .anyMatch(t -> t.getFrom().equals(state) || t.getTo().equals(state)))
            throw new DeletionOfAttachedStateException(state, TransitionSystemPart.TRANSITIONS);
    }




}
