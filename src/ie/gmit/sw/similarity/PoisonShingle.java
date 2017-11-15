package ie.gmit.sw.similarity;

public class PoisonShingle extends Shingle {
    public PoisonShingle(String text, int docId, int number) {
        super(text, docId, number, true);
    }

    @Override
    public String toString(){
        return "PoisonShingle{}";
    }
}
