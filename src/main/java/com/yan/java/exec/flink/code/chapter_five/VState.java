package com.yan.java.exec.flink.code.chapter_five;

import com.yan.java.exec.flink.code.Student;

public enum  VState {
  Terminal,

  Invalid,

  Initial;

  public VState transition(Student student){
    /**
     * 遇到一个student state进行转化
     */
    if(student.age>100 && student.age<1)
      return VState.Invalid;
    return VState.Terminal;
  }

  public boolean isTerminal(){
    return this == Terminal;
  }


}
