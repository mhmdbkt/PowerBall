import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    // Generate random ticket (used for both user random tickets & winning ticket)
    public static int[] generateTicket() {
        int[] LotteryTicket = new int[6];

        int count = 0;
        while (count < 5) {
            int candidate = ThreadLocalRandom.current().nextInt(1, 70); // 1–69
            boolean exists = false;
            for (int i = 0; i < count; i++) {
                if (LotteryTicket[i] == candidate) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                LotteryTicket[count] = candidate;
                count++;
            }
        }
        Arrays.sort(LotteryTicket, 0, 5);
        LotteryTicket[5] = ThreadLocalRandom.current().nextInt(1, 27); // 1–26

        return LotteryTicket;
    }

    // Print ticket in nice format
    public static void printTicket(String label, int[] ticket) {
        System.out.print(label + ": ");
        for (int i = 0; i < 5; i++) {
            System.out.print(ticket[i] + " ");
        }
        System.out.println("|| Powerball: " + ticket[5]);
    }

    // User picks ticket manually
    public static int[] userPick(Scanner sc) {
        int[] LotteryTicket = new int[6];

        System.out.println("Enter 5 unique numbers between 1 and 69:");
        for (int i = 0; i < 5; i++) {
            int num;
            while (true) {
                System.out.print("Pick number " + (i + 1) + ": ");
                num = sc.nextInt();

                if (num < 1 || num > 69) {
                    System.out.println("❌ Number must be between 1 and 69.");
                    continue;
                }
                boolean exists = false;
                for (int j = 0; j < i; j++) {
                    if (LotteryTicket[j] == num) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    System.out.println("❌ You already picked that number.");
                } else {
                    break;
                }
            }
            LotteryTicket[i] = num;
        }

        Arrays.sort(LotteryTicket, 0, 5);

        while (true) {
            System.out.print("Enter your Powerball (1–26): ");
            int special = sc.nextInt();
            if (special >= 1 && special <= 26) {
                LotteryTicket[5] = special;
                break;
            } else {
                System.out.println("❌ Powerball must be between 1 and 26.");
            }
        }
        return LotteryTicket;
    }

    // Compare and print result
    // Compare and only DISPLAY winning tickets
    public static int checkWin(int[] userTicket, int[] winningTicket) {
        int matches = 0;

        // Count matches among the first 5 (both arrays have their first 5 sorted)
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (userTicket[i] == winningTicket[j]) {
                    matches++;
                    break;
                }
            }
        }

        boolean powerballMatch = (userTicket[5] == winningTicket[5]);

        int prize = 0;
        boolean jackpot = false;

        if (matches == 5 && powerballMatch) {
            jackpot = true;                      // jackpot is variable; print message only
        } else if (matches == 5) {
            prize = 1_000_000;
        } else if (matches == 4 && powerballMatch) {
            prize = 50_000;
        } else if (matches == 4) {
            prize = 100;
        } else if (matches == 3 && powerballMatch) {
            prize = 100;
        } else if (matches == 3) {
            prize = 7;
        } else if (matches == 2 && powerballMatch) {
            prize = 7;
        } else if (matches == 1 && powerballMatch) {
            prize = 4;
        } else if (matches == 0 && powerballMatch) {
            prize = 4;
        }

        // ✅ Only print winners (any prize > 0 or jackpot)
        if (jackpot || prize > 0) {
            printTicket("🎟️ Winning Ticket", userTicket);
            System.out.println("Matched " + matches + " numbers.");
            System.out.println(powerballMatch ? "✅ Powerball matched!" : "❌ Powerball did not match.");
            if (jackpot) {
                System.out.println("💰 Prize Won: 🏆 GRAND PRIZE JACKPOT! (amount varies)\n");
            } else {
                System.out.println("💰 Prize Won: $" + prize + "\n");
            }
        }

        return prize; // return so you can tally totals later if you want
    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        boolean playAgain = true;
        int totalSpent = 0;
        int totalWon = 0;

        while (playAgain) {
            // Generate the ONE winning ticket for this round
            int[] winningTicket = generateTicket();
            printTicket("💻 Winning Numbers", winningTicket);

            System.out.print("Choose option:\n1️⃣ Pick your own numbers\n2️⃣ Buy X random tickets\nEnter choice: ");
            int choice = sc.nextInt();

            if (choice == 1) {
                int[] userTicket = userPick(sc);
                totalSpent += 2; // one ticket cost
                int prize = checkWin(userTicket, winningTicket);
                totalWon += prize;

            } else if (choice == 2) {
                System.out.print("How many random tickets do you want to buy? ");
                int x = sc.nextInt();
                totalSpent += x * 2; // each ticket costs $2
                for (int i = 1; i <= x; i++) {
                    int[] userTicket = generateTicket();
                    int prize = checkWin(userTicket, winningTicket);
                    totalWon += prize;
                }
            } else {
                System.out.println("❌ Invalid choice.");
            }

            System.out.print("\nPlay again? (y/n): ");
            playAgain = sc.next().equalsIgnoreCase("y");
        }

        int totalLost = totalSpent - totalWon;

        System.out.println("\n===== SESSION SUMMARY =====");
        System.out.println("💵 Total Spent: $" + totalSpent);
        System.out.println("💰 Total Won: $" + totalWon);
        System.out.println("📉 Total Lost: $" + totalLost);

        System.out.println("👋 Thanks for playing!");
        sc.close();
    }

}
