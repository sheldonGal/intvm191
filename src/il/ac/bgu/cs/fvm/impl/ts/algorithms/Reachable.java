package il.ac.bgu.cs.fvm.impl.ts.algorithms;


import il.ac.bgu.cs.fvm.impl.ts.queries.Iter;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

import java.util.*;

public class Reachable<S> {
    private final TransitionSystem<S,?,?> ts;
    private final Set<S> reached;
    private final Queue<S> toReach;

    public Reachable(TransitionSystem<S,?,?> ts){
        this.ts = ts;
        reached = new HashSet<>();
        toReach = new ArrayDeque<>(ts.getInitialStates());
    }

    public Set<S> reachable() {

        while(!toReach.isEmpty())
        {
            var currState = toReach.poll();
            reached.add(currState);

            var post = Iter.getInstance().post(ts,currState,false);
            post.removeAll(reached);

            toReach.addAll(post);
        }

        return reached;
    }

}
