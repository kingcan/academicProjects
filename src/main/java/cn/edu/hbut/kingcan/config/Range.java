package cn.edu.hbut.kingcan.config;

public class Range {
    private double min=0;
    private double max=255;
    public Range(){
    }
    public Range(double min,double max){
        this.min=min;
        this.max=max;
    }
    public double getMin(){
        return min;
    }
    public double getMax(){
        return max;
    }
    public void setMin(double min){
        this.min=min;
    }
    public void setMax(double max){
        this.max=max;
    }
}

