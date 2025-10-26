package com.maxgcoding.fa;

import java.util.ArrayList;

import lombok.Data;


@Data
public class NFAState {
    private int label;
    private ArrayList<Transition> trans;
    public NFAState(int label) {
        this.label = label;
        this.trans = new ArrayList<>();
    }
    public void addTransition(Transition t) {
        trans.add(t);
    }
}
