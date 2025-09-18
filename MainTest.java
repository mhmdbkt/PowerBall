import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class MainTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /** helper: build a ticket with first 5 auto-sorted (like your code) */
    private static int[] ticket(int a, int b, int c, int d, int e, int pb) {
        int[] t = new int[] { a, b, c, d, e, pb };
        Arrays.sort(t, 0, 5);
        return t;
    }

    /* -------------------------
       generateTicket() tests
       ------------------------- */

    @RepeatedTest(40)
    void generateTicket_respectsRanges_sorted_unique() {
        int[] t = Main.generateTicket();
        assertEquals(6, t.length);

        // first five in 1..69 and sorted strictly (your code ensures uniqueness)
        for (int i = 0; i < 5; i++) {
            assertTrue(t[i] >= 1 && t[i] <= 69);
            if (i > 0) {
                assertTrue(t[i] > t[i - 1], "first five must be strictly increasing");
            }
        }
        // powerball in 1..26
        assertTrue(t[5] >= 1 && t[5] <= 26);
    }

    /* -------------------------
       printTicket() formatting
       ------------------------- */

    @Test
    void printTicket_formatsAsExpected() {
        int[] t = ticket(5, 10, 15, 20, 25, 7);
        Main.printTicket("TEST Ticket", t);
        String s = out.toString();
        assertTrue(s.contains("TEST Ticket: "));
        assertTrue(s.contains("5 10 15 20 25 "));
        assertTrue(s.contains("|| Powerball: 7"));
    }

    /* ------------------------------------
       checkWin() – cover EVERY prize tier
       ------------------------------------ */

    @Test
    void jackpot_5PlusPB_printsJackpot_returnsZero() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,3,4,5,6);
        int prize = Main.checkWin(usr, win);
        String s = out.toString();
        assertTrue(s.toLowerCase().contains("jackpot"));
        assertEquals(0, prize, "Your code prints jackpot and keeps prize variable at 0");
    }

    @Test
    void fiveOnly_1Million() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,3,4,5,26);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$1000000"));
        assertEquals(1_000_000, prize);
    }

    @Test
    void fourPlusPB_50000() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,3,4,69,6);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$50000"));
        assertEquals(50_000, prize);
    }

    @Test
    void fourOnly_100() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,3,4,69,26);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$100"));
        assertEquals(100, prize);
    }

    @Test
    void threePlusPB_100() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,3,70,71,6);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$100"));
        assertEquals(100, prize);
    }

    @Test
    void threeOnly_7() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,3,70,71,26);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$7"));
        assertEquals(7, prize);
    }

    @Test
    void twoPlusPB_7() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,2,70,71,72,6);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$7"));
        assertEquals(7, prize);
    }

    @Test
    void onePlusPB_4() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(1,70,71,72,73,6);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$4"));
        assertEquals(4, prize);
    }

    @Test
    void zeroPlusPB_4() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(10,20,30,40,50,6);
        int prize = Main.checkWin(usr, win);
        assertTrue(out.toString().contains("$4"));
        assertEquals(4, prize);
    }

    @Test
    void totalLoss_printsNothing_returnsZero() {
        int[] win = ticket(1,2,3,4,5,6);
        int[] usr = ticket(10,20,30,40,50,26); // no PB, <3 matches
        out.reset();
        int prize = Main.checkWin(usr, win);
        assertEquals("", out.toString(), "Your code prints only winners; losers print nothing");
        assertEquals(0, prize);
    }
}
