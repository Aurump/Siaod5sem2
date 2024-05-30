import javax.crypto.spec.PSource;
import java.io.*;
import java.util.*;
public class Main1 {
        private static Map<Character, Integer> frequencyMap = new HashMap<>();
        private static Map<Character, String> fixedLengthCodes = new HashMap<>();
        private static Map<Character, String> huffmanCodes = new HashMap<>();
        private static Map<Character, Integer> sortedFrequencyMap = new LinkedHashMap<>();
        static Scanner scanner = new Scanner(System.in);
        public static void main(String[] args) {

            String choice = null;
            boolean tr=true;
            while (tr) {
                System.out.println("Меню:");
                System.out.println("1. Открыть текстовый файл");
                System.out.println("2. Показать содержимое текстового файла");
                System.out.println("3. Показать алфавитные символы с их частотами");
                System.out.println("4. Сгенерировать коды для алфавитных символов");
                System.out.println("4.1 Показать алфавит с кодами фиксированной длины");
                System.out.println("4.2 Показать алфавит с кодами Хаффмана");
                System.out.println("5. Сжать текстовый файл с кодами фиксированной длины");
                System.out.println("6. Сжать текстовый файл с кодами Хаффмана");
                System.out.println("7. Сравнить размеры файлов");
                System.out.println("8. Выход");
                System.out.print("Введите ваш выбор: ");

                choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        openTextFile();
                        break;
                    case "2":
                        displayTextFileContent();
                        break;
                    case "3":
                        displayAlphabetWithFrequencies();
                        break;
                    case "4":
                        generateCodesForAlphabet();
                        break;
                    case "4.1":
                        displayAlphabetWithFixedLengthCodes();
                        break;
                    case "4.2":
                        displayAlphabetWithHuffmanCodes();
                        break;
                    case "5":
                        compressWithFixedLengthCodes();
                        break;
                    case "6":
                        compressWithHuffmanCodes();
                        break;
                    case "7":
                        compareFileSizes();
                        break;
                    case "8":
                        tr=false;
                        System.out.println("Завершение программы");
                        break;
                    default:
                        System.out.println("Неверный выбор");
                        break;
                }
            }
        }

        private static void openTextFile() {
            try {
                BufferedReader br = new BufferedReader(new FileReader("input.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    for (char c : line.toCharArray()) {
                        if (Character.isLetter(c)) {
                            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
                        }
                    }
                }
                br.close();
                System.out.println("Текстовый файл успешно открыт");
            } catch (IOException e) {
                System.out.println("Ошибка");
            }
        }

        private static void displayTextFileContent() {
            try {
                BufferedReader br = new BufferedReader(new FileReader("input.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                br.close();
            } catch (IOException e) {
                System.out.println("Ошибка");
            }
        }

        private static void displayAlphabetWithFrequencies() {
            sortedFrequencyMap.clear();
            frequencyMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(entry -> sortedFrequencyMap.put(entry.getKey(), entry.getValue()));
            sortedFrequencyMap.forEach((key, value) -> System.out.println(key + " - " + value));
        }

        private static void generateCodesForAlphabet() {
            fixedLengthCodes.clear();
            huffmanCodes.clear();
                // Генерация кодов фиксированной длины
                int code = 0;
                for (char c : frequencyMap.keySet()) {
                    fixedLengthCodes.put(c, String.format("%03d", Integer.parseInt(Integer.toBinaryString(code))));
                    code++;
                }

            // Генерация кодов Хаффмана
            List<Node> nodes = new ArrayList<>();
            for (char c : frequencyMap.keySet()) {
                nodes.add(new Node(c, frequencyMap.get(c)));
            }

            PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(n -> n.frequency));
            priorityQueue.addAll(nodes);

            while (priorityQueue.size() > 1) {
                Node left = priorityQueue.poll();
                Node right = priorityQueue.poll();
                Node parent = new Node('X', left.frequency + right.frequency);
                parent.left = left;
                parent.right = right;
                priorityQueue.add(parent);
            }

            Node root = priorityQueue.poll();
            generateHuffmanCodes(root, "");
        }

        private static void generateHuffmanCodes(Node node, String code) {
            if (node.left == null && node.right == null) {
                huffmanCodes.put(node.character, code);
                return;
            }

            generateHuffmanCodes(node.left, code + "0");
            generateHuffmanCodes(node.right, code + "1");
        }

        private static void displayAlphabetWithFixedLengthCodes() {
            fixedLengthCodes.forEach((key, value) -> System.out.println(key + " - " + value));
        }

        private static void displayAlphabetWithHuffmanCodes() {
            huffmanCodes.forEach((key, value) -> System.out.println(key + " - " + value));
        }

        private static void compressWithFixedLengthCodes() {
            try {
                BufferedReader br = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("compressedfixedlength.bin"));
                String line;
                while ((line = br.readLine()) != null) {
                    for (char c : line.toCharArray()) {
                        if (fixedLengthCodes.containsKey(c)) {
                            bw.write(fixedLengthCodes.get(c));
                        }
                    }
                }
                br.close();
                bw.close();
                System.out.println("Файл сжат с кодами фиксированной длины");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void compressWithHuffmanCodes() {
            try {
                BufferedReader br = new BufferedReader(new FileReader("input.txt"));
                BufferedWriter bw = new BufferedWriter(new FileWriter("compressedhuffman.bin"));
                String line;
                while ((line = br.readLine()) != null) {
                    for (char c : line.toCharArray()) {
                        if (huffmanCodes.containsKey(c)) {
                            bw.write(huffmanCodes.get(c));
                        }
                    }
                }
                br.close();
                bw.close();
                System.out.println("Файл сжат с кодами Хаффмана");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static void compareFileSizes() {
            File inputFile = new File("input.txt");
            File fixedLengthFile = new File("compressedfixedlength.bin");
            File huffmanFile = new File("compressedhuffman.bin");

            long inputSize = inputFile.length();
            long fixedLengthSize = fixedLengthFile.length();
            long huffmanSize = huffmanFile.length();

            System.out.println("Размер исходного файла: " + inputSize*8 + " байт");
            System.out.println("Размер сжатого файла с кодами фиксированной длины: " + fixedLengthSize + " байт");
            System.out.println("Размер сжатого файла с кодами Хаффмана: " + huffmanSize + " байт");
        }

        static class Node {
            char character;
            int frequency;
            Node left;
            Node right;
            Node(char character, int frequency) {
                this.character = character;
                this.frequency = frequency;
            }
        }
}
