package edu.upc.dsa.exceptions;

public class PilotoNoEncontradoException extends Exception
{
    public PilotoNoEncontradoException(String message)
    {
        super("Piloto no encontrado: " + message);
    }
}
