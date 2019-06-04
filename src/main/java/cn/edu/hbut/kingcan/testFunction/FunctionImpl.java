package cn.edu.hbut.kingcan.testFunction;

import cec2015.CEC15Problems;
import cn.edu.hbut.kingcan.config.frame.Function;
public class FunctionImpl implements Function {
    CEC15Problems tf = new CEC15Problems();
    private int dim;
    private String fun_name;
    public FunctionImpl(int dim,String fun_name){
        tf.setNumberOfRun(1);
        this.fun_name=fun_name;
        this.dim=dim;
    }

    @Override
    public double function(double[] pos) {
        double result = 0;
        int func_num = 1;
        switch(fun_name){
            case "f1": func_num =1;
                break;
            case "f2": func_num =2;
                break;
            case "f3": func_num =3;
                break;
            case "f4": func_num =4;
                break;
            case "f5": func_num =5;
                break;
            case "f6": func_num =6;
                break;
            case "f7": func_num =7;
                break;
            case "f8": func_num =8;
                break;
            case "f9": func_num =9;
                break;
            case "f10": func_num =10;
                break;
            case "f11": func_num =11;
                break;
            case "f12": func_num =12;
                break;
            case "f13": func_num =13;
                break;
            case "f14": func_num =14;
                break;
            case "f15": func_num =15;
                break;
        }
        result=tf.eval(pos, dim, 1, func_num)[0];
        return -result;
    }

}
