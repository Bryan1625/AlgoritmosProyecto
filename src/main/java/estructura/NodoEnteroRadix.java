package estructura;
public class NodoEnteroRadix {
    NodoEnteroRadix(int a)
    {

        valor = a;
        siguiente = null;
    }
    NodoEnteroRadix()
    {
        siguiente = null;
        valor = 0;
    }
    public int valor;
    public NodoEnteroRadix siguiente;
}
