package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }
        // Создаём пул потоков по доступному количеству ядер
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        //Список потоков
        List<Future<Integer>> threads = new ArrayList<Future<Integer>>();
        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            //задание для каждого нового потока
            Callable task = () -> {
                int maxSize = 0;
                for (int i = 0; i < text.length(); i++) {
                    for (int j = 0; j < text.length(); j++) {
                        if (i >= j) {
                            continue;
                        }
                        boolean bFound = false;
                        for (int k = i; k < j; k++) {
                            if (text.charAt(k) == 'b') {
                                bFound = true;
                                break;
                            }
                        }
                        if (!bFound && maxSize < j - i) {
                            maxSize = j - i;
                        }
                    }
                }
                return maxSize;
            };
            // Отправляем задачу на выполнение в пул потоков
            Future<Integer> newTask = executor.submit(task);
            threads.add(newTask);//добавляем в массив потоков каждый новый

        }
        // Завершаем работу пула потоков
        executor.shutdown();
        //чтобы приеопать результат работы каждого потока
        List<Integer> maxValues = new ArrayList<>();
        for (Future<Integer> fut : threads) {
            try {
                // складываем результат
                maxValues.add(fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //получаем максимальное значение и выводим на экран
        System.out.println(maxValues.stream().max(Integer::compareTo).get());
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");

    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {

            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}