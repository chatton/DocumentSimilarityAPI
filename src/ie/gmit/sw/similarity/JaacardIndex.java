package ie.gmit.sw.similarity;

import ie.gmit.sw.documents.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class JaacardIndex implements SimilarityIndex {

    private final Document doc1;
    private final Document doc2;
    private final Queue<Shingle> shingles;
    private final Map<Integer, List<Integer>> shingleHashes;
    private final int k;
//    private final ExecutorService service;

    public JaacardIndex(final Document doc1, final Document doc2) {
        this.doc1 = doc1;
        this.doc2 = doc2;
        k = 100;
        shingles = new ConcurrentLinkedQueue<>();
        shingleHashes = new ConcurrentHashMap<>();
//        service = Executors.newFixedThreadPool(8);
    }

    private List<Shingle> getDocumentShingles(Document document) {
        return shingles.parallelStream()
                .filter(shingle -> shingle.getDocId() == document.getId())
                .collect(Collectors.toList());
    }

    private Set<Integer> generateHashes() {
        Random rnd = new Random();
        Set<Integer> hashes = new HashSet<>();
        while (hashes.size() < k) {
            hashes.add(rnd.nextInt());
        }
        return Collections.unmodifiableSet(hashes);
    }

    private int calculateShingleMinHash(Shingle shingle, int hash) {
        return shingle.getWords()
                .parallelStream()
                .mapToInt(word -> word.hashCode() ^ hash)
                .min()
                .orElse(Integer.MAX_VALUE);
    }


    private void shinglize() {
        Thread doc1Thread = new Thread(new Shinglizer(doc1, 5));
        Thread doc2Thread = new Thread(new Shinglizer(doc2, 5));

        doc1Thread.start();
        doc2Thread.start();

        try {
            doc1Thread.join();
            doc2Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double computeIndex() {

        shinglize();
        System.out.println(getDocumentShingles(doc1).size());
        System.out.println(getDocumentShingles(doc2).size());


        Set<Integer> hashes = generateHashes();
        List<Thread> minHashThreads = new ArrayList<>();
        System.out.println(shingles.size());

        for(Integer hash : hashes){
            Shingle shingle = shingles.poll();

            Thread minHashThread = new Thread(new MinHash(shingle, hash));
            minHashThreads.add(minHashThread);
            minHashThread.start();
        }

        for(Thread thread : minHashThreads){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private class Shinglizer implements Runnable {

        private final Document document;
        private final int numWords;

        private Shinglizer(final Document document, final int numWords) {
            this.document = document;
            this.numWords = numWords;
        }

        @Override
        public void run() {
            String text = document.getText();
            String[] words = text.split("[\\- ,;.]+");

            for (int i = 0; i < words.length; i++) {
                List<String> oneShingle = new ArrayList<>();
                for (int j = 0; j < numWords; j++) {
                    if (i == words.length) {
                        break;
                    }
                    oneShingle.add(words[i++].toLowerCase());
                }
                shingles.offer(new Shingle(oneShingle, document.getId()));
            }
        }
    }

    private class MinHash implements Runnable {

        private final Shingle shingle;
        private final int hash;

        public MinHash(final Shingle shingle, final int hash) {
            this.shingle = shingle;
            this.hash = hash;
        }

        private int getMinHash() {
            return shingle.getWords()
                    .parallelStream()
                    .mapToInt(word -> word.hashCode() ^ hash)
                    .min()
                    .orElse(Integer.MAX_VALUE);
        }

        @Override
        public void run() {
            int minHash = getMinHash();
            shingleHashes.computeIfAbsent(shingle.getDocId(), val -> new ArrayList<>());
            shingleHashes.get(shingle.getDocId()).add(minHash);
        }
    }

}
