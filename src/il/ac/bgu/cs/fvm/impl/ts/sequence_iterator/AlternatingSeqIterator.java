package il.ac.bgu.cs.fvm.impl.ts.sequence_iterator;

import il.ac.bgu.cs.fvm.exceptions.FVMException;
import il.ac.bgu.cs.fvm.impl.ts.util.TS_Validator;
import il.ac.bgu.cs.fvm.transitionsystem.AlternatingSequence;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;


import java.util.Iterator;

public class AlternatingSeqIterator<S,A> implements Iterator<AlternatingSeqIterator._Transition> {

    private AlternatingSequence<S,A> seq;
    private final _Transition transition;

    public AlternatingSeqIterator(AlternatingSequence<S,A> seq){
        this.seq = seq;
        transition = new _Transition();
    }


    @Override
    public boolean hasNext() {
        return seq.size() > 2;
    }

    @Override
    public _Transition next() {
        transition.from = seq.head();
        var seq2 = seq.tail();
        transition.action = seq2.head();
        seq = seq2.tail();
        transition.to = seq.head();
        return transition;
    }

    public boolean eq(Transition<S,A> other){
        return transition.from.equals(other.getFrom()) &&
                transition.to.equals(other.getTo()) &&
                transition.action.equals(other.getAction());
    }

    public class _Transition{
        private S from;
        private A action;
        private S to;

        public void validate(TransitionSystem<S,A,?> ts) throws FVMException
        {
            TS_Validator.getInstance().states(ts,true,from,to);
            TS_Validator.getInstance().actions(ts,true,action);
        }

    }
}
