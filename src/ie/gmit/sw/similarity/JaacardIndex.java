package ie.gmit.sw.similarity;

import ie.gmit.sw.documents.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class JaacardIndex implements SimilarityIndex {

    private final Document doc1;
    private final Document doc2;
    private final Queue<List<Shingle>> shingles;
    private final Map<Integer, List<Integer>> shingleHashes;
    private final int k;
//    private final ExecutorService service;

    public JaacardIndex(final Document doc1, final Document doc2) {
        this.doc1 = doc1;
        this.doc2 = doc2;
        k = 250;
        shingles = new ConcurrentLinkedQueue<>();
        shingleHashes = new ConcurrentHashMap<>();
//        service = Executors.newFixedThreadPool(8);
    }

//    private List<Shingle> getDocumentShingles(Document document) {
//        return shingles.stream()
//                .filter(shingle -> shingle.getDocId() == document.getId())
//                .collect(Collectors.toList());
//    }

    private Set<Integer> generateHashes() {
        Random rnd = new Random();
        Set<Integer> hashes = new TreeSet<>();
        while (hashes.size() < k) {
            hashes.add(rnd.nextInt());
        }
        return hashes;
    }

    private void shinglize() {
        List<Thread> threads = new ArrayList<>();

        Thread doc1Thread = new Thread(new Shinglizer(doc1, 5));
        Thread doc2Thread = new Thread(new Shinglizer(doc2, 5));

        threads.add(doc1Thread);
        threads.add(doc2Thread);

        doc1Thread.start();
        doc2Thread.start();

        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double computeIndex() {

        shinglize();

        Set<Integer> hashes = generateHashes();
        List<Thread> minHashThreads = new ArrayList<>();
        while (!shingles.isEmpty()) {
            List<Shingle> shingleList = shingles.poll();
            for (Integer hash : hashes) {
                Thread minThread = new Thread(new MinHash(shingleList, hash));
                minHashThreads.add(minThread);
                minThread.start();
            }
        }

        for (Thread thread : minHashThreads) {
            try {
                thread.join(); // all min hashes are done being calculated.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Set<Integer> doc1MinHashes = new HashSet<>(shingleHashes.get(doc1.getId()));
        Set<Integer> doc2MinHashes = new HashSet<>(shingleHashes.get(doc2.getId()));

        doc1MinHashes.retainAll(doc2MinHashes);
        return (double) (doc1MinHashes.size()) / k;

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
            String text = document.getText();
            StringBuilder sb = new StringBuilder();
            List<Shingle> listOfShingles = new ArrayList<>();
            int pos = 0;
            while (pos < text.length()) {
                for (int i = 0; i < shingleLength; i++) {
                    if (pos == text.length()) {
                        break;
                    }
                    sb.append(text.charAt(pos++));
                }
                listOfShingles.add(new Shingle(sb.toString(), document.getId()));
                sb = new StringBuilder();
            }
            shingles.offer(listOfShingles);
        }
    }

    private class MinHash implements Runnable {

        private final List<Shingle> shingles;
        private final int hash;

        public MinHash(final List<Shingle> shingles, final int hash) {
            this.shingles = shingles;
            this.hash = hash;
        }

        private int getMinHash() {
            return shingles.stream()
                    .map(Shingle::getText)
                    .mapToInt(word -> word.hashCode() ^ hash)
                    .min()
                    .orElse(Integer.MAX_VALUE);
        }

        @Override
        public void run() {
            int minHash = getMinHash();
            shingleHashes.computeIfAbsent(shingles.get(0).getDocId(), val -> new ArrayList<>());
            shingleHashes.get(shingles.get(0).getDocId()).add(minHash);
        }
    }

}
