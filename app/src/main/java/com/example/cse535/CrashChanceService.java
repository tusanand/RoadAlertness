package com.example.cse535;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.analysis.solvers.LaguerreSolver;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math3.ode.nonstiff.GillIntegrator;
import org.apache.commons.math3.ode.nonstiff.LutherIntegrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;
import org.apache.commons.math3.ode.nonstiff.ThreeEighthesIntegrator;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

public class CrashChanceService {

    public CrashChanceService() {
    }

    public void getCrashChanceAsync(String cogWorkload, double reactionTime, CrashChanceListener listener) {
        new CrashChanceTask(cogWorkload, reactionTime, listener).execute();
    }

    private static class CrashChanceTask extends AsyncTask<Void, Void, String> {

        private static final double uD = -0.1;
        private static final double TIME_STEP = 0.01;

        private double currentStep = 0;
        private double DECEL_LIM;
        private double REACTION_TIME;
        private CrashChanceListener listener;


        public CrashChanceTask(String cogWorkload, double reactionTime, CrashChanceListener listener) {
            this.REACTION_TIME = reactionTime;
            this.listener = listener;

            this.DECEL_LIM = -150.0;
            if (cogWorkload.equals("LCW")) {
                this.DECEL_LIM = -200.0;
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            return simulateCrash(0.0, 60.0, -43.8);
        }

        @Override
        protected void onPostExecute(String crashChance) {
            super.onPostExecute(crashChance);
            if (listener != null) {
                listener.onCrashChanceCalculated(crashChance);
            }
        }

        public String simulateCrash(double ax_initial, double vx_initial, double sx_initial) {
            double uD = -0.1;
            ArrayList<Double> ax_record = new ArrayList<>();
            ax_record.add(ax_initial);

            ArrayList<Double> vx_record = new ArrayList<>();
            vx_record.add(vx_initial);

            ArrayList<Double> sx_record = new ArrayList<>();
            sx_record.add(sx_initial);

            ArrayList<Double> jerk_record = new ArrayList<>();
            ArrayList<Double> decelLim_record = new ArrayList<>();

            while (vx_record.get(vx_record.size() - 1) > 0.000001) {
                decelLim_record.add(getLimit());
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
                return "Crash";
            }
            return "No Crash";
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
            FirstOrderIntegrator integrator = new EulerIntegrator(timeStep);

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

        private double getLimit() {
            if (this.currentStep < this.REACTION_TIME) {
                return -0.1;
            } else {
                return DECEL_LIM * 1.1;
            }
        }
    }

    public interface CrashChanceListener {
        void onCrashChanceCalculated(String heartRate);
    }
}
