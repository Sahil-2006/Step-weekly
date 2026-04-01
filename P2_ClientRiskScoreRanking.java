import java.util.Arrays;

/**
 * Problem 2: Client Risk Score Ranking
 */
class Client {
    String name;
    int riskScore;
    double accountBalance;

    public Client(String name, int riskScore, double accountBalance) {
        this.name = name;
        this.riskScore = riskScore;
        this.accountBalance = accountBalance;
    }

    @Override
    public String toString() {
        return name + ":" + riskScore;
    }
}

public class P2_ClientRiskScoreRanking {

    public static void bubbleSort(Client[] clients) {
        int n = clients.length;
        int swaps = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (clients[j].riskScore > clients[j+1].riskScore) {
                    Client temp = clients[j];
                    clients[j] = clients[j+1];
                    clients[j+1] = temp;
                    swaps++;
                }
            }
        }
        System.out.println("Bubble (asc): " + Arrays.toString(clients) + " // Swaps: " + swaps);
    }

    public static void insertionSort(Client[] clients) {
        int n = clients.length;
        for (int i = 1; i < n; i++) {
            Client key = clients[i];
            int j = i - 1;
            // DESC Sort by riskScore
            while (j >= 0 && (clients[j].riskScore < key.riskScore)) {
                clients[j + 1] = clients[j];
                j--;
            }
            clients[j + 1] = key;
        }
        System.out.println("Insertion (desc): " + Arrays.toString(clients));
    }

    public static void main(String[] args) {
        Client[] data = {
            new Client("clientC", 80, 10000),
            new Client("clientA", 20, 5000),
            new Client("clientB", 50, 7000)
        };

        System.out.println("Input: " + Arrays.toString(data));
        
        Client[] ascData = data.clone();
        bubbleSort(ascData);

        Client[] descData = data.clone();
        insertionSort(descData);

        System.out.print("Top 3 risks: ");
        for (int i = 0; i < Math.min(3, descData.length); i++) {
            System.out.print(descData[i].name + "(" + descData[i].riskScore + ")");
            if (i < 2) System.out.print(", ");
        }
        System.out.println();
    }
}
