package com.neuralFlux.Saas_OOH_demo.enums;

public enum PanelType {
    OUTDOOR(2),
    FRONT_LIGHT(2),
    TRIEDRO(3),
    LED(5),
    EMPENA(1),
    RODOVIARIO(2);

    private final int maxFaces;

    PanelType(int maxFaces){
        this.maxFaces = maxFaces;
    }

    public int getMaxFaces(){
        return maxFaces;
    }
}

