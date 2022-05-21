package com.thecout.lox;

public class Counter
{
    private int value;
    private int maxValue;

    public Counter(int startValue, int maxValue)
    {
        this.value = startValue;
        this.maxValue = maxValue;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (this.value <= maxValue+1)
        {
            this.value = value;
        }
    }
}
