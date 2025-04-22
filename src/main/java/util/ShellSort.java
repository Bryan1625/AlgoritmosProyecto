package util;

public class ShellSort {

    public void ordenar(int a[]) {
        for (int incr = a.length / 2; incr > 0; incr /= 2) {

            for (int i = incr; i < a.length; i++) {

                int j = i - incr;
                while (j >= 0) {

                    if (a[j] > a[j + incr]) {
                        int T = a[j];
                        a[j] = a[j + incr];
                        a[j + incr] = T;
                        j -= incr;

                    } else {

                        j = -1;
                    }
                }
            }
        }
    }
}
