package com.maxgcoding.pm.vm;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Instruction {
    private InstType inst;
    private Character operand;
    private String cclOperand;
    private Integer next;
    private Integer alternate;
}
