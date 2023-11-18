package com.example.cse535;

public class ShareSymptomsData {
    private static ShareSymptomsData shareSymptomsData;
    private int nausea;
    private int headache;
    private int diarrhea;
    private int soar_throat;
    private int fever;
    private int muscle_ache;
    private int no_smell_taste;
    private int cough;
    private int breathlessness;
    private int tired;
    private double symptomComputedEffect;

    public static ShareSymptomsData getInstance() {
        if(shareSymptomsData != null) {
            return shareSymptomsData;
        }
        shareSymptomsData =  new ShareSymptomsData();
        return  shareSymptomsData;
    }

    public int getNausea() {
        return nausea;
    }

    public void setNausea(int nausea) {
        this.nausea = nausea;
    }

    public int getHeadache() {
        return headache;
    }

    public void setHeadache(int headache) {
        this.headache = headache;
    }

    public int getDiarrhea() {
        return diarrhea;
    }

    public void setDiarrhea(int diarrhea) {
        this.diarrhea = diarrhea;
    }

    public int getSoar_throat() {
        return soar_throat;
    }

    public void setSoar_throat(int soar_throat) {
        this.soar_throat = soar_throat;
    }

    public int getFever() {
        return fever;
    }

    public void setFever(int fever) {
        this.fever = fever;
    }

    public int getMuscle_ache() {
        return muscle_ache;
    }

    public void setMuscle_ache(int muscle_ache) {
        this.muscle_ache = muscle_ache;
    }

    public int getNo_smell_taste() {
        return no_smell_taste;
    }

    public void setNo_smell_taste(int no_smell_taste) {
        this.no_smell_taste = no_smell_taste;
    }

    public int getCough() {
        return cough;
    }

    public void setCough(int cough) {
        this.cough = cough;
    }

    public int getBreathlessness() {
        return breathlessness;
    }

    public void setBreathlessness(int breathlessness) {
        this.breathlessness = breathlessness;
    }

    public int getTired() {
        return tired;
    }

    public void setTired(int tired) {
        this.tired = tired;
    }

    public double getSymptomComputedEffect() {
        return symptomComputedEffect;
    }

    public void setSymptomComputedEffect(double symptomComputedEffect) {
        this.symptomComputedEffect = symptomComputedEffect;
    }
}
