package il.ac.bgu.cs.fvm.impl.ts;

import il.ac.bgu.cs.fvm.exceptions.*;
import il.ac.bgu.cs.fvm.impl.ts.util.TS_Validator;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

import java.util.*;

public class TS<S,ACT,AP> implements TransitionSystem<S,ACT,AP> {
    private String name;
    private Set<S> states;
    private Set<S> I;
    private Set<ACT> actions;
    private Set<AP> aps;
    private Map<S,Set<AP>> labels;
    private Set<Transition<S,ACT>> transitions;

    private final Set<AP> emptyAps = Collections.emptySet();


    public TS(){
        name = "";
        states = new HashSet<>();
        I = new HashSet<>();
        actions = new HashSet<>();
        aps = new HashSet<>();
        labels = new HashMap<>();
        transitions = new HashSet<>();
    }


    //<editor-fold desc="Name :: get, set">


    @Override
    public String getName() {
        return name;
    }


    @Override
    public void setName(String name) {
        this.name = name;
    }

    //</editor-fold>

    //<editor-fold desc="States :: get, add, remove">

    @Override
    public Set<S> getStates() {
        return states;
    }

    @Override
    public void addState(S s) {
        states.add(s);
    }

    @Override
    public void removeState(S s) throws FVMException {
        TS_Validator.getInstance().removeState(this,s);
        states.remove(s);
    }

    //</editor-fold>

    //<editor-fold desc="Initial States :: get,set">

    @Override
    public Set<S> getInitialStates() {
        return I;
    }

    @Override
    public void setInitial(S aState, boolean isInitial) throws StateNotFoundException {
        TS_Validator.getInstance().states(this, true, aState);
        if (isInitial)
            I.add(aState);
        else
            I.remove(aState);
    }


    //</editor-fold>

    //<editor-fold desc="Action :: get, add, remove">

    @Override
    public Set<ACT> getActions() {
        return actions;
    }

    @Override
    public void addAction(ACT anAction) {
        actions.add(anAction);
    }

    @Override
    public void removeAction(ACT act) throws FVMException {
        TS_Validator.getInstance().removeAction(this, act);
        actions.remove(act);
    }

    //</editor-fold>

    //<editor-fold desc="Atomic Propositions :: get, add, remove">

    @Override
    public Set<AP> getAtomicPropositions() {
        return aps;
    }

    @Override
    public void addAtomicProposition(AP p) {
        aps.add(p);
    }

    @Override
    public void removeAtomicProposition(AP p) throws FVMException {
        TS_Validator.getInstance().removeAP(this, p);
        aps.remove(p);
    }

    //</editor-fold>

    //<editor-fold desc="Labeling function :: getLabel(S), addLabel(S,AP), getLabelingFunction(), removeLabel(S,AP) ">
    @Override
    public Set<AP> getLabel(S s) {
        TS_Validator.getInstance().states(this, true, s);
        return labels.getOrDefault(s, emptyAps);
    }

    @Override
    public void addToLabel(S s, AP l) throws FVMException {
        TS_Validator.getInstance().addStateLabeling(this, s, l);

        if (!labels.containsKey(s))
            labels.put(s,new HashSet<>());
        labels.get(s).add(l);
    }

    @Override
    public Map<S, Set<AP>> getLabelingFunction() {
        return labels;
    }

    @Override
    public void removeLabel(S s, AP l) {
        labels.getOrDefault(s,emptyAps).remove(l);
    }
    //</editor-fold>

    //<editor-fold desc="Transition :: get, add, remove">
    @Override
    public Set<Transition<S, ACT>> getTransitions() {
        return transitions;
    }

    @Override
    public void addTransition(Transition<S, ACT> t) throws FVMException {
        TS_Validator.getInstance().addTransition(this, t);
        transitions.add(t);
    }

    @Override
    public void removeTransition(Transition<S, ACT> t) {
        transitions.remove(t);
    }

    //</editor-fold>

}
