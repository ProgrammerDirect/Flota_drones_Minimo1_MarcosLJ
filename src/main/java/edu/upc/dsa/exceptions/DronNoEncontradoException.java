package edu.upc.dsa.exceptions;

public class DronNoEncontradoException extends Exception
{
    public DronNoEncontradoException(String message)
    {
        super("Dron no encontrado: " + message);
    }
}
