package com.example.cse535;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class TRFuzzyLogicController {
    private static final double e = 2.71828d;
    private static final double pi = 3.14;
    private static ShareReactionTimeData shareRTD;
    private static ShareSymptomsData shareSD;
    private static ShareHeartRespiratorySleepData shareHRSD;

    public static double ComputeFuzzy() {
        shareHRSD = ShareHeartRespiratorySleepData.getInstance();
        double hr = shareHRSD.getHeartRateValue() * 1.0;
        double rr = shareHRSD.getRespiratoryValue() * 1.0;
        double sleep = shareHRSD.getSleepValue();

        shareRTD = ShareReactionTimeData.getInstance();
        double tr = shareRTD.getReactionTime() * 1.0 / 1000;

        shareSD = ShareSymptomsData.getInstance();
        double symptom = shareSD.getSymptomComputedEffect();

        int hrMembership = 0;
        int rrMembership = 0;
        int trMembership = 0;
        int sleepMembership = 0;
        int symptomMembership = 0;

        if (HRMFL(hr) < HRMFH(hr)) {hrMembership = 1;}
        if (RRMFL(rr) < RRMFH(rr)) { rrMembership = 1;}
        if (TRMFL(tr) < TRMFH(tr)) {trMembership = 1;}
        if (SLEEPMFL(sleep) < SLEEPMFH(sleep)) {sleepMembership = 1;}
        if (SYMPTMFL(symptom) < SYMPTMFH(symptom)) { symptomMembership = 1;}

        boolean[] rules = EvaluateRules(hrMembership, rrMembership, trMembership, sleepMembership, symptomMembership);

        double[] function = new double[1000];

        //aggregate outputs
        for (double i = 0; i <= 1; i += 0.001) {

            double ruleImpact0 = 0;
            double ruleImpact1 = 0;
            double ruleImpact2 = 0;
            double ruleImpact3 = 0;
            double ruleImpact4 = 0;
            double ruleImpact5 = 0;

            if (rules[0]) {
                Double[] values = {HRMFL(hr), RRMFL(rr)};
                ruleImpact0 = Collections.max(Arrays.asList(values));

                if (OUTPUTL(i) < ruleImpact0) {
                    ruleImpact0 = OUTPUTL(i);
                }

            }

            if (rules[1]) {
                Double[] values = {HRMFL(hr), RRMFH(rr)};
                ruleImpact1 = Collections.max(Arrays.asList(values));

                if (OUTPUTH(i) < ruleImpact1) {
                    ruleImpact1 = OUTPUTH(i);
                }
            }

            if (rules[2]) {
                Double[] values = {HRMFH(hr), RRMFL(rr)};
                ruleImpact2 = Collections.max(Arrays.asList(values));

                if (OUTPUTL(i) < ruleImpact2) {
                    ruleImpact2 = OUTPUTL(i);
                }
            }

            if (rules[3]) {
                ruleImpact3 = TRMFH(tr);

                if (OUTPUTH(i) < ruleImpact3) {
                    ruleImpact3 = OUTPUTH(i);
                }
            }

            if (rules[4]) {
                ruleImpact4 = SLEEPMFH(sleep);

                if (OUTPUTH(i) < ruleImpact4) {
                    ruleImpact4 = OUTPUTH(i);
                }

            }

            if (rules[5]) {
                ruleImpact5 = SYMPTMFH(symptom);

                if (OUTPUTH(i) < ruleImpact5) {
                    ruleImpact5 = OUTPUTH(i);
                }
            }

            Double[] potentialValues = {ruleImpact0, ruleImpact1, ruleImpact2, ruleImpact3, ruleImpact4, ruleImpact5};

            int index = (int)(i*1000);
            function[index] = Collections.max(Arrays.asList(potentialValues));
        }

        return computeCentroid(function, trMembership);
    }


    private static int closestIndexOf(double[] function, double x) {

        double lowest = 1000000;
        int index = 0;

        for (int i = 0; i < function.length; i++) {
            double diff = (Math.abs(function[i] - x));
            if (diff <= lowest) {
                index = i;
                lowest = diff;
            }
        }
        return index;
    }

    private static double mean(double[] function) {

        double sum = 0;

        for (double i : function) {
            sum += i;
        }

        return sum / function.length;

    }

    private static int medianIndex(double[] function) {

        ArrayList<IndexedListValue> func2 = new ArrayList<IndexedListValue>();

        for (int i = 0; i < function.length; i++) {
            IndexedListValue temp = new IndexedListValue();
            temp.index = i;
            temp.value = function[i];
            func2.add(temp);
        }

        Collections.sort(func2, new Sorter() );

        return func2.get((int)function.length/2).index;


    }

    private static double medianValue(double[] function) {

        ArrayList<IndexedListValue> func2 = new ArrayList<IndexedListValue>();

        for (int i = 0; i < function.length; i++) {
            IndexedListValue temp = new IndexedListValue();
            temp.index = i;
            temp.value = function[i];
            func2.add(temp);
        }

        Collections.sort(func2, new Sorter() );

        return func2.get((int)function.length/2).value;


    }

    private static int computeCentroid(double[] function, int trMembership)
    {
        int reactionTime = ShareReactionTimeData.getInstance().getReactionTime();
        int left = FindLeft(function);

        int right = FindRight(function);

        double area = Area(function, left, right);

        int outArea = XBar(function, left, right, area);
        return (trMembership == 0) ? outArea : reactionTime;
    }

    private static int FindLeft (double[] function) {


        for (int i = 0; i < function.length; i++) {

            if (function[i] > 0 ) {
                return i;
            }

        }
        return 0;
    }

    private static int FindRight(double[] function) {

        boolean inFunction = false;

        for (int i = 0; i < function.length; i++) {

            if (!inFunction && function[i] != 0) {
                inFunction = true;
            }
            if (inFunction && function[i] == 0) {
                return i;
            }

        }
        return function.length;
    }

    private static double Area(double[] function, int left, int right) {

        double area = 0;

        for (int i = left; i < right; i++) {

            area += function[i];

        }
        return area;
    }

    private static int XBar(double[] function, int left, int right, double area) {

        int sum = 0;

        for (int i = left; i < right; i++) {

            sum += i * function[i];

        }

        return (int)(sum / area);

    }

    private static double Gauss(double x, double mu, double std) {

        double exp = -0.5 * Math.pow(((x - mu)/std), 2);
        return (1/(std * Math.sqrt(2 * Math.PI))) * Math.exp(exp);

    }

    private static double Trimf(double a, double b, double c, double x) {

        if (x <= a) {
            Log.d("Debug", String.valueOf(0));
            return 0;}

        if (a <= x  && x <= b) {
            Log.d("Debug", String.valueOf((x-a)/(b-a)));
            return (x-a)/(b-a); }

        if (b <= x && x <= c) {
            Log.d("Debug", String.valueOf((c-x)/(c-b)));
            return (c-x)/(c-b); }

        Log.d("Debug", String.valueOf(0));
        return 0;

    }

    private static double HRMFL(double hr) {
        if (hr > 200) { hr = 200;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 60, 75 );
    }

    private static double HRMFH(double hr) {
        if (hr > 200) { hr = 200;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 120, 60);
    }

    private static double RRMFL(double hr) {
        if (hr > 20) { hr = 20;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 12, 5);
    }

    private static double RRMFH(double hr) {
        if (hr > 20) { hr = 20;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 17, 5.055);
    }

    private static double TRMFL(double hr) {
        if (hr > 1) { hr = 1;}
        if (hr < 0) {hr = 0;}
        return Trimf(0.0,0.25,  0.3902, hr);
    }

    private static double TRMFH(double hr) {
        if (hr > 1) { hr = 1;}
        if (hr < 0) {hr = 0;}
        return Trimf(0.0,.82,  1, hr);
    }

    private static double SLEEPMFL(double hr) {
        if (hr > 14) { hr = 14;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 3, .7);
    }

    private static double SLEEPMFH(double hr) {
        if (hr > 14) { hr = 14;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 8, 4);
    }

    private static double SYMPTMFL(double hr) {
        if (hr > 23.25) { hr = 23.25;}
        if (hr < 0) {hr = 0;}
        return Trimf(-.16,5.5,  15, hr);
    }

    private static double SYMPTMFH(double hr) {
        if (hr > 23.25) { hr = 23.25;}
        if (hr < 0) {hr = 0;}
        return Gauss(hr, 23.23, 10);
    }

    private static double OUTPUTL(double x) {
        if (x > 1) { x = 1;}
        if (x < 0) {x = 0;}
        return Trimf(0, .2, .45, x);
    }

    private static double OUTPUTH(double x) {
        if (x > 1) { x = 1;}
        if (x < 0) {x = 0;}
        return Trimf(.3, 1, 1.1, x);
    }

    private static boolean[] EvaluateRules(int hr, int rr, int tr, int sleep, int sympt) {

        /*
        Rules:
        1: If hr is low and rr is low, output is low
        2: if hr is low and rr is high, output is high
        3: if hr is high and rr is low, output is low
        4: if tr is high, output is high
        5: if sleep is low, output is high
        6: if symptoms is high, output is high
         */
        boolean[] rules = new boolean[6]; //if rules apply

        if (hr == 0 && rr == 0) { rules[0] = true; }

        if (hr == 0 && rr == 1) {rules[1] = true;}

        if (hr == 1 && rr == 0) {rules[2] = true; }

        if (tr == 1) { rules[3] = true; }

        if (sleep == 0) {rules[4] = true; }

        if (sympt == 1) { rules[5] = true; }

        return rules;

    }

}

class IndexedListValue {

    int index;
    double value;

}

class Sorter implements Comparator<IndexedListValue> {

    public int compare(IndexedListValue a, IndexedListValue b) {

        return Double.compare(a.value, b.value);

    }

}
