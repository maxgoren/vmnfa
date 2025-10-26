package com.maxgcoding.vm;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Instruction {
    private InstType inst;
    private String operand;
    private Integer nextInst;
    private Integer altInst;
}
