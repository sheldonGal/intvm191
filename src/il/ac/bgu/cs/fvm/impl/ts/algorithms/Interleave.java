package il.ac.bgu.cs.fvm.impl.ts.algorithms;

import il.ac.bgu.cs.fvm.impl.ts.TS;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;
import il.ac.bgu.cs.fvm.util.Pair;
import il.ac.bgu.cs.fvm.util.Util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Interleave<S1,S2,A,P> {

    //<editor-fold desc="Class Attributes:">

    // arguments
    private final TransitionSystem<S1,A,P> ts1;
    private final TransitionSystem<S2,A,P> ts2;
    private final Set<A> handshake;

    // result
    private final TransitionSystem<Pair<S1,S2>,A,P> res;

    // algorithm utils
    private final Queue<Pair<S1,S2>> toReach;
    private final UnaryLogic<S1> ul1;
    private final UnaryLogic<S2> ul2;
    private final BinaryLogic bl;

    // current state tracker
    private Pair<S1,S2> currState = null;

    //</editor-fold>

    //<editor-fold desc="Constructor(TransitionSystem<S1,A,P>, TransitionSystem<S2,A,P>, Set<A>)">

    public Interleave(TransitionSystem<S1,A,P> ts1, TransitionSystem<S2,A,P> ts2, Set<A> handshake){
        this.ts1 = ts1;
        this.ts2 = ts2;
        this.handshake = handshake;

        this.res = new TS<>();
        res.setName(ts1.getName() + "-" + ts2.getName());

        // initialize toReach with all initial states
        var initials = Util.getPairs(ts1.getInitialStates(),ts2.getInitialStates());
        toReach = new ArrayDeque<>(initials);

        // initialize logic helpers
        ul1 = new UnaryLogic<>(t -> Pair.pair(t.getTo(), currState.second));
        ul2 = new UnaryLogic<>(t -> Pair.pair(currState.first,t.getTo()));
        bl = new BinaryLogic(ul1,ul2);
    }

    //</editor-fold>

    //<editor-fold desc="Public Methods:">

    public TransitionSystem<Pair<S1,S2>,A,P> interleave(){

        while ( !toReach.isEmpty() )
        {
            currState = toReach.poll();
            res.addState(currState);

            Set<Transition<S1,A>> post1 = post(ts1,currState.first);
            Set<Transition<S2,A>> post2 = post(ts2,currState.second);

            if (post1.isEmpty() && post2.isEmpty())
                continue;

            if (post1.isEmpty())
                handleSingle(post2, ul2);

            if (post2.isEmpty())
                handleSingle(post1, ul1);

            if (!post1.isEmpty() && !post2.isEmpty())
                handleBoth(post1,post2);
        }

        addInitialStates();
        addLabeling();

        return res;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods: ">

    // post variant which returns the Transition's leading to the post states.
    private <S> Set<Transition<S,A>> post(TransitionSystem<S,A,?> ts, S state){
        return ts.getTransitions()
                .parallelStream()
                .filter(t -> t.getFrom().equals(state))
                .collect(Collectors.toSet());
    }

    // post transition computation, this method will set all initial states.
    private void addInitialStates(){
        Util.getPairs(ts1.getInitialStates(),ts2.getInitialStates())
                .forEach(s -> {
                    if(res.getStates().contains(s))
                        res.setInitial(s,true);
                });
    }

    // post transition computation, this method will add labeling function along with atomic propositions.
    private void addLabeling(){
        res.getStates().forEach(s -> {
            ts1.getLabel(s.first).forEach(l1 -> {
                res.addAtomicProposition(l1);
                res.addToLabel(s,l1);
            });
            ts2.getLabel(s.second).forEach(l2 -> {
                res.addAtomicProposition(l2);
                res.addToLabel(s,l2);
            });
        });
    }

    private <S> void handleSingle(Set<Transition<S,A>> post, UnaryLogic<S> logic){
        for (Transition<S, A> transition : post) {
            logic.setTransition(transition);
            logic.handle();
        }
    }

    private void handleBoth(Set<Transition<S1,A>> post1, Set<Transition<S2,A>> post2){
        for (Transition<S1, A> t1 : post1) {
            ul1.setTransition(t1);
            for (Transition<S2, A> t2 : post2) {
                ul2.setTransition(t2);
                bl.apply();
            }
        }
    }

    private void add(Pair<S1,S2> to, A action){
        if (!res.getStates().contains(to)){
            toReach.add(to);
            res.addState(to);
        }
        res.addAction(action);
        res.addTransition(new Transition<>(currState,action,to));
    }

    //</editor-fold>

    //<editor-fold desc="Inner Classes: ">

    private class UnaryLogic<S> {
        private Transition<S,A> transition;
        private final Function<Transition<S,A>,Pair<S1,S2>> to;

        private UnaryLogic(Function<Transition<S,A>,Pair<S1,S2>> to){
            this.to = to;
        }

        private void setTransition(Transition<S,A> transition){
            this.transition = transition;
        }

        private A getAction(){
            return transition.getAction();
        }

        private S getTo(){
            return transition.getTo();
        }

        private void handle(){
            if (!handshake.contains(transition.getAction()))
                add(to.apply(transition),transition.getAction());
        }
    }

    private class BinaryLogic {
        private UnaryLogic<S1> l1;
        private UnaryLogic<S2> l2;


        private BinaryLogic(UnaryLogic<S1> l1, UnaryLogic<S2> l2){
            this.l1 = l1;
            this.l2 = l2;
        }

        private void apply(){
            if (l1.getAction().equals(l2.getAction()) &&
                    handshake.contains(l1.getAction()))
                add(Pair.pair(l1.getTo(), l2.getTo()), l1.getAction());
            else
            {
                l1.handle();
                l2.handle();
            }
        }
    }

    //</editor-fold>









}
