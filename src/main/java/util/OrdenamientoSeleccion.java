package util;

public class OrdenamientoSeleccion {

    public void ordenar(int arreglo[] )
    {

        int i, j, k, menor;
        i = 0;
        while( i < arreglo.length - 1)
        {

            menor = arreglo [i];
            k = i;
            for( j = i+1; j < arreglo.length; j++){

                if (arreglo [j] < menor ){
                    menor = arreglo [j];

                    k = j;

                }
            }
            arreglo [k] = arreglo[i];
            arreglo [i] = menor;
            i++;
        }
    }
}
