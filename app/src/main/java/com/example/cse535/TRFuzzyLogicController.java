package com.example.cse535;

import java.util.Arrays;
import java.util.Collections;

public class TRFuzzyLogicController {
    private static final double e = 2.71828d;
    private static final double pi = 3.14;

    public static double ComputeFuzzy(double hr, double rr, double tr, double sleep, double symptom) {

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
                Double[] values = {HRMFL(hr), RRMFL(rr), OUTPUTL(i)};
                ruleImpact0 = Collections.max(Arrays.asList(values));
            }

            if (rules[1]) {
                Double[] values = {HRMFL(hr), RRMFH(rr), OUTPUTH(i)};
                ruleImpact1 = Collections.max(Arrays.asList(values));
            }

            if (rules[2]) {
                Double[] values = {HRMFH(hr), RRMFL(rr), OUTPUTL(i)};
                ruleImpact2 = Collections.max(Arrays.asList(values));
            }

            if (rules[3]) {
                Double[] values = {TRMFH(tr),  OUTPUTH(i)};
                ruleImpact3 = Collections.max(Arrays.asList(values));
            }

            if (rules[4]) {
                Double[] values = {SLEEPMFH(sleep),  OUTPUTH(i)};
                ruleImpact4 = Collections.max(Arrays.asList(values));
            }

            if (rules[5]) {
                Double[] values = {SYMPTMFH(symptom),  OUTPUTH(i)};
                ruleImpact5 = Collections.max(Arrays.asList(values));
            }

            Double[] potentialValues = {ruleImpact0, ruleImpact1, ruleImpact2, ruleImpact3, ruleImpact4, ruleImpact5};
            int index = (int)(i*1000);
            function[index] = Collections.max(Arrays.asList(potentialValues));
        }

        double median = median(function);

        int index = indexOf(function, median);

        return index/1000.0;
    }

    private static int indexOf(double[] function, double x) {

        for (int i = 0; i < function.length; i++) {
            if (function[i] == x) {
                return i;
            }
        }
        return -1;
    }

    private static double median(double[] function) {

        double[] functionCopy = function.clone();

        double[] function2 = Arrays.stream(functionCopy).sorted().toArray();

        return function2[(int)function2.length/2];

    }

    private static double Gauss(double mean, double std, double x) {
        return (1 / (std * Math.sqrt(2 * pi))) * Math.pow(e, (-1 * Math.pow((x - mean), 2)) / (2 * Math.pow(std, 2)));
    }

    private static double Trimf(double peak, double foot1, double foot2, double x) {

        if (x <= foot1) { return 0; }

        if (x < peak) {
            return (x - foot1)/(peak - foot1);
        }

        if (x > peak && x < foot2) {
            return (foot2 - x)/(foot2 - peak);
        }

        return 0;
    }

    private static double HRMFL(double hr) {
        if (hr > 200) { hr = 200;}
        if (hr < 0) {hr = 0;}
        return Gauss(1.35, 55.86, hr);
    }

    private static double HRMFH(double hr) {
        if (hr > 200) { hr = 200;}
        if (hr < 0) {hr = 0;}
        return Gauss(198, 55.64, hr);
    }

    private static double RRMFL(double hr) {
        if (hr > 20) { hr = 20;}
        if (hr < 0) {hr = 0;}
        return Gauss(0, 9.729, hr);
    }

    private static double RRMFH(double hr) {
        if (hr > 20) { hr = 20;}
        if (hr < 0) {hr = 0;}
        return Gauss(20, 5.055, hr);
    }

    private static double TRMFL(double hr) {
        if (hr > 1) { hr = 1;}
        if (hr < 0) {hr = 0;}
        return Trimf(0.0053, -0.411, 0.3902, hr);
    }

    private static double TRMFH(double hr) {
        if (hr > 1) { hr = 1;}
        if (hr < 0) {hr = 0;}
        return Trimf(1, 0.09656, 1.42, hr);
    }

    private static double SLEEPMFL(double hr) {
        if (hr > 14) { hr = 14;}
        if (hr < 0) {hr = 0;}
        return Gauss(2.22e-16, 3.476, hr);
    }

    private static double SLEEPMFH(double hr) {
        if (hr > 14) { hr = 14;}
        if (hr < 0) {hr = 0;}
        return Gauss(14, 5.489, hr);
    }

    private static double SYMPTMFL(double hr) {
        if (hr > 100) { hr = 100;}
        if (hr < 0) {hr = 0;}
        return Trimf(-8.88e-16, -41.7, 51.46, hr);
    }

    private static double SYMPTMFH(double hr) {
        if (hr > 100) { hr = 100;}
        if (hr < 0) {hr = 0;}
        return Gauss(100, 27.97, hr);
    }

    private static double OUTPUTL(double x) {
        if (x > 1) { x = 1;}
        if (x < 0) {x = 0;}
        return Gauss(0.0017, 0.148, x);
    }

    private static double OUTPUTH(double x) {
        if (x > 1) { x = 1;}
        if (x < 0) {x = 0;}
        return Gauss( 0.985, 0.2805, x);
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
