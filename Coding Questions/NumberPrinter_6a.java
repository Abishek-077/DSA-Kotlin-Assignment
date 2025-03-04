/*
 * Question 6
a)
You are given a class NumberPrinter with three methods: printZero, printEven, and printOdd.
These methods are designed to print the numbers 0, even numbers, and odd numbers, respectively.
Task:
Create a ThreadController class that coordinates three threads:
5. ZeroThread: Calls printZero to print 0s.
6. EvenThread: Calls printEven to print even numbers.
7. OddThread: Calls printOdd to print odd numbers.
These threads should work together to print the sequence "0102030405..." up to a specified number n.
The output should be interleaved, ensuring that the numbers are printed in the correct order.
Example:
If n = 5, the output should be "0102030405".
Constraints:
 The threads should be synchronized to prevent race conditions and ensure correct output.
 The NumberPrinter class is already provided and cannot be modified.
 */
import java.util.concurrent.Semaphore;

// Provided class that cannot be modified
class NumberPrinter_6a {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int num) {
        System.out.print(num);
    }

    public void printOdd(int num) {
        System.out.print(num);
    }
}

// ThreadController to manage synchronization
class ThreadController {
    private int n;
    private NumberPrinter_6a printer;

    private Semaphore zeroSem = new Semaphore(1);  // Initially available for 0
    private Semaphore evenSem = new Semaphore(0);  // Block even number printing
    private Semaphore oddSem = new Semaphore(0);   // Block odd number printing

    public ThreadController(int n, NumberPrinter_6a printer) {
        this.n = n;
        this.printer = printer;
    }

    public void printZero() throws InterruptedException {
        for (int i = 1; i <= n; i++) {
            zeroSem.acquire();
            printer.printZero();
            if (i % 2 == 0) {
                evenSem.release(); // Allow even thread to print
            } else {
                oddSem.release(); // Allow odd thread to print
            }
        }
    }

    public void printEven() throws InterruptedException {
        for (int i = 2; i <= n; i += 2) {
            evenSem.acquire();
            printer.printEven(i);
            zeroSem.release(); // Allow zero thread to print
        }
    }

    public void printOdd() throws InterruptedException {
        for (int i = 1; i <= n; i += 2) {
            oddSem.acquire();
            printer.printOdd(i);
            zeroSem.release(); // Allow zero thread to print
        }
    }
}

// Main class to start the threads
class Main {
    public static void main(String[] args) {
        int n = 5;
        NumberPrinter_6a printer = new NumberPrinter_6a();
        ThreadController controller = new ThreadController(n, printer);

        Thread zeroThread = new Thread(() -> {
            try {
                controller.printZero();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread evenThread = new Thread(() -> {
            try {
                controller.printEven();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread oddThread = new Thread(() -> {
            try {
                controller.printOdd();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        zeroThread.start();
        evenThread.start();
        oddThread.start();

        try {
            zeroThread.join();
            evenThread.join();
            oddThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
