package org.forwardlogic.kafka.streams.memory;

public class UsedMemoryCountAndSum {

    private long count;
    private float sum;
    private float average;
    private String hostAddress;
    private long timestamp;

    public UsedMemoryCountAndSum() {}

    public UsedMemoryCountAndSum(long count, float sum, String hostAddress, long timestamp) {
        this.count = count;
        this.sum = sum;
        this.hostAddress = hostAddress;
        this.timestamp = timestamp;
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

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
