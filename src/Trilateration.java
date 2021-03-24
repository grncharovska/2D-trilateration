import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Trilateration {
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        Scanner sc = new Scanner(System.in);
        Random r = new Random();


        System.out.println("Insert number of nodes in the network: ");
        int N = sc.nextInt();

        System.out.println();

        System.out.println("Insert area dimensions: ");
        int L = sc.nextInt();

        System.out.println();

        System.out.println("Insert listen range: ");
        int R = sc.nextInt();

        System.out.println();

        System.out.println("Insert percentage of anchor nodes: ");
        int AncPercent = sc.nextInt();

        System.out.println();

        int anchor = (N * AncPercent) / 100;
        if (anchor < 3) anchor = 3;

        System.out.println("Insert noise: ");
        int Noise = sc.nextInt();

        //just anchor nodes
        ArrayList<Point> anchors = new ArrayList<>();
        //all nodes
        ArrayList<Point> nodes = new ArrayList<>();
        ArrayList<Point> nodesTemp = new ArrayList<>(); //the ones I need
        ArrayList<Point> found = new ArrayList<>(); //found nodes
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Integer> neighbors = new ArrayList<>();

        int num = N - anchor;

        // int neighboursSum = 0;
        int neighborsCount ;

        for (int i = 0; i < anchor; i++) {

            int x = r.nextInt(L);
            int y = r.nextInt(L);
            Point p = new Point(x, y, true);
            anchors.add(p);
            nodes.add(p);
        }

        for (int i = 0; i < num; i++) {

            double x = r.nextInt(L);
            double y = r.nextInt(L);
            Point p = new Point(x, y, false);
            nodes.add(p);
            nodesTemp.add(p);
        }

        double d1;
        double d2;
        double d3;

        double[] err = new double[nodes.size()];
        //boolean f = false;

        System.out.println(err.length);

        while (nodes.size() != anchors.size()) {

            for (int i = 0; i < nodes.size(); i++) {
                distances.clear();
                neighborsCount = 0;

                if (!nodes.get(i).isAnchor) {

                    for (Point point : anchors) {
                        double d = Distance(point.x, point.y, nodes.get(i).x, nodes.get(i).y);
                        if (d < R) neighborsCount++;
                        double shum = d * Noise / 100;
                        d += shum;
                        distances.add((int) d);
                    }

                    int[] niza;
                    niza = Min(distances);
                    d1 = niza[0];
                    int poz1 = niza[1];

                    double x1 = anchors.get(poz1).x;
                    double y1 = anchors.get(poz1).y;

                    d2 = niza[2];
                    int poz2 = niza[3];

                    double x2 = anchors.get(poz2).x;
                    double y2 = anchors.get(poz2).y;

                    d3 = niza[4];
                    //int poz3 = niza[5];

                    //double x3 = anchors.get(poz3).x;
                    //double y3 = anchors.get(poz3).y;

                    double d = Distance(nodes.get(i).x, nodes.get(i).y, 0, 0);

                    if (d1 <= R && d2 <= R && d3 <= R) {
                        double x = GetX(x1, y1, x2, y2, d1, d2, d);
                        double y = GetY(x1, y1, x2, y2, d1, d2, d);
                        Point p = new Point(x, y, true);
                        err[i] = Distance(x, y, nodes.get(i).x, nodes.get(i).y);
                        anchors.add(p);
                        found.add(p);
                        nodes.get(i).isAnchor = true;
                        neighborsCount++;
                    }

                }

                neighbors.add(neighborsCount);
            }

            if (AllNodesFound(nodes)) {
                break;
            }
        }

        System.out.println("Original: " + "\t" + "Found: ");
        for (int i = 0; i < nodesTemp.size(); i++) {

            System.out.println("X: " + String.format("%.1f", nodesTemp.get(i).x) + "\t" + "Y: " + String.format("%.1f", nodesTemp.get(i).y) + "\t" + "X: " + String.format("%.1f", found.get(i).x) + "\t" + "Y: " + String.format("%.1f", found.get(i).y));

        }

        System.out.println();

        System.out.println("Node size: " + nodes.size());
        System.out.println("Found size:" + anchors.size());

        double sum = 0;
        double avg;
        for (double v : err) {
            sum += v;
        }

        avg = sum / (err.length);
        System.out.println("Avg error including anchor nodes: " + String.format("%.1f", avg));
        double percentage = avg * 100 / R;
        System.out.println("Error: " + String.format("%.1f", percentage) + " in this range");

        double[] error = new double[nodesTemp.size()];
        sum = 0;

        for (int i = 0; i < nodesTemp.size(); i++) {
            double d = Distance(nodesTemp.get(i).x, nodesTemp.get(i).y, found.get(i).x, found.get(i).y);
            error[i] = d;
            sum += error[i];
        }

        avg = sum / (error.length);
        System.out.println("Avg error without anchor nodes: " + String.format("%.1f", avg));
        percentage = avg * 100 / R;
        System.out.println("Error: " + String.format("%.1f", percentage) + " in this range");

        int suma = 0;
        for (Integer neighbor : neighbors) {
            suma += neighbor;
        }
        double averageN = suma / neighbors.size();
        System.out.println("Avg number of neighbours: " + averageN);

    }


    public static double Distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static double GetX(double x1, double y1, double x2, double y2, double d1, double d2, double d3) {

        //Checking
        return (y1 * (x2 * x2 + y2 * y2 - d2 * d2 + d3 * d3) - y2 * (x1 * x1 + y1 * y1 - d1 * d1 + d3 * d3)) / (2 * (x2 * y1 - x1 * y2));

    }

    public static double GetY(double x1, double y1, double x2, double y2, double d1, double d2, double d3) {

        //Checking
        return (x1 * (x2 * x2 + y2 * y2 - d2 * d2 + d3 * d3) - x2 * (x1 * x1 + y1 * y1 - d1 * d1 + d3 * d3)) / (2 * (x1 * y2 - x2 * y1));

    }

    public static int[] Min(ArrayList<Integer> list) {
        int current = Integer.MAX_VALUE;

        int poz = 0;
        int[] array = new int[6];

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < current) {
                current = list.get(i);
                poz = i;
            }
        }
        array[0] = current;
        array[1] = poz;
        list.set(poz, 1000000);
        current = Integer.MAX_VALUE;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < current) {
                current = list.get(i);
                poz = i;
            }
        }

        array[2] = current;
        array[3] = poz;
        list.set(poz, 100000);
        current = Integer.MAX_VALUE;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < current) {
                current = list.get(i);
                poz = i;
            }
        }

        array[4] = current;
        array[5] = poz;

        return array;

    }

    public static boolean AllNodesFound(ArrayList<Point> list) {

        boolean flag = true;

        for (Point point : list) {
            if (!point.isAnchor) {
                flag = false;
                break;
            }
        }
        return flag;
    }


}
