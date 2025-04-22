package estructura;

public class ColaEnlazadaEntero {
    private NodoEnteroRadix inicio;
    private NodoEnteroRadix fin;
    private int tamano;
    public ColaEnlazadaEntero()
    {
        inicio = null;
        fin = null;
        tamano = 0;
    }
    public void encolar(int num)
    {
        tamano++;
        NodoEnteroRadix temp= new NodoEnteroRadix(num);
        if (inicio == null)
        {
            inicio = temp;
            fin = inicio;
        }
        else
        {
            fin.siguiente = temp;
            fin = temp;

        }
        temp = null;
    }
    public int decolar()
    {
        tamano--;
        int temp = inicio.valor;
        NodoEnteroRadix nodoTemp;
        nodoTemp = inicio;
        inicio = inicio.siguiente;
        nodoTemp = null;
        return temp;
    }
    public boolean estaVacia()
    {
        return (tamano == 0);
    }
}