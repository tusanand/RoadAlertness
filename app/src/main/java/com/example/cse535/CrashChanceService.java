package com.example.cse535;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.EnumMap;


public class CrashChanceService {

    public CrashChanceService() {
    }

    public void getCrashChanceAsync(String cogWorkload, CrashChanceListener listener) throws Exception {
        new CrashChanceTask(cogWorkload, listener).execute();
    }

    private static class CrashChanceTask extends AsyncTask<Void, Void, String> {

        private static final double uD = -0.1;
        private static final double TIME_STEP = 0.01;

        private double DECEL_LIM;
        private final double REACTION_TIME;
        private final CrashChanceListener listener;

        private ShareCrashSpeedData shareCSD;
        private ShareReactionTimeData shareRTD;

        private enum Crash {
            WILL_NOT_CRASH,
            WILL_CRASH
        }

        public CrashChanceTask(String cogWorkload, CrashChanceListener listener) throws Exception {
            shareCSD = ShareCrashSpeedData.getInstance();
            shareRTD = ShareReactionTimeData.getInstance();
            int reactionTime = shareRTD.getReactionTime();

            this.listener = listener;

            if (reactionTime <= 0) throw new Exception();
            this.REACTION_TIME = reactionTime * 1.0 / 1000;

            this.DECEL_LIM = -150.0;
            if (cogWorkload.equals("LCW")) this.DECEL_LIM = -200.0;
        }

        @Override
        protected String doInBackground(Void... voids) {
            int exitVal = 150;
            for (int i = 20; i < 150; i += 10) {
                if (simulateCrash(0.0, i, -1 * distanceBehindKmph(i)) == Crash.WILL_CRASH) {
                    exitVal = i;
                    break;
                }
            }

            shareCSD.setCrashSpeed(exitVal);
            return String.valueOf(exitVal);
        }

        @Override
        protected void onPostExecute(String crashChance) {
            super.onPostExecute(crashChance);
            if (listener != null) {
                listener.onCrashChanceCalculated(crashChance);
            }
        }

        public static double distanceBehindKmph(double speedKmph) {
            // Convert time from seconds to hours
            double timeHours = 3.5 / 3600.0;

            // Calculate the distance using the formula: Distance = Speed * Time
            double distanceMeters = speedKmph * timeHours * 1000;

            return distanceMeters;
        }

        public Crash simulateCrash(double ax_initial, double vx_initial, double sx_initial) {
            double currentStep = 0;

            ArrayList<Double> ax_record = new ArrayList<>();
            ax_record.add(ax_initial);

            ArrayList<Double> vx_record = new ArrayList<>();
            vx_record.add(vx_initial);

            ArrayList<Double> sx_record = new ArrayList<>();
            sx_record.add(sx_initial);

            ArrayList<Double> jerk_record = new ArrayList<>();
            ArrayList<Double> decelLim_record = new ArrayList<>();

            while (vx_record.get(vx_record.size() - 1) > 0.000001) {
                decelLim_record.add(getLimit(currentStep));
                jerk_record.add(calcJerk(
                        decelLim_record.get(decelLim_record.size() - 1),
                        sx_record.get(sx_record.size() - 1),
                        vx_record.get(vx_record.size() - 1),
                        ax_record.get(ax_record.size() - 1)
                ));

                if (jerk_record.size() == 1) {
                    double[] ax_inputs = {jerk_record.get(0)};
                    ax_record.add(customIntegrate(ax_inputs, ax_record.get(ax_record.size() - 1), TIME_STEP));
                }
                else {
                    double[] ax_inputs = {jerk_record.get(jerk_record.size() - 2), jerk_record.get(jerk_record.size() - 1)};
                    ax_record.add(customIntegrate(ax_inputs, ax_record.get(ax_record.size() - 1), TIME_STEP));
                }

                double[] vx_inputs = {ax_record.get(ax_record.size() - 2), ax_record.get(ax_record.size() - 1)};
                vx_record.add(customIntegrate(vx_inputs, vx_record.get(vx_record.size() - 1), TIME_STEP));

                double[] sx_inputs = {vx_record.get(vx_record.size() - 2), vx_record.get(vx_record.size() - 1)};
                sx_record.add(customIntegrate(sx_inputs, sx_record.get(sx_record.size() - 1), TIME_STEP));

                currentStep += TIME_STEP;
            }

            if(sx_record.get(sx_record.size() - 1) > 0)
            {
                return Crash.WILL_CRASH;
            }
            return Crash.WILL_NOT_CRASH;
        }

        public static double customIntegrate(double[] values, double initial_value, double timeStep) {
            if (values.length == 1) {
                return values[0] * timeStep;
            }

            double[] timePoints = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                timePoints[i] = i * timeStep;
            }

            UnivariateFunction valueFunc = new LinearInterpolator().interpolate(timePoints, values);
            FirstOrderDifferentialEquations ode = new FirstOrderDifferentialEquations() {
                @Override
                public int getDimension() {
                    return 1;
                }

                @Override
                public void computeDerivatives(double t, double[] y, double[] yDot) {
                    yDot[0] = valueFunc.value(t);
                }
            };
            FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(timeStep);

            double[] y0 = {initial_value};
            integrator.integrate(ode, 0, y0, (values.length - 1) * timeStep, y0);
            return y0[0];
        }

        private double calcJerk(double decelLim, double sx, double vx, double ax) {
            if (decelLim < DECEL_LIM * 1.1) {
                decelLim = DECEL_LIM * 1.1;
            } else if (decelLim > 0) {
                decelLim = 0;
            }
            return decelLim - 0.01 * sx - 0.3 * vx - 0.5 * ax;
        }

        private double getLimit(double currentStep) {
            if (currentStep < this.REACTION_TIME) {
                return uD;
            } else {
                return DECEL_LIM * 1.1;
            }
        }
    }

    public interface CrashChanceListener {
        void onCrashChanceCalculated(String heartRate);
    }
}
