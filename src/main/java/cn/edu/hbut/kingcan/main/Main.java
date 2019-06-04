package cn.edu.hbut.kingcan.main;

import cn.edu.hbut.kingcan.config.Range;
import cn.edu.hbut.kingcan.config.frame.Algorithm;
import cn.edu.hbut.kingcan.optimization.algorithm.cs.CsWhole;
import cn.edu.hbut.kingcan.optimization.algorithm.ga.GaWhole;
import cn.edu.hbut.kingcan.optimization.algorithm.hro.HroWhole;
import cn.edu.hbut.kingcan.optimization.algorithm.pso.PsoWhole;
import cn.edu.hbut.kingcan.optimization.algorithm.wwo.WwoWhole;
import cn.edu.hbut.kingcan.testFunction.FunctionImpl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static String OUTPUTPATH="D:\\FunctionTest";

    private static String[] functionName = {
            "f1",	                    	    //f1
            "f2",                   	        //f2
            "f3",                    	        //f3
            "f4",                    	        //f4
            "f5",                   	        //f5
            "f6",                    	        //f6
            "f7",                   	        //f7
            "f8",                               //f8
            "f9",                               //f9
            "f10",                              //f10
            "f11",                              //f11
            "f12",                              //f12
            "f13",                              //f13
            "f14",                              //f14
            "f15",                              //f15
    };
    private static String[] rangeName = {
            "(-100,100)",//f1
            "(-100,100)", //f2
            "(-100,100)",//f3
            "(-100,100)", //f4
            "(-100,100)", //f5
            "(-100,100)", //f6
            "(-100,100)",//f7
            "(-100,100)",//f8
            "(-100,100)",//f9
            "(-100,100)",//f10
            "(-100,100)",//f11
            "(-100,100)",//f12
            "(-100,100)",//f13
            "(-100,100)",//f14
            "(-100,100)",//f15
    };
    private static Range[] rangeValue = new Range[]{

            Range(-100, 100), //f1
            Range(-100, 100), //f2
            Range(-100, 100), //f3
            Range(-100, 100), //f4
            Range(-100, 100), //f5
            Range(-100, 100), //f6
            Range(-100, 100), //f7
            Range(-100, 100), //f8
            Range(-100, 100), //f9
            Range(-100, 100), //f10
            Range(-100, 100), //f11
            Range(-100, 100), //f12
            Range(-100, 100), //f13
            Range(-100, 100), //f14
            Range(-100, 100), //f15

    };
    private static String[] algorithmName = {
            "PSO",						//0
//		"HRA",						//4
//		"GA",
//		"CS",
//		"WWO",						//水波算法

    };

    private static int[] dimValue = {
//			10,
            30
    };

    private static int[] sizeValue = {
//			20,
//			40,
            60,
//			80,
//			100,
//			120 ,
//			150,
    };

    private static int[] iterValue = {
//			5000,
//			10000,
            20000,
//			100000
    };
    private static int numValue = 50;
    private static int currentNum=0;
    private static StringBuilder proData = new StringBuilder("");
    private static int futureTaskNum=3;//设置线程数

    /**
     * 将date转换成yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String dateToYYMMDDHHmmss(Date date){
        String dateStr="";
        SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        dateStr=df.format(date);
        return dateStr;
    }
    public static void main(String[] arg0) throws InterruptedException, ExecutionException {
        OUTPUTPATH+= File.separator+dateToYYMMDDHHmmss(new Date())+File.separator;

        try {
            for (int a = 0; a < algorithmName.length; a++) {//遍历算法
                proData .append( "函数,算法,维度,种群,最大,最小,均值,标准差,时间\n");
                String algorithmFolderPath=OUTPUTPATH+algorithmName[a]+File.separator;
                File algorithmFolder=new File(algorithmFolderPath);
                if(!algorithmFolder.exists()){
                    algorithmFolder.mkdirs();
                }

                File proFile;
                proFile = new File(
                        algorithmFolderPath
                                + algorithmName[a]+"数据统计.csv");

                if (!proFile.exists()) {
                    proFile.createNewFile();
                }
                FileWriter pfw = new FileWriter(proFile.getAbsoluteFile());
                BufferedWriter pbw = new BufferedWriter(pfw);

                for (int f = 0; f < functionName.length; f++) {//遍历测试函数
                    for (int d = 0; d < dimValue.length; d++) {//遍历测试的纬度
                        for (int s = 0; s < sizeValue.length; s++) {//遍历种群数
                            for (int i = 0; i < iterValue.length; i++) {//遍历迭代次数
                                currentNum=0;
                                final String function=functionName[f];
                                final Range range=rangeValue[f];
                                final String algorithm=algorithmName[a];
                                final int dim=dimValue[d];
                                final int size=sizeValue[s];
                                final int iter=iterValue[i];
                                String fileName = "";
                                fileName += algorithmName[a] + "_";
                                fileName += functionName[f] + "_";
                                fileName += rangeName[f] + "_";
                                fileName += dimValue[d] + "_";
                                fileName += sizeValue[s] + "_";
                                fileName += iterValue[i] + "_";
                                fileName += numValue;
                                fileName += ".csv";
								/*写入单个文件*/
                                File file = new File(
                                        algorithmFolderPath
                                                + fileName);
                                System.out.println("name:"
                                        + file.getAbsolutePath());
                                // if file doesnt exists, then create it
                                if (!file.exists()) {
                                    file.createNewFile();
                                }
                                FileWriter fw = new FileWriter(
                                        file.getAbsoluteFile());
                                BufferedWriter bw = new BufferedWriter(fw);
                                List<FutureTask<StringBuffer>> futureTasks = new ArrayList<FutureTask<StringBuffer>>();
                                //线程池 初始化十个线程 和JDBC连接池是一个意思 实现重用
                                ExecutorService executorService = Executors.newFixedThreadPool(futureTaskNum);

                                Callable<StringBuffer> callable = new Callable<StringBuffer>() {
                                    @Override
                                    public StringBuffer call() throws Exception {

//								        System.out.println("任务执行:获取到结果 :"+data);
                                        return  selectAlgorithm(algorithm,
                                                dim, size,
                                                iter,
                                                range.getMin(),
                                                range.getMax(),
                                                function);
                                    }
                                };
                                for (int n = 0; n < numValue; n++) {
                                    FutureTask<StringBuffer> futureTask = new FutureTask<StringBuffer>(callable);
                                    futureTasks.add(futureTask);
                                    //提交异步任务到线程池，让线程池管理任务 特爽把。
                                    //由于是异步并行任务，所以这里并不会阻塞
                                    executorService.submit(futureTask);
                                }

                                for (FutureTask<StringBuffer> futureTask : futureTasks) {
                                    //futureTask.get() 得到我们想要的结果
                                    //该方法有一个重载get(long timeout, TimeUnit unit) 第一个参数为最大等待时间，第二个为时间的单位
                                    bw.write(futureTask.get().toString());
                                }
                                bw.close();
                                executorService.shutdown();


								 /*整合单个文件所有数据*/
                                double[] resultValue = null;
                                resultValue = calValue(algorithmFolderPath+fileName);
                                System.out.println("resultValue.length:"
                                        + resultValue.length);
                                proData .append( functionName[f] + ",");
                                proData .append( algorithmName[a] + ",");
                                proData .append( dimValue[d] + ",");
                                proData .append( sizeValue[s]);
                                for (int j = 0; j < resultValue.length; j++) {
                                    System.out.println(j + "resultValue[i]"
                                            + resultValue[j]);
                                    proData .append( ",");
                                    proData .append( resultValue[j]);
                                }
                                proData.append( "\n");
                                pbw.write(proData.toString());
                                proData = new StringBuilder("");
                                fileName = "";
                            }
                        }
                    }
                }
                pbw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double[] calValue(String path) {
        BufferedReader reader;
        double[] resultValue = new double[5+iterValue[0]];
        double[] results = new double[numValue];
        double[] times = new double[numValue];
        double[][] wholeValue=new double[iterValue[0]][numValue];
        try {
            reader = new BufferedReader(new FileReader(
                    path));
            // reader.readLine();// 第一行信息，为标题信息，不用,如果需要，注释掉
            String line = null;
            int i = 0;
            while ((line = reader.readLine()) != null && i < numValue) {
                String item[] = line.split(",");// CSV格式文件为逗号分隔符文件，这里根据逗号切分
                String result = item[item.length - 2];
                String time = item[item.length - 1];
                results[i] = Double.valueOf(result);
                times[i] = Double.valueOf(time);
                times[i]=(int)times[i];
                for(int j=0;j<iterValue[0];j++){
                    wholeValue[j][i]=Double.valueOf(item[j]);
                }
                // System.out.println(i+":data"+":"+results[i]);
                i++;
            }
            double[] temp = maxMinMeanDev(results);
            for (int j = 0; j < 4; j++) {
                resultValue[j] = temp[j];
            }
            resultValue[4] = (int)mean(times);
            for(int j=0;j<iterValue[0];j++){
                resultValue[5+j]=mean(wholeValue[j]);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultValue;
    }

    // 计算最大值，最小值，均值，标准差
    private static double[] maxMinMeanDev(double[] data) {

        double[] calValue = new double[4];
        if (data.length == 0)
            return calValue;
        double min = data[0];
        double max = data[0];
        double mean = 0;
        double dev = 0;
        double sum = 0;
        double sumq = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
            if (data[i] > max) {
                max = data[i];
            }
            if (data[i] < min) {
                min = data[i];
            }
        }
        mean = sum / data.length;
        for (int i = 0; i < data.length; i++) {
            sumq += (data[i] - mean) * (data[i] - mean);
        }
        dev = Math.sqrt(sumq / data.length);
        calValue[0] = max;
        calValue[1] = min;
        calValue[2] = mean;
        calValue[3] = dev;
        return calValue;
    }

    // 计算均值
    private static double mean(double[] data) {
        if (data.length == 0) {
            return 0;
        } else {
            double sum = 0;

            for (int i = 0; i < data.length; i++) {
                sum += data[i];
            }
            return sum / data.length;
        }
    }

    private static StringBuffer selectAlgorithm(String name, int dim, int size,
                                                int iter, double min, double max, String function) {
        StringBuffer data=new StringBuffer("");
        Algorithm algorithm = null;
        long startMili = System.currentTimeMillis();// 当前时间对应的毫秒数
        switch (name) {
            case "WWO":
                algorithm = new WwoWhole(dim, size, iter, min, max);
                break;
            case "PSO":
                algorithm = new PsoWhole(dim, size, iter, min, max);
                break;
            case "HRO":
                algorithm = new HroWhole(dim, size, iter, min, max);
                break;
            case "CS":
                algorithm = new CsWhole(dim, size, iter, min, max);
                break;
            case "GA":
                algorithm = new GaWhole(dim, size, iter, min, max);
                break;

        }

        algorithm.setFunction(new FunctionImpl(dim,function));
        algorithm.run();
//		double[] position =algorithm.getBestPosition();
        long endMili = System.currentTimeMillis();
        double[] wholeValue = algorithm.getWholeValue();
//		double bestValue = algorithm.getBestValue();
        data .append( wholeValue[0]);
        for (int i = 1; i < wholeValue.length; i++) {
            data .append( ",");
            data .append( wholeValue[i]);
        }
        data .append( "," + (endMili - startMili));
        data .append( "\n");
        System.out.println(currentNum+":总耗时为：" + (endMili - startMili) + "毫秒");
        currentNum++;
        return data;
    }

    private static Range Range(double min, double max) {
        // TODO Auto-generated method stub
        Range range = new Range(min, max);
        return range;
    }
}
