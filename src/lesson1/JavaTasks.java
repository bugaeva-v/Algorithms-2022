package lesson1;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("unused")
public class JavaTasks {
    /**
     * Сортировка времён
     *
     * Простая
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
     * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
     *
     * Пример:
     *
     * 01:15:19 PM
     * 07:26:57 AM
     * 10:00:03 AM
     * 07:56:14 PM
     * 01:15:19 PM
     * 12:40:31 AM
     *
     * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
     * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
     *
     * 12:40:31 AM
     * 07:26:57 AM
     * 10:00:03 AM
     * 01:15:19 PM
     * 01:15:19 PM
     * 07:56:14 PM
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */
    static public void sortTimes(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Сортировка адресов
     *
     * Средняя
     *
     * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
     * где они прописаны. Пример:
     *
     * Петров Иван - Железнодорожная 3
     * Сидоров Петр - Садовая 5
     * Иванов Алексей - Железнодорожная 7
     * Сидорова Мария - Садовая 5
     * Иванов Михаил - Железнодорожная 7
     *
     * Людей в городе может быть до миллиона.
     *
     * Вывести записи в выходной файл outputName,
     * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
     * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
     *
     * Железнодорожная 3 - Петров Иван
     * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
     * Садовая 5 - Сидоров Петр, Сидорова Мария
     *
     * В случае обнаружения неверного формата файла бросить любое исключение.
     */

    //T = O(n^2)
    //R = O(n)
    static public void sortAddresses(String inputName, String outputName) throws IOException {

        class Address implements Comparable<Address>{
            final String street;
            final Integer h;

            Address(String s, int t) {
                street = s;
                h = t;
            }

            @Override
            public int compareTo(@NotNull Address o) {
                int comp = street.compareTo(o.street);
                if(comp == 0) comp = h.compareTo(o.h);
                return comp;
            }

            @Override
            public String toString() { return street + " " + h + " - "; }
        }

        try( FileReader f  = new FileReader(inputName, StandardCharsets.UTF_8)) {
            BufferedReader read = new BufferedReader(f);
            List<Integer> list = new ArrayList<>();
            String line;
            TreeMap<Address, List<String>> m = new TreeMap<>();
            while ((line = read.readLine()) != null) {
                String[] t = line.split(" +");
                if (t.length != 5 || !t[2].equals("-"))
                    throw new IOException("Неправильный формат строки: \"" + line + "\"\n");
                Address a = new Address(t[3], Integer.parseInt(t[4]));
                m.computeIfAbsent(a, k -> new ArrayList<>()).add(t[0] + " " + t[1]);
            }
            Map.Entry<Address, List<String>> pair;
            try(FileWriter writer = new FileWriter(outputName, StandardCharsets.UTF_8)) {
                while ((pair = m.pollFirstEntry()) != null) {
                    writer.write(pair.getKey().street + " " + pair.getKey().h + " - ");
                    Collections.sort(pair.getValue());
                    writer.write(String.join(", ", pair.getValue()) + "\n");
                }
            }
        }
    }

    /**
     * Сортировка температур
     *
     * Средняя
     * (Модифицированная задача с сайта acmp.ru)
     *
     * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
     * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
     * Например:
     *
     * 24.7
     * -12.6
     * 121.3
     * -98.4
     * 99.5
     * -12.6
     * 11.0
     *
     * Количество строк в файле может достигать ста миллионов.
     * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
     * Повторяющиеся строки сохранить. Например:
     *
     * -98.4
     * -12.6
     * -12.6
     * 11.0
     * 24.7
     * 99.5
     * 121.3
     */

    //T = O(n)
    //R = O(n)
    static public void sortTemperatures(String inputName, String outputName) throws IOException {
        File inp = new File(inputName);
        if(!inp.isFile()) throw new FileNotFoundException("");
        BufferedReader read = new BufferedReader(new FileReader(inp));
        List<Integer> list = new ArrayList<>();
        String line;
        final int MAX = 5000;
        final int MIN = -2730;
        while ((line = read.readLine()) != null)
            list.add((int) ((Double.parseDouble(line)) * 10) - MIN);
        read.close();
        int[] m = new int[list.size()];
        for(int i = 0; i< list.size(); i++)
            m[i] = list.get(i);
        m = Sorts.countingSort(m, MAX - MIN);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
        for (int i : m)
            writer.write((double) (i + MIN) / 10 + "\n");
        writer.close();
    }

    /**
     * Сортировка последовательности
     *
     * Средняя
     * (Задача взята с сайта acmp.ru)
     *
     * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
     *
     * 1
     * 2
     * 3
     * 2
     * 3
     * 1
     * 2
     *
     * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
     * а если таких чисел несколько, то найти минимальное из них,
     * и после этого переместить все такие числа в конец заданной последовательности.
     * Порядок расположения остальных чисел должен остаться без изменения.
     *
     * 1
     * 3
     * 3
     * 1
     * 2
     * 2
     * 2
     */
    static public void sortSequence(String inputName, String outputName) {
        throw new NotImplementedError();
    }

    /**
     * Соединить два отсортированных массива в один
     *
     * Простая
     *
     * Задан отсортированный массив first и второй массив second,
     * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
     * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
     *
     * first = [4 9 15 20 28]
     * second = [null null null null null 1 3 9 13 18 23]
     *
     * Результат: second = [1 3 4 9 9 13 15 20 23 28]
     */
    static <T extends Comparable<T>> void mergeArrays(T[] first, T[] second) {
        throw new NotImplementedError();
    }
}
