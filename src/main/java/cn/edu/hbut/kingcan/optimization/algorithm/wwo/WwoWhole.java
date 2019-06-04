package cn.edu.hbut.kingcan.optimization.algorithm.wwo;

import cn.edu.hbut.kingcan.config.Range;
import cn.edu.hbut.kingcan.config.frame.Algorithm;
import cn.edu.hbut.kingcan.config.frame.Unit;

public class WwoWhole extends Algorithm {
    protected int dim;// 维度
    protected int size;// 种群数量
    protected int iter;// 迭代数
    protected Range[] ranges;
    protected WwoUnit[] group;// 种群
    protected double[] positionBest;// 当前最优位置
    protected int idBest;//最優個體id
    protected double valueBest = -Double.MAX_VALUE;// 当前最优值
    protected double valueWorst = Double.MAX_VALUE;//當前最差值
    protected double xMax = 100;// 最大范围
    protected double xMin = 0;// 最小范围
    protected double h = 12;// 波高
    protected double lambda = 0.5d;// 波长
    protected double alpha = 1.0026d;//波長衰減係數
    protected double beta=0.25d;//碎浪係數
    protected double[] wholeValue;// 每一代的最优值记录
    protected int[] temp;
    protected int kmax=12;



    public WwoWhole(int dim, int size, int iter, double min, double max) {
        this.dim = dim;
        this.size = size;
        this.iter = iter;
        this.xMax = max;
        this.xMin = min;
        this.ranges = new Range[dim];
        for (int i = 0; i < dim; i++) {
            this.ranges[i] = new Range(xMin, xMax);
        }
        this.group = new WwoUnit[size];
        this.positionBest = new double[dim];
        this.wholeValue = new double[iter];
        this.h = 12;
        this.lambda = 0.5d;
    }

    public WwoWhole(int dim, int size, int iter, Range[] ranges) {
        this.dim = dim;
        this.size = size;
        this.iter = iter;
        this.ranges = new Range[dim];
        for (int i = 0; i < dim; i++) {
            if (i < ranges.length) {
                this.ranges[i] = new Range(ranges[i].getMin(),
                        ranges[i].getMax());
            }
        }
        this.group = new WwoUnit[size];
        this.positionBest = new double[dim];
        this.wholeValue = new double[iter];
        this.h = 12;
        this.lambda = 0.5d;
    }

    // 初始化位置，速度。
    @Override
    public void init() {
        // TODO Auto-generated method stub
        for (int s = 0; s < size; s++) {
            WwoUnit pu = new WwoUnit(dim);
            for (int d = 0; d < dim; d++) {
                pu.setPosition(d,
                        randNumDouble(ranges[d].getMin(), ranges[d].getMax()));
            }
            double value = fitFunction(pu.getPosition());
            pu.setH(h);
            pu.setLambda(lambda);
            pu.setValue(value);
            group[s] = pu;
        }
    }

    public void init(Unit[] units) {
        // TODO Auto-generated method stub
        for (int s = 0; s < size; s++) {
            WwoUnit wu = new WwoUnit(dim);
            for (int d = 0; d < dim; d++) {
                wu.setPosition(d, units[s].getPosition(d));
            }
            double value = units[s].getValue();
            wu.setH(h);
            wu.setLambda(lambda);
            wu.setValue(value);
            group[s] = wu;

        }
    }

    public void init(double[] position) {
        for (int s = 0; s < size; s++) {
            WwoUnit wu = new WwoUnit(dim);
            if (s == 0) {
                for (int d = 0; d < dim; d++) {
                    wu.setPosition(d, position[d]);
                }
            } else {
                for (int d = 0; d < dim; d++) {
                    wu.setPosition(
                            d,
                            randNumDouble(ranges[d].getMin(),
                                    ranges[d].getMax()));
                }
            }

            wu.setH(h);
            wu.setLambda(lambda);
            double value = fitFunction(wu.getPosition());
            wu.setValue(value);
            group[s] = wu;

        }
    }

    // 传播
    private void passWave(int id) {
        WwoUnit wwo = group[id];
        double[] positionNew = new double[dim];
        for (int d = 0; d < dim; d++) {
            positionNew[d] = wwo.getPosition(d) + randNumDouble(-1.0d, 1.0d)*(ranges[d].getMax()-ranges[d].getMin())
                    * wwo.getLambda();
            if (positionNew[d] > ranges[d].getMax()) {
                positionNew[d] = ranges[d].getMax();
            }
            if (positionNew[d] < ranges[d].getMin()) {
                positionNew[d] = ranges[d].getMin();
            }
        }
        double result = fitFunction(positionNew);
        if (result > group[id].getValue()) {
            group[id].setPosition(positionNew);
            group[id].setValue(result);
            if(result>valueBest){
                valueBest=result;
                for(int d=0;d<dim;d++){
                    positionBest[d]=positionNew[d];

                }
                idBest=id;
            }
            group[id].setH(h);
        }else{
            group[id].setH(group[id].getH() - 1.0d);
        }


    }

    // 折射
    private void refraction(int id) {
        WwoUnit wwo = group[id];
        double[] positionNew = new double[dim];
        for (int d = 0; d < dim; d++) {

            positionNew[d] = randGauss(
                    (positionBest[d] + wwo.getPosition(d)) / 2,
                    (positionBest[d] - wwo.getPosition(d)) / 2);

//			positionNew[d] = randNumDouble(wwo.getPosition(d),
//					(positionBest[d] + wwo.getPosition(d)) / 2);
            if (positionNew[d] > ranges[d].getMax()) {
                positionNew[d] = ranges[d].getMax();
            }
            if (positionNew[d] < ranges[d].getMin()) {
                positionNew[d] = ranges[d].getMin();
            }
        }
        double result = fitFunction(positionNew);
        if (result != 0.0d) {
            group[id].setLambda(group[id].getValue() / result*group[id].getLambda());
        }
        if (result > group[id].getValue()) {
            group[id].setPosition(positionNew);
            group[id].setValue(result);
            if(result>valueBest){
                valueBest=result;
                for(int d=0;d<dim;d++){
                    positionBest[d]=positionNew[d];

                }
                idBest=id;
            }
        }
        group[id].setH(h);

    }

    // 碎浪
    private void breakingWave() {
        double[] positionNew = new double[dim];
        ///    kmax=Math.min(12, dim/2);
        temp=randomCommon(0,dim,kmax);
        for(int j=0;j<kmax;j++){
            int d=temp[j];
            positionNew[d] = positionBest[d]+randGauss(0.0d, 1.0d)*beta* (ranges[d].getMax()-ranges[d].getMin());
//			positionNew[d] = positionBest[d]+randNumDouble(ranges[d].getMin(),ranges[d].getMax())*beta;
            if (positionNew[d] > ranges[d].getMax()) {
                positionNew[d] = ranges[d].getMax();
            }
            if (positionNew[d] < ranges[d].getMin()) {
                positionNew[d] = ranges[d].getMin();
            }
        }
        double result = fitFunction(positionNew);
        if (result > valueBest) {
            group[idBest].setPosition(positionNew);
            group[idBest].setValue(result);
            valueBest=result;
            for(int d=0;d<dim;d++){
                positionBest[d]=positionNew[d];
            }
        }
    }

    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }





    private void getFMaxFMin(){
        valueWorst=group[0].getValue();
        for(int s=0;s<size;s++){
            if(group[s].getValue()>valueBest){
                valueBest=group[s].getValue();
                idBest=s;
            }
            if(group[s].getValue()<valueWorst){
                valueWorst=group[s].getValue();
            }
        }
    }

    private void updateLambda(){
        for(int s=0;s<size;s++){
            double b=-(group[s].getValue()-valueWorst+Double.MIN_VALUE)/(valueBest-valueWorst+Double.MIN_VALUE);
            double newLambda = group[s].getLambda()*Math.pow(alpha, b);
            group[s].setLambda(newLambda);
        }
    }

    @Override
    protected void update() {
        // TODO Auto-generated method stub
        getFMaxFMin();


        for (int s = 0; s < size; s++) {
            passWave(s);
            if(group[s].getH()<=0.0d){
                refraction(s);

            }
        }

        updateLambda();
    }

    @Override
    public void iteration() {
        // TODO Auto-generated method stub
        for (int i = 0; i < iter; i++) {

            update();
            breakingWave();
            beta = 0.25-(double)(i+1)/iter*(0.25-0.001);

            //	size = (50-3)*(i+1)/iter+3;


            wholeValue[i] = valueBest;

        }
    }

    // 运行
    public void run() {
        init();
        iteration();
    }

    public double[] getBestPosition() {
        return positionBest;
    }

    public double getBestValue() {
        return valueBest;
    }

    public void setBestPosition(double[] position) {
        for (int i = 0; i < dim; i++) {
            positionBest[i] = position[i];
        }
    }

    public void setBestValue(double value) {
        valueBest = value;
    }

    public double[] getWholeValue() {
        return wholeValue;
    }

    public double getWholeValue(int id) {
        return wholeValue[id];
    }

    public void setWholeValue(int id, double value) {
        wholeValue[id] = value;
    }

    public void realise() {
        // mBuff=null;
        group = null;
        positionBest = null;
        wholeValue = null;
    }

    public void setMax(double xMax) {
        this.xMax = xMax;
    }

    public void setMin(double xMin) {
        this.xMin = xMin;
    }

    public Unit[] getGroup() {
        return (Unit[]) group;
    }

    public WwoUnit getGroup(int id) {
        return group[id];
    }

    public double[] getPosition(int i) {
        return group[i].getPosition();
    }

}
