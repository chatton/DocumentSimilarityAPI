package ie.gmit.sw.similarity;

import ie.gmit.sw.documents.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JaacardIndex implements SimilarityIndex {

    private final List<Document> documents;
    private final Queue<Shingle> shingles;
    private final Map<Integer, List<Integer>> shingleHashes;
    private final int k;

    public JaacardIndex(final List<Document> documents) {
        this.documents = Collections.unmodifiableList(documents);
        k = 100; // TODO remove hard coded value.
        shingles = new ConcurrentLinkedQueue<>();
        shingleHashes = new ConcurrentHashMap<>();
    }

    private Set<Integer> generateHashes() {
        Random rnd = new Random();
        Set<Integer> hashes = new TreeSet<>();
        while (hashes.size() < k) {
            hashes.add(rnd.nextInt());
        }
        return hashes;
    }

    private void shinglize() {
        for (Document doc : documents) {
            Thread thread = new Thread(new Shinglizer(doc, 10));
            thread.start();
        }
    }

    @Override
    public double computeIndex() {

        // starts 1 thread per document breaking each one into shingles
        shinglize();

        Set<Integer> hashes = generateHashes(); // generates k hash functions

        List<Thread> minHashThreads = new ArrayList<>();
        List<MinHash> minHashTasks = new ArrayList<>();

        for (Document doc : documents) { // each document has it's own k threads
            for (Integer hash : hashes) { // create one thread per hash.
                MinHash minHash = new MinHash(hash, doc.getId());
                minHashTasks.add(minHash); // need to keep task reference to add shingles.
                Thread minHashThread = new Thread(minHash);
                minHashThreads.add(minHashThread);
                minHashThread.start();
            }
        }

        int completedCount = 0;
        while (completedCount != documents.size()) {
            if (!shingles.isEmpty()) {
                Shingle shingle = shingles.poll();
                if (shingle.isPoison()) { // expecting 1 PoisonShingle per document.
                    completedCount++;
                }

                minHashTasks.forEach(minHash -> minHash.addShingle(shingle));
            }
        }
        joinThreads(minHashThreads);


        // TODO calculate JaacardIndex with resulting hash shingles.
        return 0;

    }

    private void joinThreads(List<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class Shinglizer implements Runnable {

        private final Document document;
        private final int shingleLength;

        private Shinglizer(final Document document, final int shingleLength) {
            this.document = document;
            this.shingleLength = shingleLength;
        }

        @Override
        public void run() {
            String text = document.getText().toLowerCase();
            StringBuilder sb = new StringBuilder();
            String[] words = text.split("[ -.,;:\\-]+");
            int pos = 0;
            int num = 1;
            while (pos < words.length) {
                for (int i = 0; i < shingleLength; i++) {
                    if (pos == words.length) {
                        break;
                    }
                    sb.append(words[pos]).append(" ");
                    pos++;
                } // for

                Shingle shingle = new Shingle(sb.toString(), document.getId(), num++);
                shingles.offer(shingle);
                sb = new StringBuilder();
            } // while

            // signal completion
            shingles.offer(new PoisonShingle("Poison", document.getId(), num));
        }
    }

    private class MinHash implements Runnable {

        private final Queue<Shingle> shingleQueue;
        private final int hash;
        private final int docId;
        private int minHash;
        private int totalNumShingles = -1;
        private int numProcessed = 0;

        public void addShingle(Shingle shingle) {
            // ignore any shingles not for this thread.
            if (shingle.getDocId() != docId) {
                return;
            }
            if (shingle.isPoison()) {
                totalNumShingles = shingle.getNumber() - 1;
                return;
            }
            // only deal with threads for the document we care about.
            shingleQueue.offer(shingle);
        }

        public MinHash(final int hash, final int docId) {
            this.docId = docId;
            this.hash = hash;
            minHash = Integer.MAX_VALUE;
            shingleQueue = new ConcurrentLinkedQueue<>();
        }

        private boolean done() {
            if (totalNumShingles == -1) { // haven't found Poison yet, so we don't know how many shingles there will be in total.
                return false;
            }
            return numProcessed == totalNumShingles;
        }

        @Override
        public void run() {
            while (!done()) {
                if (!shingleQueue.isEmpty()) {
                    Shingle next = shingleQueue.poll();
                    numProcessed++;
                    int nextHash = next.hashCode() ^ hash;
                    if (nextHash < minHash) {
                        minHash = nextHash;
                    }
                }
            } // while

            assert minHash != Integer.MAX_VALUE;
            // TODO add the shingle to a list for this document.
        }
    }
}
