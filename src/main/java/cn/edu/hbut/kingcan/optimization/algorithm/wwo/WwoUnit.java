package cn.edu.hbut.kingcan.optimization.algorithm.wwo;

import cn.edu.hbut.kingcan.config.frame.Unit;

public class WwoUnit extends Unit {

    //波高
    private double h=12;

    private double lambda=0.5d;

    public WwoUnit(int dim){
        super(dim);
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }


}
