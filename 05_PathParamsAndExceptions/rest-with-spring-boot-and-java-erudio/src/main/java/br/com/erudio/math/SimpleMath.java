package br.com.erudio.math;


public class SimpleMath {

    public Double sum(Double numberOne, Double numberTwo) {
        return numberOne + numberTwo;
    }

    public Double min(Double numberOne, Double numberTwo) {
        return numberOne - numberTwo;
    }

    public Double multi(Double numberOne, Double numberTwo) {
        return numberOne * numberTwo;
    }

    public Double div(Double numberOne, Double numberTwo) {
        return numberOne / numberTwo;
    }

    public Double mean(Double numberOne, Double numberTwo) {
        return (numberOne + numberTwo) / 2;
    }

    public Double sqrt(Double number) {
        return Math.sqrt(number);
    }

}
