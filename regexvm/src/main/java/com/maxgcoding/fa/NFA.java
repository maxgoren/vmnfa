package com.maxgcoding.fa;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NFA {
    private NFAState start;
    private NFAState accept;
}
