package util;

import estructura.ColaEnlazadaEntero;

public class RadixSortVarEntero {
    private ColaEnlazadaEntero[] Q =
            {
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero(),
                    new ColaEnlazadaEntero()
            };
    void sort(int[] a)
    {
        int max =0, maxIndex = 0, numeroDigitos;
        for (int i = 0; i < a.length; i++)
        {
            if (max < a[i])
            {
                max = a[i];
                maxIndex = i;
            }
        }
        numeroDigitos =
                String.valueOf(max).length();
        sort(a, numeroDigitos);
    }
    void sort(int[] a, int numeroDigitos)
    {
        int posArreglo;
        for (int i = 1; i<= numeroDigitos; i++)
        {
            posArreglo = 0;
            for (int j = 0; j < a.length; j++)
            {
                Q[obtenerRadical(a[j], i)].
                        encolar(a[j]);

            }
            for (int j = 0; j < Q.length; j++)
            {
                while (!Q[j].estaVacia())
                {
                    a[posArreglo] = Q[j].decolar();
                    posArreglo++;
                }
            }
        }
    }
    int obtenerRadical(int numero,int radical)
    {
        return (int)
                (numero/Math.pow(10, radical-1)) % 10;
    }
}