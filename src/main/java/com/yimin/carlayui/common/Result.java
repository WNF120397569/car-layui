package com.yimin.carlayui.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**是一个在Java开发过程中用注解的方式，简化了 JavaBean 的编写，避免了冗余和样板式代码而出现的插件，让编写的类更加简洁 如：
 private int rid;
 private String rname;
 private String level;

 public int getRid() {
 return rid;
 }
 public void setRid(int rid) {
 this.rid = rid;
 }

可简化为
 import lombok.Data;
 @Data
 public class Role {
 private int rid;
 }
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result success(String msg){
        return new Result(1,msg);
    }

    public static Result success(String msg,Object data){
        return new Result(1,msg,data);
    }

    public static Result error(String msg){
        return new Result(0,msg);
    }

}
