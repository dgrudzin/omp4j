package ru.unn.omp4j.directives.workshare;

public class Chunk {

    private int firstIndex;
    private int lastIndex;
    private int step;

    public Chunk(int firstIndex, int lastIndex, int step) {
        super();
        this.firstIndex = firstIndex;
        this.lastIndex = lastIndex;
        this.step = step;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public int getStep() {
        return step;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("FirstIndex = ").append(firstIndex).append("; LastIndex = " + lastIndex)
                .append("; Step = ").append(step).toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + firstIndex;
        result = prime * result + lastIndex;
        result = prime * result + step;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Chunk other = (Chunk) obj;
        if (firstIndex != other.firstIndex)
            return false;
        if (lastIndex != other.lastIndex)
            return false;
        if (step != other.step)
            return false;
        return true;
    }
}
