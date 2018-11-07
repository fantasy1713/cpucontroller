import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Controller {
    public static void main(String[] args) {
        int busyTime = 500;
        double targetUsage = 30;
        while (true) {
            Thread current = Thread.currentThread();
            long start = System.currentTimeMillis();

            int temp = 0;
            while ((System.currentTimeMillis() - start) < busyTime) {
                System.out.print(":"+busyTime  );
                temp++;
            }
            temp = 0;
            double usage = getCpuRateForLinux();
            System.out.println("busyTime : "+busyTime  );
            System.out.println(usage);
            if ((usage - targetUsage) > 10 && busyTime > 1) {
                System.out.println("slowdown");
                busyTime--;
            }else if((usage - targetUsage) < -10 ){
                System.out.println("speedup");
                busyTime++;
            }else{

            }


            try {
                System.out.println("sleep");
                current.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        while (true) {
//            double usage = getCpuRateForLinux();
//            System.out.println(usage);
//        }
    }

//    /**
//     * 功能：Linux CPU使用信息
//     */
//    public static Map<?, ?> cpuinfo() {
//        InputStreamReader inputs = null;
//        BufferedReader buffer = null;
//        Map<String, Object> map = new HashMap<String, Object>();
//        try {
//            inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
//            buffer = new BufferedReader(inputs);
//            String line = "";
//            while (true) {
//                line = buffer.readLine();
//                if (line == null) {
//                    break;
//                }
//                if (line.startsWith("cpu")) {
//                    StringTokenizer tokenizer = new StringTokenizer(line);
//                    List<String> temp = new ArrayList<String>();
//                    while (tokenizer.hasMoreElements()) {
//                        String value = tokenizer.nextToken();
//                        temp.add(value);
//                    }
//                    map.put("user", temp.get(1));
//                    map.put("nice", temp.get(2));
//                    map.put("system", temp.get(3));
//                    map.put("idle", temp.get(4));
//                    map.put("iowait", temp.get(5));
//                    map.put("irq", temp.get(6));
//                    map.put("softirq", temp.get(7));
//                    map.put("stealstolen", temp.get(8));
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            logger.debug(e);
//        } finally {
//            try {
//                buffer.close();
//                inputs.close();
//            } catch (Exception e2) {
//                logger.debug(e2);
//            }
//        }
//        return map;
//    }

    private static double getCpuRateForLinux() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        String linuxVersion = null;
        StringTokenizer tokenStat = null;
        try {
//            System.out.println("Get usage rate of CUP , linux version: "
//                    + linuxVersion);

            Process process = Runtime.getRuntime().exec("top");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);

            if ("2.4".equals(linuxVersion)) {
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();

                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
                String user = tokenStat.nextToken();
                tokenStat.nextToken();
                String system = tokenStat.nextToken();
                tokenStat.nextToken();
                String nice = tokenStat.nextToken();

                System.out.println(user + " , " + system + " , " + nice);

                user = user.substring(0, user.indexOf("%"));
                system = system.substring(0, system.indexOf("%"));
                nice = nice.substring(0, nice.indexOf("%"));

                float userUsage = new Float(user).floatValue();
                float systemUsage = new Float(system).floatValue();
                float niceUsage = new Float(nice).floatValue();

                return (userUsage + systemUsage + niceUsage) / 100;
            } else {
//                System.out.println(brStat.readLine());
//                System.out.println(brStat.readLine());
//                System.out.println(brStat.readLine());
                brStat.readLine();
                brStat.readLine();
                brStat.readLine();

                tokenStat = new StringTokenizer(brStat.readLine());
                tokenStat.nextToken();
                tokenStat.nextToken();
//                tokenStat.nextToken();
//                tokenStat.nextToken();
//                tokenStat.nextToken();
//                tokenStat.nextToken();
//                tokenStat.nextToken();
                String cpuUsage = tokenStat.nextToken();

                System.out.println("CPU usage : " + cpuUsage);
                Float usage = new Float(cpuUsage.substring(0, cpuUsage
                        .indexOf("%")));

                return usage.doubleValue();
            }

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            freeResource(is, isr, brStat);
            return 1;
        } finally {
            freeResource(is, isr, brStat);
        }

    }

    private static void freeResource(InputStream is, InputStreamReader isr,
                                     BufferedReader br) {
        try {
            if (is != null)
                is.close();
            if (isr != null)
                isr.close();
            if (br != null)
                br.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
//    public static void main(String[] args) throws Exception {
//    // 角度的分割
//    final double SPLIT = 0.01;
//    //
//    // 2PI分割的次数，也就是2/0.01个，正好是一周
//    final int COUNT = (int) (2 / SPLIT);
//    final double PI = Math.PI;
//    // 时间间隔
//    final int INTERVAL = 200;
//    long[] busySpan = new long[COUNT];
//    long[] idleSpan = new long[COUNT];
//    int half = INTERVAL / 2;
//    double radian = 0.0;
//    for (int i = 0; i < COUNT; i++) {
//      busySpan[i] = (long) (half + (Math.sin(PI * radian) * half));
//      idleSpan[i] = INTERVAL - busySpan[i];
//      radian += SPLIT;
//    }
//    long startTime = 0;
//    int j = 0;
//    while (true) {
//      j = j % COUNT;
//      startTime = System.currentTimeMillis();
//      while (System.currentTimeMillis() - startTime < busySpan[j])
//        ;
//      Thread.sleep(idleSpan[j]);
//      j++;
//    }
//  }

}
