package com.example.weather;
public class MathOperation {
    public double round(double number, int fractionLength)
    { int x = 1;

        for(int y = 1 ; y <= fractionLength; y++, x *= 10){}

        number = (double)((double)Math.round(number * x) / x);

        return number;
    }

}