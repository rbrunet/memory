package org.forwardlogic.kafka.streams.memory;

public class UsedMemoryCountAndSum {

    private long count;
    private float sum;
    private float average;

    public UsedMemoryCountAndSum() {}

    public UsedMemoryCountAndSum(long count, float sum) {
        this.count = count;
        this.sum = sum;
    }


    public void computeAverage() {
        this.average = this.sum/this.count;
    }

    public float getAverage() {
        return average;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public float getSum() {
        return sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }
}
