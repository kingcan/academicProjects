package cn.edu.hbut.kingcan.optimization.algorithm.cs;


import cn.edu.hbut.kingcan.config.Range;
import cn.edu.hbut.kingcan.config.frame.Algorithm;
import cn.edu.hbut.kingcan.config.frame.Unit;
import org.apache.commons.math3.special.Gamma;

public class CsWhole extends Algorithm {
    protected int dim;// 维度
    protected int size;// 种群数量
    protected int iter;// 迭代数
    private Range[] ranges;
    private CsUnit[] group;// 种群
    private double[] positionBest;// 当前最优位置
    private double valueBest = -Float.MAX_VALUE;// 当前最优值
    private int idBest;// 最优鸟巢的id
    private double xMax = 255;// 最大范围
    private double xMin = 0;// 最小范围
    private double stepMax = 10.0f;// 最大步长
    private double Pa = 0.3f;// 窝主发现外来蛋的概率
    private double[] wholeValue;

    public CsWhole(int dim, int size, int iter, double min, double max) {
        this.dim = dim;
        this.size = size;
        this.iter = iter;
        this.xMax = max;
        this.xMin = min;
        this.ranges = new Range[dim];
        for (int i = 0; i < dim; i++) {
            this.ranges[i] = new Range(xMin, xMax);
        }
        group = new CsUnit[size];
        positionBest = new double[dim];
        wholeValue = new double[iter];

    }

    public CsWhole(int dim, int size, int iter, Range[] ranges) {
        this.dim = dim;
        this.size = size;
        this.iter = iter;
        this.ranges = new Range[dim];
        for (int i = 0; i < dim; i++) {
            if (i < ranges.length) {
                this.ranges[i] = new Range(ranges[i].getMin(), ranges[i].getMax());
            }
        }
        group = new CsUnit[size];
        positionBest = new double[dim];
        wholeValue = new double[iter];

    }

    // gamma函数，来自《c常用算法程序》徐士良。
    private double gamma(double x) {
        double y;
        double t;
        double s;
        double u;
        double[] a = { 0.0000677106, -0.0003442342, 0.0015397681, -0.0024467480, 0.0109736958, -0.0002109075,
                0.0742379071, 0.0815782188, 0.4118402518, 0.4227843370, 1.0 };
        if (x <= 0.0) {
            return -1.0;
        }
        y = x;
        if (y <= 1.0) {
            t = 1.0 / (y * (y + 1));
            y += 2.0;
        } else if (y <= 2.0) {
            t = 1.0 / y;
            y += 1.0;
        } else if (y <= 3.0) {
            t = 1.0;
        } else {
            t = 1.0;
            while (y > 3.0) {
                y -= 1.0;
                t *= y;
            }
        }
        s = a[0];
        u = y - 2.0;
        for (int i = 1; i < 10; i++) {
            s = s * u + a[i];
            s = s * t;
        }
        return s;
    }


    //1
    @Override
    public void init() {
        // TODO Auto-generated method stub
        for (int i = 0; i < size; i++) {
            CsUnit pu = new CsUnit(dim);
            for (int j = 0; j < dim; j++) {
                pu.setPosition(j, randNumDouble(ranges[j].getMin(), ranges[j].getMax()));
            }
            group[i] = pu;
            group[i].setValue(fitFunction(group[i].getPosition()));
        }
    }

    public void init(Unit[] units) {
        // TODO Auto-generated method stub
        for (int s = 0; s < size; s++){
            Unit cs = new CsUnit(dim);
            for (int d = 0; d < dim; d++){
                cs.setPosition(d, units[s].getPosition(d));
            }
            double value=units[s].getValue();
            cs.setValue(value);
            group[s] =(CsUnit) cs ;
        }
    }

    public void init(double[]position) {
        // TODO Auto-generated method stub
        for (int i = 0; i < size; i++) {
            CsUnit cs = new CsUnit(dim);

            if(i==0){
                for (int d = 0; d < dim; d++){
                    cs.setPosition(d, position[d]);
                }
            }else{
                for (int j = 0; j < dim; j++) {
                    cs.setPosition(j,
                            randNumDouble(ranges[j].getMin(), ranges[j].getMax()));
                }
            }
            // Arrays.sort(du.getPosition());
            cs.setValue(fitFunction(cs.getPosition()));
            group[i] = cs;
        }
    }


    //3
    @Override
    protected void update() {
        // TODO Auto-generated method stub
        updateBest();
        updatePosition();
        changeNest();
    }


    //2
    @Override
    public void iteration() {
        // TODO Auto-generated method stub
        for (int j = 0; j < iter; j++) {
            update();
            wholeValue[j] = valueBest;
        }
    }

    @Override
    public double[] getBestPosition() {
        // TODO Auto-generated method stub
        return positionBest;
    }


    //6
    // levy概率飞行长度
    private double levy() {
        double s;
        double u;
        double v;
        double t;
        double beta = 1.5f;
        // v=gamma(1+beta)*Math.sin(Math.PI*beta/2)/
        // gamma((1+beta)/2)*beta*Math.pow(2, (beta-1)/2);
        v = Gamma.gamma(1.0f + beta) * Math.sin(Math.PI * beta / 2.0f) / Gamma.gamma((1 + beta) / 2.0f) * beta
                * Math.pow(2.0f, (beta - 1) / 2.0f);
        t = Math.pow(v, 1.0f / beta);
        t = t * randGauss(0, 1);
        do {
            u = randGauss(0, 1);
        } while (u == 0.0f);

        s = u / Math.pow(Math.abs(t), 1.0f / beta);
        return s;
    }


    //4
    // 更新最佳鸟蛋的位置
    private void updateBest() {
        double temp = 0;
        for (int i = 0; i < size; i++) {
            temp = group[i].getValue();
            if (temp > valueBest) {
                valueBest = temp;
                for (int j = 0; j < dim; j++) {
                    positionBest[j] = group[i].getPosition(j);
                }
                idBest = i;
            }
        }
    }


    //5
    // 更新位置
    private void updatePosition() {
        double[] positionNew = new double[dim];
        for (int j = 0; j < size; j++) {
            //不更新最优个体
            if (j != idBest) {
                for (int i = 0; i < dim; i++) {
                    positionNew[i] = group[j].getPosition(i)
                            + randNumDouble(0, 1) * levy() * (group[j].getPosition(i) - positionBest[i]);
                    if (positionNew[i] > ranges[i].getMax()) {
                        positionNew[i] = ranges[i].getMax();
                    }
                    if (positionNew[i] < ranges[i].getMin()) {
                        positionNew[i] = ranges[i].getMin();
                    }
                }
                double temp = fitFunction(positionNew);
                if (temp > group[j].getValue()) {
                    group[j].setPosition(positionNew);
                    group[j].setValue(temp);
                }
            }
        }
    }


    //7
    // 窝主是否发现鸟蛋并改变位置
    private void changeNest() {
        double r;
        double[] positionNew = new double[dim];
        for (int i = 0; i < size; i++) {
            //最优鸟巢不会被寄主发现
            if (i != idBest) {
                r = randNumDouble(0.0d, 1.0d);
                if (r < Pa) {
                    for (int j = 0; j < dim; j++) {
                        positionNew[j] = group[i].getPosition(j)
                                + randNumDouble(ranges[j].getMin(), ranges[j].getMax()) * r;
                        if (positionNew[j] > ranges[j].getMax()) {
                            positionNew[j] = ranges[j].getMax();
                        }
                        if (positionNew[j] < ranges[j].getMin()) {
                            positionNew[j] = ranges[j].getMin();
                        }
                    }
                    double temp = fitFunction(positionNew);
                    if (temp > group[i].getValue()) {
                        group[i].setPosition(positionNew);
                        group[i].setValue(temp);
                    }
                }
            }
        }
    }

//	// 适应度函数
//	protected double fitFunction(double[] pos) {
//		return 0;
//	}

    // 运行
    public void run() {
        init();
        iteration();
    }

    public double getBestValue() {
        return valueBest;
    }

    public double[] getWholeValue() {
        return wholeValue;
    }

    public void realise() {
        // mBuff=null;
        group = null;
        positionBest = null;
        wholeValue = null;
    }

    public void setMax(float xMax) {
        this.xMax = xMax;
    }

    public void setMin(float xMin) {
        this.xMin = xMin;
    }

    public void setPosition(int i, double[] pos) {
        group[i].setPosition(pos);
    }


    public double[] getPosition(int i) {
        return group[i].getPosition();
    }
    public void setGroup(int i,CsUnit unit){
        group[i].setPosition(unit.getPosition());
        group[i].setValue(unit.getValue());
    }
    public void setGroup(int i,double[] position,double value){
        group[i].setPosition(position);
        group[i].setValue(value);
    }
}

