package cn.edu.hbut.kingcan.optimization.algorithm.ga;

import cn.edu.hbut.kingcan.config.Range;
import cn.edu.hbut.kingcan.config.frame.Algorithm;
import cn.edu.hbut.kingcan.config.frame.Unit;

public class GaWhole extends Algorithm {
    protected           int           dim;//维度
    protected           int           size;//种群数量
    protected           int           iter;//迭代数
    private             Range[]       ranges;
    private             GaUnit[]      group;//种群
    private             GaUnit[]      newGroup;
    private             double[]       positionBest;//当前最优位置
    private             int           indexBest;
    private             double         valueBest=-Double.MAX_VALUE;//当前最优值
    private             double         xMax=100;//最大范围
    private             double         xMin=0;//最小范围
    private             double         crossRate ;//交叉率
    private             double        alterRate ;//变异率
    private             double[]       wholeValue;//历代最佳值

    public GaWhole(int dim,int size,int iter,double min,double max){
        this.dim=dim;
        this.size=size;
        this.iter=iter;
        this.xMax=max;
        this.xMin=min;
        this.ranges=new Range[dim];
        for(int i=0;i<dim;i++){
            this.ranges[i]=new Range(xMin,xMax);
        }
        this.group=new GaUnit[size];
        this.newGroup=new GaUnit[size];
        this.positionBest=new double[dim];
        this.wholeValue=new double[iter];
        this.crossRate=0.8d;
        this.alterRate=0.05d;
    }
    public GaWhole(int dim,int size,int iter,Range[]ranges){
        this.dim=dim;
        this.size=size;
        this.iter=iter;
        this.ranges=new Range[dim];
        for(int i=0;i<dim;i++){
            if(i<ranges.length){
                this.ranges[i]=new Range(ranges[i].getMin(),ranges[i].getMax());
            }
        }
        this.group=new GaUnit[size];
        this.newGroup=new GaUnit[size];
        this.positionBest=new double[dim];
        this.wholeValue=new double[iter];
        this.crossRate=0.8d;
        this.alterRate=0.05d;
    }
    @Override
    public void init() {
        // TODO Auto-generated method stub
        double value=0;
        indexBest=0;
        double[] position=new double[dim];
        for (int i = 0; i < size; i++){
            GaUnit gu = new GaUnit(dim);
            GaUnit gu1 = new GaUnit(dim);
            newGroup[i]=gu1;
            for (int j = 0; j < dim; j++){
                value=randNumDouble(ranges[j].getMin(), ranges[j].getMax());
                gu.setPosition(j, value);
                position[j]=value;
            }
            gu.setValue(fitFunction(position));
            group[i] =gu;
        }
    }

    public void init(Unit[] units) {
        // TODO Auto-generated method stub
        for (int s = 0; s < size; s++){
            Unit ga = new GaUnit(dim);
            for (int d = 0; d < dim; d++){
                ga.setPosition(d, units[s].getPosition(d));
            }
            double value=units[s].getValue();
            ga.setValue(value);
            group[s] =(GaUnit) ga ;
        }
    }

    public void init(double[]position) {
        // TODO Auto-generated method stub
        for (int i = 0; i < size; i++) {
            GaUnit ga = new GaUnit(dim);

            if(i==0){
                for (int d = 0; d < dim; d++){
                    ga.setPosition(d, position[d]);
                }
            }else{
                for (int j = 0; j < dim; j++) {
                    ga.setPosition(j,
                            randNumDouble(ranges[j].getMin(), ranges[j].getMax()));
                }
            }
            // Arrays.sort(du.getPosition());
            ga.setValue(fitFunction(ga.getPosition()));
            group[i] = ga;
        }
    }


    @Override
    protected void update() {
        // TODO Auto-generated method stub
        indexBest = 0;
        double[] rouletteRate = null;
        rouletteRate=rouletteRate();
        double currentBest=-Float.MAX_VALUE;//当前最优值
        for (int k = 0; k < size; k++){
            double temp = group[k].getValue();
            if(temp>currentBest){
                indexBest=k;
                currentBest = temp;
            }
        }
        if (group[indexBest].getValue() >valueBest){
            valueBest=group[indexBest].getValue();
            for(int d=0;d<dim;d++){
                positionBest[d]=group[indexBest].getPosition(d);
            }
        }
        roulette(indexBest,rouletteRate);

        altered();

        cross();

        calFitness();
    }

    @Override
    public void iteration() {
        // TODO Auto-generated method stub
        for(int i=0;i<iter;i++){
            update();
            wholeValue[i]=valueBest;
        }
    }

    /**
     * 计算适应度值，个体值-当前中个体的最差值
     * 计算轮盘赌概率
     * @return
     */

    private double[] rouletteRate(){
        double[]rouletteValue=new double[size];
        //求和
        double rouletteValueMin=Float.MAX_VALUE;
        for(int i=0;i<size;i++){
            rouletteValue[i]=group[i].getValue();
            if(rouletteValue[i]<rouletteValueMin){
                rouletteValueMin=rouletteValue[i];
            }
        }
        for(int i=0;i<size;i++){
            rouletteValue[i]=rouletteValue[i]-rouletteValueMin+Math.abs(rouletteValueMin);
        }
        return rouletteValue;
    }
    //轮盘赌选择
    private void roulette(int index,double[]rouletteRate){
        int id=0;
        double rand=0;
        double rouletteSum=0;
        double rouletteTemp=0;
        for(int i=0;i<size;i++){
            rouletteSum+=rouletteRate[i];
        }
        for(int i=0;i<size;i++){
            //保留最佳值
            if(i==index){
                newGroup[i].setPosition(group[i].getPosition());
            }else{
                //获取轮盘赌值
                rand=randNumDouble(0.0d,rouletteSum);
                rouletteTemp=rouletteRate[0];
                for(int j=1;j<size;j++){
                    if(j==size-1){
                        id=size-1;
                        break;
                    }
                    else if(rand<rouletteTemp){
                        id=j-1;

                        break;
                    }
                    rouletteTemp+=rouletteRate[j];
                }
                newGroup[i].setPosition(group[id].getPosition());
            }
        }
        for(int i=0;i<size;i++){
            group[i].setPosition(newGroup[i].getPosition());
        }
    }
    //交叉
    private void cross(){
        double gene1=0;
        double gene2=0;
        int randD=0;
        for (int i = 0; i < size; i+=2){
            randD=randNumInt(0,dim-1);
            double rnd = randNumDouble(0.0d, 1.0d);
            if (rnd < crossRate){
                gene1=group[i].getPosition(randD);
                gene2=group[i+1].getPosition(randD);
                group[i].setPosition(randD, gene2);
                group[i+1].setPosition(randD, gene1);
            }
        }
    }
    //变异
    private double alteredRand(double min,double max){
        double value = 0;
        double estimate=randNumDouble(0.0d,1.0d);

        double absMin=Math.abs(min);
        double absMax=Math.abs(max);
        double max10=Math.log10(Math.max(absMin,absMax));
        double min10=Math.log10(Math.min(absMin,absMax));
        if(estimate<0.5d){
            value=1.0d/Math.pow(10, randNumDouble(min10,max10));
            if(randNumDouble(0.0d,1.0d)<0.5d){
                value=-value;
            }
        }else{
            value=randNumDouble(min, max);
        }


        return value;
    }
    private void altered(){
        int randD=0;
        for (int i = 0; i < size; i++){
            randD=randNumInt(0,dim-1);
            double rnd = randNumDouble(0.0d, 1.0d);
            if (rnd < alterRate &&i!=indexBest){
                double value=randNumDouble(ranges[randD].getMin(), ranges[randD].getMax());
                group[i].setPosition(randD,value);
            }
        }
    }
    //计算适应值
    private void calFitness(){
        for(int i=0;i<size;i++){
            group[i].setValue(fitFunction(group[i].getPosition()));
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        init();
        iteration();
    }

    @Override
    public double getBestValue() {
        // TODO Auto-generated method stub
        return valueBest;
    }

    @Override
    public double[] getBestPosition() {
        // TODO Auto-generated method stub
        return  positionBest;
    }

    @Override
    public double[] getWholeValue() {
        // TODO Auto-generated method stub
        return wholeValue;
    }

    @Override
    public void realise() {
        // TODO Auto-generated method stub
        group=null;
        positionBest=null;
        wholeValue=null;
    }
    public void setGroup(int i,GaUnit unit){
        group[i].setPosition(unit.getPosition());
        group[i].setValue(unit.getValue());
    }
    public void setGroup(int i,double[] position,double value){
        group[i].setPosition(position);
        group[i].setValue(value);
    }
}
