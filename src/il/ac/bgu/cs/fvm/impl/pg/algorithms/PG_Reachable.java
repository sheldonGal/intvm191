package il.ac.bgu.cs.fvm.impl.pg.algorithms;

import il.ac.bgu.cs.fvm.programgraph.PGTransition;
import il.ac.bgu.cs.fvm.programgraph.ProgramGraph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class PG_Reachable<L> {
    public Set<L> post(ProgramGraph<L, ?> pg, L s) {
        HashSet<L> ans = new HashSet<>();

        for (PGTransition<L, ?> t: pg.getTransitions()) {
            if (t.getFrom().equals(s))
                ans.add(t.getTo());
        }

        return ans;
    }

    public Set<L> reach(ProgramGraph<L,?> pg) {
        HashSet<L> answer = new HashSet<>();
        Queue<L> currents = new LinkedList<>();
        currents.addAll(pg.getInitialLocations());

        while (!currents.isEmpty()) {
            L current = currents.poll();
            answer.add(current);
            for (L location : post(pg,current)) {
                if (!answer.contains(location))
                    currents.add(location);
            }
        }

        return answer;
    }
}
